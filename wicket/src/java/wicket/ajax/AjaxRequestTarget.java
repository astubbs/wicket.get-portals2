/*
 * $Id$
 * $Revision$ $Date$
 * 
 * ==============================================================================
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package wicket.ajax;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import wicket.Application;
import wicket.Component;
import wicket.IRequestTarget;
import wicket.Page;
import wicket.RequestCycle;
import wicket.Response;
import wicket.protocol.http.WebResponse;

/**
 * A request target that produces ajax response envelopes used on the client
 * side to update component markup as well as evaluate arbitrary javascript.
 * <p>
 * A component whose markup needs to be updated should be added to this target
 * via AjaxRequestTarget#addComponent(Component) method. Its body will be
 * rendered and added to the envelope when the target is processed, and
 * refreshed on the client side when the ajax response is received.
 * <p>
 * It is important that the component whose markup needs to be updated contains
 * an id attribute in the generated markup that is equal to the value retrieved
 * from Component#getMarkupId(). This can be accomplished by either setting the
 * id attribute in the html template, or using an attribute modifier that will
 * add the attribute with value Component#getMarkupId() to the tag ( such as
 * MarkupIdSetter )
 * <p>
 * Any javascript that needs to be evaluater on the client side can be added
 * using AjaxRequestTarget#addJavascript(String). For example, this feature can
 * be useful when it is desirable to link component update with some javascript
 * effects.
 * 
 * @author Igor Vaynberg (ivaynberg)
 */
public class AjaxRequestTarget implements IRequestTarget
{
	/** the component instances that will be rendered */
	private final List/* <Component> */components = new ArrayList();

	private final List/* <String> */javascripts = new ArrayList();

	/**
	 * Constructor
	 */
	public AjaxRequestTarget()
	{
	}

	/**
	 * Adds a component to the list of components to be rendered
	 * 
	 * @param component
	 *            component to be rendered
	 */
	public final void addComponent(Component component)
	{
		if (component == null)
		{
			throw new IllegalArgumentException("component cannot be null");
		}
		else if (component instanceof Page)
		{
			throw new IllegalArgumentException("component cannot be a page");
		}

		components.add(component);
	}

	/**
	 * Adds javascript that will be evaluated on the client side
	 * 
	 * @param javascript
	 */
	public final void addJavascript(String javascript)
	{
		if (javascript == null)
		{
			throw new IllegalArgumentException("javascript cannot be null");
		}

		javascripts.add(javascript);
	}

	/**
	 * @see wicket.IRequestTarget#respond(wicket.RequestCycle)
	 */
	public final void respond(final RequestCycle requestCycle)
	{
		final Application app = Application.get();

		// disable component use check since we want to ignore header contribs
		final boolean oldUseCheck = app.getDebugSettings().getComponentUseCheck();
		app.getDebugSettings().setComponentUseCheck(false);

		// Determine encoding
		final String encoding = app.getRequestCycleSettings().getResponseRequestEncoding();

		// Set content type based on markup type for page
		WebResponse response = (WebResponse)requestCycle.getResponse();
		response.setCharacterEncoding(encoding);
		response.setContentType("text/xml; charset=" + encoding);

		// Make sure it is not cached by a
		response.setHeader("Expires", "Mon, 26 Jul 1997 05:00:00 GMT");
		response.setHeader("Cache-Control", "no-cache, must-revalidate");
		response.setHeader("Pragma", "no-cache");

		response.write("<?xml version=\"1.0\" encoding=\"" + encoding + "\"?>");
		response.write("<ajax-response>");

		Iterator it = components.iterator();
		while (it.hasNext())
		{
			Component c = (Component)it.next();
			respondComponent(response, c);
		}

		it = javascripts.iterator();
		while (it.hasNext())
		{
			String js = (String)it.next();
			respondInvocation(response, js);
		}
		response.write("</ajax-response>");

		// restore component use check
		app.getDebugSettings().setComponentUseCheck(oldUseCheck);
	}

	/**
	 * @param response
	 * @param js
	 */
	private void respondInvocation(final Response response, final String js)
	{
		response.write("<evaluate>");
		response.write("<![CDATA[");
		response.write(js);
		response.write("]]>");
		response.write("</evaluate>");
	}

	/**
	 * 
	 * @param response
	 * @param component
	 */
	private void respondComponent(final Response response, final Component component)
	{
		String id=component.getMarkupId();

		response.write("<component id=\"" + id + "\">");

		response.write("<![CDATA[");

		// Initialize temporary variables
		Page page = component.getPage();
		if (page != null)
		{
			page.startComponentRender(component);
		}

		boolean old = component.getRenderBodyOnly();
		component.setRenderBodyOnly(true);

		// Render the component
		component.doRender();

		component.setRenderBodyOnly(old);

		if (page != null)
		{
			page.endComponentRender(component);
		}

		response.write("]]>");

		response.write("</component>");
	}

	/**
	 * @see wicket.IRequestTarget#cleanUp(wicket.RequestCycle)
	 */
	public void cleanUp(final RequestCycle requestCycle)
	{
	}

	/**
	 * @see wicket.IRequestTarget#getLock(RequestCycle)
	 */
	public Object getLock(final RequestCycle requestCycle)
	{
		return requestCycle.getSession();
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(final Object obj)
	{
		if (obj instanceof AjaxRequestTarget)
		{
			AjaxRequestTarget that = (AjaxRequestTarget)obj;
			return components.equals(that.components) && javascripts.equals(that.javascripts);
		}
		return false;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode()
	{
		int result = "AjaxRequestTarget".hashCode();
		result += components.hashCode() * 17;
		result += javascripts.hashCode() * 17;
		return result;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		return "[AjaxRequestTarget@" + hashCode() + " components [" + components + "], javascript ["
				+ javascripts + "]";
	}
}