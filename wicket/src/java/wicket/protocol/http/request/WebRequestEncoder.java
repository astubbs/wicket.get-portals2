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
package wicket.protocol.http.request;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import wicket.Application;
import wicket.Component;
import wicket.IRequestTarget;
import wicket.Page;
import wicket.PageMap;
import wicket.PageParameters;
import wicket.Request;
import wicket.RequestCycle;
import wicket.WicketRuntimeException;
import wicket.protocol.http.WebRequest;
import wicket.protocol.http.WebRequestCycle;
import wicket.request.IListenerInterfaceRequestTarget;
import wicket.request.IPageClassRequestTarget;
import wicket.request.IPageRequestTarget;
import wicket.request.IRequestEncoder;
import wicket.request.ISharedResourceRequestTarget;
import wicket.request.RequestParameters;
import wicket.util.lang.Classes;
import wicket.util.string.Strings;

/**
 * Request parameters factory implementation that uses http request parameters
 * and path info to construct the request parameters object.
 * 
 * @author Eelco Hillenius
 */
public class WebRequestEncoder implements IRequestEncoder
{
	/** log. */
	private Log log = LogFactory.getLog(WebRequestEncoder.class);

	/**
	 * map of path mounts for request targets on paths.
	 */
	private Map/* <String,IRequestTarget> */mountsOnPath = new HashMap();

	/**
	 * map of path mounts for request targets on targets.
	 */
	private Map/* <IRequestTarget, String> */mountsOnTarget = new HashMap();

	/**
	 * Construct.
	 */
	public WebRequestEncoder()
	{
	}

	/**
	 * Encode the given request target. If a mount is found, that mounted url
	 * will be returned. Otherwise, one of the delegation methods will be
	 * called. In case you are using custom targets that are not part of the
	 * default target hierarchy, you need to override
	 * {@link #doEncode(RequestCycle, IRequestTarget)}, which will be called
	 * after the defaults have been tried. When that doesn't provide a url
	 * either, and exception will be thrown saying that encoding could not be
	 * done.
	 * 
	 * @see wicket.request.IRequestEncoder#encode(wicket.RequestCycle,
	 *      wicket.IRequestTarget)
	 */
	public final String encode(RequestCycle requestCycle, IRequestTarget requestTarget)
	{
		// first check whether the target was mounted
		String mountPath = (String)mountsOnTarget.get(requestTarget);
		if (mountPath != null)
		{
			final StringBuffer url = urlPrefix(requestCycle);
			return url.append(mountPath).toString();
		}

		if (requestTarget instanceof IPageClassRequestTarget)
		{
			return encode(requestCycle, (IPageClassRequestTarget)requestTarget);
		}
		else if (requestTarget instanceof IListenerInterfaceRequestTarget)
		{
			return encode(requestCycle, (IListenerInterfaceRequestTarget)requestTarget);
		}
		else if (requestTarget instanceof ISharedResourceRequestTarget)
		{
			return encode(requestCycle, (ISharedResourceRequestTarget)requestTarget);
		}
		// TODO handle more
		else if (requestTarget instanceof IPageRequestTarget)
		{

		}

		// fallthough for non-default request targets
		String url = doEncode(requestCycle, requestTarget);
		if (url != null)
		{
			return url;
		}

		// this method was not able to produce a url; throw an exception
		throw new WicketRuntimeException("unable to encode " + requestTarget);
	}

	/**
	 * In case you are using custom targets that are not part of the default
	 * target hierarchy, you need to override this method, which will be called
	 * after the defaults have been tried. When this doesn't provide a url
	 * either (returns null), an exception will be thrown by the encode method
	 * saying that encoding could not be done.
	 * 
	 * @param requestCycle
	 *            the current request cycle (for efficient access)
	 * 
	 * @param requestTarget
	 *            the request target
	 * @return the url to the provided target
	 */
	protected String doEncode(RequestCycle requestCycle, IRequestTarget requestTarget)
	{
		return null;
	}

	/**
	 * @see wicket.request.IRequestEncoder#decode(wicket.Request)
	 */
	public final RequestParameters decode(Request request)
	{
		RequestParameters parameters = new RequestParameters();
		String pathInfo = getRequestPath(request);
		parameters.setPath(pathInfo);
		addPageParameters(request, parameters);
		addBookmarkablePageParameters(request, parameters);
		addResourceParameters(request, parameters);
		return parameters;
	}

