/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package wicket.markup.html.internal;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import wicket.RequestCycle;
import wicket.ResourceReference;
import wicket.Response;
import wicket.markup.html.IHeaderResponse;
import wicket.markup.html.WicketEventReference;
import wicket.util.string.JavascriptUtils;

/**
 * Default implementation of the {@link IHeaderResponse} interface.
 * 
 * @author Matej Knopp
 * @author Igor Vaynberg (ivaynberg)
 */
public class HeaderResponse implements IHeaderResponse
{
	private static final long serialVersionUID = 1L;

	private Response response;

	private Set rendered = new HashSet();

	/**
	 * Creates a new header response instance.
	 * 
	 * @param response
	 *            response used to write the head elements
	 */
	public HeaderResponse(Response response)
	{
		this.response = response;
	}

	/**
	 * @see wicket.markup.html.IHeaderResponse#markRendered(java.lang.Object)
	 */
	public final void markRendered(Object object)
	{
		rendered.add(object);
	}

	/**
	 * @see wicket.markup.html.IHeaderResponse#renderCSSReference(wicket.markup.html.ResourceReference)
	 */
	public final void renderCSSReference(ResourceReference reference)
	{
		CharSequence url = RequestCycle.get().urlFor(reference);
		renderCSSReference(url.toString(), null);
	}

	/**
	 * @see wicket.markup.html.IHeaderResponse#renderCSSReference(wicket.ResourceReference,
	 *      java.lang.String)
	 */
	public void renderCSSReference(ResourceReference reference, String media)
	{
		CharSequence url = RequestCycle.get().urlFor(reference);
		renderCSSReference(url.toString(), media);
	}

	/**
	 * @see wicket.markup.html.IHeaderResponse#renderCSSReference(java.lang.String)
	 */
	public void renderCSSReference(String url)
	{
		renderCSSReference(url, null);
	}

	/**
	 * @see wicket.markup.html.IHeaderResponse#renderCSSReference(java.lang.String,
	 *      java.lang.String)
	 */
	public void renderCSSReference(String url, String media)
	{
		List token = Arrays.asList(new Object[] { "css", url, media });
		if (wasRendered(token) == false)
		{
			response.write("<link rel=\"stylesheet\" type=\"text/css\" href=\"");
			response.write(url);
			response.write("\"");
			if (media != null)
			{
				response.write(" media=\"");
				response.write(media);
				response.write("\"");
			}
			response.println(" />");
			markRendered(token);
		}
	}

	/**
	 * @see wicket.markup.html.IHeaderResponse#renderJavascriptReference(wicket.markup.html.ResourceReference)
	 */
	public final void renderJavascriptReference(ResourceReference reference)
	{
		CharSequence url = RequestCycle.get().urlFor(reference);
		renderJavascriptReference(url.toString());
	}

	/**
	 * @see wicket.markup.html.IHeaderResponse#renderJavascriptReference(java.lang.String)
	 */
	public final void renderJavascriptReference(String url)
	{
		List token = Arrays.asList(new Object[] { "javascript", url });
		if (wasRendered(token) == false)
		{
			JavascriptUtils.writeJavascriptUrl(getResponse(), url);
			markRendered(token);
		}
	}


	/**
	 * @see wicket.markup.html.IHeaderResponse#renderJavascript(java.lang.CharSequence,
	 *      java.lang.String)
	 */
	public void renderJavascript(CharSequence javascript, String id)
	{
		List token = Arrays.asList(new Object[] { javascript, id });
		if (wasRendered(token) == false)
		{
			JavascriptUtils.writeJavascript(getResponse(), javascript, id);
			markRendered(token);
		}
	}

	/**
	 * @see wicket.markup.html.IHeaderResponse#renderString(java.lang.CharSequence)
	 */
	public final void renderString(CharSequence string)
	{
		if (wasRendered(string) == false)
		{
			getResponse().write(string);
			markRendered(string);
		}
	}

	/**
	 * @see wicket.markup.html.IHeaderResponse#wasRendered(java.lang.Object)
	 */
	public final boolean wasRendered(Object object)
	{
		return rendered.contains(object);
	}

	/**
	 * @see wicket.markup.html.IHeaderResponse#getResponse()
	 */
	public final Response getResponse()
	{
		return response;
	}

	/**
	 * @see wicket.markup.html.IHeaderResponse#renderOnDomReadyJavascript(java.lang.String)
	 */
	public void renderOnDomReadyJavascript(String javascript)
	{
		renderJavascriptReference(WicketEventReference.INSTANCE);
		JavascriptUtils.writeJavascript(getResponse(),
				"Wicket.Event.add(window, \"domready\", function() { " + javascript + ";});");
	}

	/**
	 * @see wicket.markup.html.IHeaderResponse#renderOnLoadJavascript(java.lang.String)
	 */
	public void renderOnLoadJavascript(String javascript)
	{
		renderJavascriptReference(WicketEventReference.INSTANCE);
		JavascriptUtils.writeJavascript(getResponse(),
				"Wicket.Event.add(window, \"load\", function() { " + javascript + ";});");
	}
}
