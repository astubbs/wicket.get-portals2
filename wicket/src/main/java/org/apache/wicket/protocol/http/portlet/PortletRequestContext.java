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
package org.apache.wicket.protocol.http.portlet;

import java.util.HashMap;

import javax.portlet.MimeResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.PortletURL;
import javax.portlet.ResourceURL;
import javax.servlet.http.HttpServletRequest;

import org.apache.portals.bridges.util.PortletWindowUtils;
import org.apache.wicket.RequestContext;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.Response;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.protocol.http.WebRequest;
import org.apache.wicket.protocol.http.WebResponse;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;

/**
 * @author Ate Douma
 */
public class PortletRequestContext extends RequestContext
{
	private final WicketFilterPortletContext filterContext;
	private final PortletConfig portletConfig;
	private final PortletRequest portletRequest;
	private final PortletResponse portletResponse;
	// needed for JSR-168 support which only allows PortletURLs to be created by RenderResponse,
	// with JSR-286 PortletResponse can do that too
	private final MimeResponse mimeResponse;
// private final PortletResourceURLFactory resourceURLFactory;
	private final IHeaderResponse headerResponse;
	private String portletWindowId;
	private final String wicketUrlPortletParameter;
	private final boolean ajax;
	private final boolean embedded;
	private final boolean resourceRequest;
	private final String[] lastEncodedUrl = new String[2];

	public PortletRequestContext(WicketFilterPortletContext filterContext,
		ServletWebRequest request, WebResponse response)
	{
		this.filterContext = filterContext;
		HttpServletRequest servletRequest = request.getHttpServletRequest();
		portletConfig = (PortletConfig)servletRequest.getAttribute("javax.portlet.config");
		portletRequest = (PortletRequest)servletRequest.getAttribute("javax.portlet.request");
		portletResponse = (PortletResponse)servletRequest.getAttribute("javax.portlet.response");
		mimeResponse = (portletResponse instanceof MimeResponse) ? (MimeResponse)portletResponse
			: null;
		wicketUrlPortletParameter = (String)portletRequest.getAttribute(WicketPortlet.WICKET_URL_PORTLET_PARAMETER_ATTR);
		ajax = request.isAjax();
		resourceRequest = portletRequest.getAttribute(WicketPortlet.REQUEST_TYPE_ATTR).equals(
			WicketPortlet.RESOURCE_REQUEST);
		embedded = !(ajax || resourceRequest);
		headerResponse = embedded ? newPortletHeaderResponse(response) : null;
	}

	protected IHeaderResponse newPortletHeaderResponse(Response response)
	{
		return new EmbeddedPortletHeaderResponse(response);
	}

	public String getLastEncodedPath()
	{
		if (lastEncodedUrl != null)
		{
			return lastEncodedUrl[1];
		}
		return null;
	}

	public String getLastEncodedPath(String url)
	{
		if (url != null && lastEncodedUrl != null && url.equals(lastEncodedUrl[0]))
		{
			return lastEncodedUrl[1];
		}
		return null;
	}

	protected String saveLastEncodedUrl(String url, String path)
	{
		lastEncodedUrl[0] = url;
		lastEncodedUrl[1] = path;
		return url;
	}

	/**
	 * @see org.apache.wicket.RequestContext#encodeActionURL(java.lang.CharSequence)
	 */
	@Override
	public CharSequence encodeActionURL(CharSequence path)
	{
		return encodeActionURL(path, false);
	}

	public CharSequence encodeActionURL(CharSequence path, boolean forceActionURL)
	{
		if ((!forceActionURL && resourceRequest) || RequestCycle.get().isUrlForNewWindowEncoding())
		{
			return encodeResourceURL(path);
		}
		if (path != null)
		{
			path = getQualifiedPath(path);
			if (mimeResponse != null)
			{
				PortletURL url = mimeResponse.createActionURL();
				url.setParameter(wicketUrlPortletParameter, path.toString());
				path = saveLastEncodedUrl(url.toString(), path.toString());
			}
		}
		return path;
	}