	/**
	 * @see wicket.request.IRequestEncoder#mountPath(java.lang.String,
	 *      wicket.IRequestTarget)
	 */
	public final void mountPath(String path, IRequestTarget requestTarget)
	{
		if (path == null)
		{
			throw new NullPointerException("argument path must be not-null");
		}

		if (requestTarget == null)
		{
			throw new NullPointerException("argument requestTarget must be not-null");
		}

		// sanity check
		if (!path.startsWith("/"))
		{
			path = "/" + path;
		}

		if (mountsOnPath.containsKey(path))
		{
			throw new WicketRuntimeException(path + " is already mounted for "
					+ mountsOnPath.get(path));
		}
		mountsOnPath.put(path, requestTarget);
		mountsOnTarget.put(requestTarget, path);
	}

	/**
	 * @see wicket.request.IRequestEncoder#unmountPath(java.lang.String)
	 */
	public final void unmountPath(String path)
	{
		if (path == null)
		{
			throw new NullPointerException("argument path must be not-null");
		}

		// sanity check
		if (!path.startsWith("/"))
		{
			path = "/" + path;
		}

		IRequestTarget target = (IRequestTarget)mountsOnPath.remove(path);
		mountsOnTarget.remove(target);
	}

	/**
	 * @see wicket.request.IRequestEncoder#getMountedTarget(java.lang.String)
	 */
	public final IRequestTarget getMountedTarget(String path)
	{
		return (IRequestTarget)mountsOnPath.get(path);
	}

	/**
	 * @see wicket.request.IRequestEncoder#getMountedPath(wicket.IRequestTarget)
	 */
	public final String getMountedPath(IRequestTarget requestTarget)
	{
		return (String)mountsOnTarget.get(requestTarget);
	}

	/**
	 * Gets the request info path. This is an overridable method in order to
	 * provide users with a means to implement e.g. a path encryption scheme.
	 * This method by default returns {@link Request#getPath()}.
	 * 
	 * @param request
	 *            the request
	 * @return the path info object, possibly processed
	 */
	protected String getRequestPath(Request request)
	{
		return request.getPath();
	}

	/**
	 * Encode a page class target.
	 * 
	 * @param requestCycle
	 *            the current request cycle
	 * @param requestTarget
	 *            the target to encode
	 * @return the encoded url
	 */
	protected final String encode(RequestCycle requestCycle, IPageClassRequestTarget requestTarget)
	{
		final Class pageClass = requestTarget.getPageClass();
		final PageParameters parameters = requestTarget.getPageParameters();
		final StringBuffer url = urlPrefix(requestCycle);
		url.append("?bookmarkablePage=");
		String pageReference = pageClass.getName();
		if (pageReference == null)
		{
			pageReference = pageClass.getName();
		}
		url.append(pageReference);

		String pageMapName = requestTarget.getPageMapName();
		if (pageMapName == null)
		{
			IRequestTarget currentTarget = requestCycle.getRequestTarget();
			if (currentTarget instanceof IPageRequestTarget)
			{
				Page currentPage = ((IPageRequestTarget)currentTarget).getPage();
				final PageMap pageMap = currentPage.getPageMap();
				if (!pageMap.isDefault())
				{
					url.append("&pagemap=");
					url.append(pageMap.getName());
				}
			}
		}
		else
		{
			url.append("&pagemap=");
			url.append(pageMapName);
		}

		if (parameters != null)
		{
			for (final Iterator iterator = parameters.keySet().iterator(); iterator.hasNext();)
			{
				final String key = (String)iterator.next();
				final String value = parameters.getString(key);
				if (value != null)
				{
					String escapedValue = value;
					try
					{
						escapedValue = URLEncoder.encode(escapedValue, Application.get()
								.getSettings().getResponseRequestEncoding());
					}
					catch (UnsupportedEncodingException ex)
					{
						log.error(ex.getMessage(), ex);
					}
					url.append('&');
					url.append(key);
					url.append('=');
					url.append(escapedValue);
				}
			}
		}

		return requestCycle.getResponse().encodeURL(url.toString());
	}