	/**
	 * @see org.apache.wicket.RequestContext#encodeMarkupId(java.lang.String)
	 */
	@Override
	public String encodeMarkupId(String markupId)
	{
		if (markupId != null)
		{
			markupId = getNamespace() + "_" + markupId;
		}
		return markupId;
	}

	/**
	 * @see org.apache.wicket.RequestContext#encodeRenderURL(java.lang.CharSequence)
	 */
	@Override
	public CharSequence encodeRenderURL(CharSequence path)
	{
		return encodeRenderURL(path, false);
	}

	public CharSequence encodeRenderURL(CharSequence path, boolean forceRenderURL)
	{
		if ((!forceRenderURL && resourceRequest) || RequestCycle.get().isUrlForNewWindowEncoding())
		{
			return encodeResourceURL(path);
		}
		if (path != null)
		{
			path = getQualifiedPath(path);
			if (mimeResponse != null)
			{
				PortletURL url = mimeResponse.createRenderURL();
				url.setParameter(wicketUrlPortletParameter +
					portletRequest.getPortletMode().toString(), path.toString());
				path = saveLastEncodedUrl(url.toString(), path.toString());
			}
		}
		return path;
	}

	/**
	 * @see org.apache.wicket.RequestContext#encodeResourceURL(java.lang.CharSequence)
	 */
	@Override
	public CharSequence encodeResourceURL(CharSequence path)
	{
		if (path != null)
		{
			path = getQualifiedPath(path);
			if (mimeResponse != null)
			{
				try
				{
					HashMap parameters = new HashMap(2);
					parameters.put(wicketUrlPortletParameter +
						portletRequest.getPortletMode().toString(),
						new String[] { path.toString() });
					ResourceURL url = mimeResponse.createResourceURL();
					url.setParameters(parameters);
					path = saveLastEncodedUrl(url.toString(), path.toString());
				}
				catch (Exception e)
				{
					throw new RuntimeException(e);
				}
			}
		}
		return path;
	}

	/**
	 * @see org.apache.wicket.RequestContext#encodeSharedResourceURL(java.lang.CharSequence)
	 */
	@Override
	public CharSequence encodeSharedResourceURL(CharSequence path)
	{
		if (path != null)
		{
			String url = filterContext.encodeWindowIdInPath(getPortletWindowId(), path);
			return saveLastEncodedUrl(url, url);
		}
		return null;
	}

	/**
	 * @see org.apache.wicket.RequestContext#getHeaderResponse()
	 */
	@Override
	public IHeaderResponse getHeaderResponse()
	{
		return headerResponse;
	}

	/**
	 * @see org.apache.wicket.RequestContext#getNamespace()
	 */
	@Override
	public CharSequence getNamespace()
	{
		return portletResponse != null ? portletResponse.getNamespace() : "";
	}

	/**
	 * @see org.apache.wicket.RequestContext#isPortletRequest()
	 */
	@Override
	public boolean isPortletRequest()
	{
		return true;
	}

	public boolean isEmbedded()
	{
		return embedded;
	}

	protected String getQualifiedPath(CharSequence path)
	{
		HttpServletRequest request = ((WebRequest)RequestCycle.get().getRequest()).getHttpServletRequest();
		return request.getServletPath() + "/" + path;
	}

	protected String getPortletWindowId()
	{
		if (portletWindowId == null)
		{
			portletWindowId = PortletWindowUtils.getPortletWindowId(portletRequest.getPortletSession());
		}
		return portletWindowId;
	}

	public PortletConfig getPortletConfig()
	{
		return portletConfig;
	}

	public PortletRequest getPortletRequest()
	{
		return portletRequest;
	}

	public PortletResponse getPortletResponse()
	{
		return portletResponse;
	}

	public boolean isAjax()
	{
		return ajax;
	}
}