	/**
	 * Encode a listener interface target.
	 * 
	 * @param requestCycle
	 *            the current request cycle
	 * @param requestTarget
	 *            the target to encode
	 * @return the encoded url
	 */
	protected final String encode(RequestCycle requestCycle,
			IListenerInterfaceRequestTarget requestTarget)
	{
		final StringBuffer url = urlPrefix(requestCycle);
		url.append("?path=");
		Component component = requestTarget.getComponent();
		url.append(component.getPath());
		Page currentPage = component.getPage();
		final PageMap pageMap = currentPage.getPageMap();
		if (!pageMap.isDefault())
		{
			url.append("&pagemap=");
			url.append(pageMap.getName());
		}
		int versionNumber = component.getPage().getCurrentVersionNumber();
		if (versionNumber > 0)
		{
			url.append("&version=");
			url.append(versionNumber);
		}

		String listenerName = Classes.name(requestTarget.getListenerMethod().getDeclaringClass());
		if (!"IRedirectListener".equals(listenerName))
		{
			url.append("&interface=");
			url.append(listenerName);
		}

		return requestCycle.getResponse().encodeURL(url.toString());

	}

	/**
	 * Encode a shared resource target.
	 * 
	 * @param requestCycle
	 *            the current request cycle
	 * @param requestTarget
	 *            the target to encode
	 * @return the encoded url
	 */
	protected final String encode(RequestCycle requestCycle,
			ISharedResourceRequestTarget requestTarget)
	{
		String prefix = urlPrefix(requestCycle).toString();
		String resourceKey = requestTarget.getResourceKey();
		if ((resourceKey == null) || (resourceKey.trim().length() == 0))
		{
			return prefix;
		}
		else
		{
			if (prefix.endsWith("/") || resourceKey.startsWith("/"))
			{
				return prefix + resourceKey;
			}

			return prefix + "/" + resourceKey;
		}
	}

	/**
	 * Adds page related parameters (path and optionally pagemap, version and
	 * interface).
	 * 
	 * @param request
	 *            the incomming request
	 * @param parameters
	 *            the parameters object to set the found values on
	 */
	protected void addPageParameters(Request request, RequestParameters parameters)
	{
		String componentPath = request.getParameter("path");
		if (componentPath != null)
		{
			parameters.setComponentPath(componentPath);
			parameters.setPageMapName(request.getParameter("pagemap"));
			final String versionNumberString = request.getParameter("version");
			final int versionNumber = Strings.isEmpty(versionNumberString) ? 0 : Integer
					.parseInt(versionNumberString);
			parameters.setVersionNumber(versionNumber);
			String interfaceName = request.getParameter("interface");
			if (interfaceName == null)
			{
				interfaceName = "IRedirectListener";
			}
			parameters.setInterfaceName(interfaceName);
			parameters.setBehaviourId(request.getParameter("behaviourId"));
		}
	}

	/**
	 * Adds bookmarkable page related parameters (page alias and optionally page
	 * parameters). Any bookmarkable page alias mount will override this method;
	 * hence if a mount is found, this method will not be called.
	 * 
	 * @param request
	 *            the incomming request
	 * @param parameters
	 *            the parameters object to set the found values on
	 */
	protected void addBookmarkablePageParameters(Request request, RequestParameters parameters)
	{
		parameters.setBookmarkablePageClass(request.getParameter("bookmarkablePage"));
		parameters.setParameters(request.getParameterMap());
	}

	/**
	 * Adds (shared) resource related parameters (resource key). Any shared
	 * resource key mount will override this method; hence if a mount is found,
	 * this method will not be called.
	 * 
	 * @param request
	 *            the incomming request
	 * @param parameters
	 *            the parameters object to set the found values on
	 */
	protected void addResourceParameters(Request request, RequestParameters parameters)
	{
		String pathInfo = request.getPath();
		if (pathInfo != null && pathInfo.startsWith("/resources/"))
		{
			parameters.setResourceKey(pathInfo.substring("/resources/".length()));
		}
	}

	/**
	 * Creates a prefix for a url.
	 * 
	 * @param requestCycle
	 *            the current request cycle
	 * 
	 * @return Prefix for URLs including the context path and servlet path.
	 */
	protected StringBuffer urlPrefix(RequestCycle requestCycle)
	{
		final StringBuffer buffer = new StringBuffer();
		final WebRequest request = ((WebRequestCycle)requestCycle).getWebRequest();
		if (request != null)
		{
			final String contextPath = request.getContextPath();
			buffer.append(contextPath);
			String path = request.getServletPath();
			if (path == null || "".equals(path))
			{
				path = "/";
			}
			buffer.append(path);
		}

		return buffer;
	}
}
