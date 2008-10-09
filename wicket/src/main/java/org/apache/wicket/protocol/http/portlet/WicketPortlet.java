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

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.EventRequest;
import javax.portlet.EventResponse;
import javax.portlet.GenericPortlet;
import javax.portlet.MimeResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.PortletResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.portlet.ResourceURL;

import org.apache.wicket.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Ate Douma
 */
public class WicketPortlet extends GenericPortlet
{
	private static final Logger LOG = LoggerFactory.getLogger(WicketPortlet.class);

	public static final String WICKET_URL_PORTLET_PARAMETER = "_wu";
	public static final String WICKET_FILTER_PATH_PARAM = "wicketFilterPath";

	/** Portal Action request */
	public static final String ACTION_REQUEST = "ACTION";
	/** Portal Resource request */
	public static final String RESOURCE_REQUEST = "RESOURCE";
	public static final String EVENT_REQUEST = "EVENT";

	/** Portal Custom request */
	public static final String CUSTOM_REQUEST = "CUSTOM";
	/** Portal View request - i.e. doView */
	public static final String VIEW_REQUEST = "VIEW";
	/** Portal Edit request - i.e. doEdit */
	public static final String EDIT_REQUEST = "EDIT";
	/** Portal Help request - i.e. doHelp */
	public static final String HELP_REQUEST = "HELP";
	/**
	 * Marker used as key to store the type of request as a request attribute - i.e. resource /
	 * action / event request etc .
	 */
	public static final String REQUEST_TYPE_ATTR = WicketPortlet.class.getName() + ".REQUEST_TYPE";
	public static final String WICKET_URL_PORTLET_PARAMETER_ATTR = WicketPortlet.class.getName() +
		".WICKET_URL_PORTLET_PARAMETER";
	public static final String CONFIG_PARAM_PREFIX = WicketPortlet.class.getName() + ".";
	/** Marker used as key for the WicketResponseState object stored as a request attribute. */
	public static final String RESPONSE_STATE_ATTR = WicketResponseState.class.getName();
	public static final String WICKET_PORTLET_PROPERTIES = WicketPortlet.class.getName().replace(
		'.', '/') +
		".properties";
	public static final String WICKET_FILTER_PATH = WicketPortlet.class.getName() + ".FILTERPATH";
	public static final String WICKET_FILTER_QUERY = WicketPortlet.class.getName() + ".FILTERQUERY";

	/**
	 * Name of portlet init parameter for Action page
	 */
	public static final String PARAM_ACTION_PAGE = "actionPage";
	/**
	 * Name of portlet init parameter for Custom page
	 */
	public static final String PARAM_CUSTOM_PAGE = "customPage";
	/**
	 * Name of portlet init parameter for Edit page
	 */
	public static final String PARAM_EDIT_PAGE = "editPage";
	/**
	 * Name of portlet init parameter for Edit page
	 */
	public static final String PARAM_HELP_PAGE = "helpPage";
	/**
	 * Name of portlet init parameter for View page
	 */
	public static final String PARAM_VIEW_PAGE = "viewPage";

	private String wicketFilterPath;
	private String wicketFilterQuery;
	private final HashMap<String, String> defaultPages = new HashMap<String, String>();

	@Override
	public void init(PortletConfig config) throws PortletException
	{
		super.init(config);

		wicketFilterPath = buildWicketFilterPath(config.getInitParameter(WICKET_FILTER_PATH_PARAM));
		wicketFilterQuery = buildWicketFilterQuery(wicketFilterPath);

		defaultPages.put(PARAM_VIEW_PAGE, config.getInitParameter(PARAM_VIEW_PAGE));
		defaultPages.put(PARAM_ACTION_PAGE, config.getInitParameter(PARAM_ACTION_PAGE));
		defaultPages.put(PARAM_CUSTOM_PAGE, config.getInitParameter(PARAM_CUSTOM_PAGE));
		defaultPages.put(PARAM_HELP_PAGE, config.getInitParameter(PARAM_HELP_PAGE));
		defaultPages.put(PARAM_EDIT_PAGE, config.getInitParameter(PARAM_EDIT_PAGE));

		validateDefaultPages(defaultPages, wicketFilterPath, wicketFilterQuery);
	}

	@Override
	public void destroy()
	{
		super.destroy();
	}

	protected String getDefaultPage(String pageType)
	{
		return defaultPages.get(pageType);
	}

	protected String buildWicketFilterPath(String filterPath)
	{
		if (filterPath == null || filterPath.length() == 0)
		{
			filterPath = "/";
		}
		else
		{
			if (!filterPath.startsWith("/"))
			{
				filterPath = "/" + filterPath;
			}
			if (filterPath.endsWith("*"))
			{
				filterPath = filterPath.substring(0, filterPath.length() - 1);
			}
			if (!filterPath.endsWith("/"))
			{
				filterPath += "/";
			}
		}
		return filterPath;
	}

	protected String buildWicketFilterQuery(String wicketFilterPath)
	{
		if (wicketFilterPath.equals("/"))
		{
			return "?";
		}
		else
		{
			return wicketFilterPath.substring(0, wicketFilterPath.length() - 1) + "?";
		}
	}

	protected String fixWicketUrl(String url, String wicketFilterPath, String wicketFilterQuery)
	{
		if (url == null)
		{
			return wicketFilterPath;
		}
		else if (!url.startsWith(wicketFilterPath))
		{
			if ((url + "/").equals(wicketFilterPath))
			{
				// hack around "old" style wicket home url's without trailing '/' which would lead
				// to a redirect to the real home path anyway
				url = wicketFilterPath;
			}
			else if (url.startsWith(wicketFilterQuery))
			{
				// correct url: path?query -> path/?query
				url = wicketFilterPath + "?" + url.substring(wicketFilterQuery.length());
			}
		}
		return url;
	}

	protected void validateDefaultPages(Map defaultPages, String wicketFilterPath,
		String wicketFilterQuery)
	{
		String viewPage = fixWicketUrl((String)defaultPages.get(PARAM_VIEW_PAGE), wicketFilterPath,
			wicketFilterQuery);
		defaultPages.put(PARAM_VIEW_PAGE, viewPage.startsWith(wicketFilterPath) ? viewPage
			: wicketFilterPath);

		String defaultPage = (String)defaultPages.get(PARAM_ACTION_PAGE);
		if (defaultPage == null)
		{
			defaultPages.put(PARAM_ACTION_PAGE, viewPage);
		}
		else
		{
			defaultPage = fixWicketUrl(defaultPage, wicketFilterPath, wicketFilterQuery);
			defaultPages.put(PARAM_ACTION_PAGE, defaultPage.startsWith(wicketFilterPath)
				? defaultPage : viewPage);
		}

		defaultPage = (String)defaultPages.get(PARAM_CUSTOM_PAGE);
		if (defaultPage == null)
		{
			defaultPages.put(PARAM_CUSTOM_PAGE, viewPage);
		}
		else
		{
			defaultPage = fixWicketUrl(defaultPage, wicketFilterPath, wicketFilterQuery);
			defaultPages.put(PARAM_CUSTOM_PAGE, defaultPage.startsWith(wicketFilterPath)
				? defaultPage : viewPage);
		}

		defaultPage = (String)defaultPages.get(PARAM_HELP_PAGE);
		if (defaultPage == null)
		{
			defaultPages.put(PARAM_HELP_PAGE, viewPage);
		}
		else
		{
			defaultPage = fixWicketUrl(defaultPage, wicketFilterPath, wicketFilterQuery);
			defaultPages.put(PARAM_HELP_PAGE, defaultPage.startsWith(wicketFilterPath)
				? defaultPage : viewPage);
		}

		defaultPage = (String)defaultPages.get(PARAM_EDIT_PAGE);
		if (defaultPage == null)
		{
			defaultPages.put(PARAM_EDIT_PAGE, viewPage);
		}
		else
		{
			defaultPage = fixWicketUrl(defaultPage, wicketFilterPath, wicketFilterQuery);
			defaultPages.put(PARAM_EDIT_PAGE, defaultPage.startsWith(wicketFilterPath)
				? defaultPage : viewPage);
		}
	}

	protected Properties getWicketPortletProperties(Properties properties) throws PortletException
	{
		if (properties == null)
		{
			properties = new Properties();
		}
		InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(
			WICKET_PORTLET_PROPERTIES);
		if (is != null)
		{
			try
			{
				properties.load(is);
			}
			catch (IOException e)
			{
				throw new PortletException(
					"Failed to load WicketPortlet.properties from classpath", e);
			}
		}
		return properties;
	}

	protected String getWicketConfigParameter(PortletRequest request, String paramName,
		String defaultValue)
	{
		return defaultValue;
	}

	protected String getWicketUrlPortletParameter(PortletRequest request)
	{
		return WICKET_URL_PORTLET_PARAMETER;
	}

	protected String getWicketFilterPath()
	{
		return wicketFilterPath;
	}

	protected String getWicketURL(PortletRequest request, String pageType, String defaultPage)
	{
		String wicketURL = null;
		if (request instanceof ActionRequest)
		{
			wicketURL = request.getParameter((String)request.getAttribute(WicketPortlet.WICKET_URL_PORTLET_PARAMETER_ATTR));
		}
		else
		{
			wicketURL = request.getParameter((String)request.getAttribute(WicketPortlet.WICKET_URL_PORTLET_PARAMETER_ATTR) +
				request.getPortletMode().toString());
		}
		if (wicketURL == null)
		{
			wicketURL = getWicketConfigParameter(request, CONFIG_PARAM_PREFIX + pageType,
				defaultPage);
		}
		return wicketURL;
	}

	@Override
	protected void doView(RenderRequest request, RenderResponse response) throws PortletException,
		IOException
	{
		LOG.debug("doView");
		processRequest(request, response, VIEW_REQUEST, PARAM_VIEW_PAGE);
	}

	@Override
	protected void doEdit(RenderRequest request, RenderResponse response) throws PortletException,
		IOException
	{
		LOG.debug("doEdit");
		processRequest(request, response, EDIT_REQUEST, PARAM_EDIT_PAGE);
	}

	@Override
	protected void doHelp(RenderRequest request, RenderResponse response) throws PortletException,
		IOException
	{
		LOG.debug("doHelp");
		processRequest(request, response, HELP_REQUEST, PARAM_HELP_PAGE);
	}

	protected void doCustom(RenderRequest request, RenderResponse response)
		throws PortletException, IOException
	{
		LOG.debug("doCustom");
		processRequest(request, response, CUSTOM_REQUEST, PARAM_CUSTOM_PAGE);
	}

	@Override
	public void processAction(ActionRequest request, ActionResponse response)
		throws PortletException, IOException
	{
		LOG.debug("processAction");
		processRequest(request, response, ACTION_REQUEST, PARAM_ACTION_PAGE);
	}

	@Override
	public void processEvent(EventRequest request, EventResponse response) throws PortletException,
		IOException
	{
		// TODO implement?
		LOG.debug("processEvent");
		throw new NotImplementedException();
	}

	@Override
	public void serveResource(ResourceRequest request, ResourceResponse response)
		throws PortletException, IOException
	{
		LOG.debug("serveResource");
		processRequest(request, response, RESOURCE_REQUEST, PARAM_VIEW_PAGE);
	}

	/**
	 * Consumes and processes all portlet requests. All the doX methods delegate to this method,
	 * including processAction and serveResource.
	 * 
	 * @param request
	 * @param response
	 * @param requestType
	 * @param pageType
	 * @throws PortletException
	 * @throws IOException
	 */
	protected void processRequest(PortletRequest request, PortletResponse response,
		String requestType, String pageType) throws PortletException, IOException
	{
		String wicketURL = null;
		String wicketFilterPath = null;
		String wicketFilterQuery = null;

		request.setAttribute(WICKET_URL_PORTLET_PARAMETER_ATTR,
			getWicketUrlPortletParameter(request));

		wicketURL = getWicketURL(request, pageType, getDefaultPage(pageType));
		wicketFilterPath = getWicketConfigParameter(request, WICKET_FILTER_PATH,
			this.wicketFilterPath);
		wicketFilterQuery = getWicketConfigParameter(request, WICKET_FILTER_QUERY,
			this.wicketFilterQuery);

		boolean actionRequest = ACTION_REQUEST.equals(requestType);

		WicketResponseState responseState = new WicketResponseState();

		request.setAttribute(RESPONSE_STATE_ATTR, responseState);
		request.setAttribute(REQUEST_TYPE_ATTR, requestType);

		if (actionRequest)
		{

			PortletRequestDispatcher rd = getPortletContext().getRequestDispatcher(wicketURL);

			if (rd != null)
			{
				rd.include(request, response);
				processActionResponseState(wicketURL, wicketFilterPath, wicketFilterQuery,
					(ActionRequest)request, (ActionResponse)response, responseState);
			}
		}
		else
		{
			PortletRequestDispatcher rd = null;
			String previousURL = null;
			while (true)
			{
				rd = getPortletContext().getRequestDispatcher(wicketURL);
				if (rd != null)
				{
					rd.include(request, response);
					String redirectLocation = responseState.getRedirectLocation();
					if (redirectLocation != null)
					{
						redirectLocation = fixWicketUrl(redirectLocation, wicketFilterPath,
							wicketFilterQuery);
						boolean validWicketUrl = redirectLocation.startsWith(wicketFilterPath);
						if (requestType.equals(RESOURCE_REQUEST))
						{
							if (validWicketUrl)
							{
								HashMap<String, String[]> parameters = new HashMap<String, String[]>(
									2);
								parameters.put(
									(String)request.getAttribute(WicketPortlet.WICKET_URL_PORTLET_PARAMETER_ATTR) +
										request.getPortletMode().toString(),
									new String[] { redirectLocation });
								ResourceURL url = ((MimeResponse)response).createResourceURL();
								url.setParameters(parameters);
								redirectLocation = url.toString();
							}
							System.out.println("sendRedirect");
							// TODO fix this!!
							// FIXME
							LOG.warn("no-op - sendReriect not implemented");
							throw new NotImplementedException("no-op - sendReriect not implemented");
							// ((ResourceResponse)response).getHttpServletResponse(this, request,
							// response).sendRedirect(redirectLocation);
						}
						else if (validWicketUrl &&
							((previousURL == null || previousURL != redirectLocation)))
						{
							previousURL = wicketURL;
							wicketURL = redirectLocation;
							((RenderResponse)response).reset();
							responseState.reset();
							continue;
						}
						else
						{
							// TODO: unhandled/unsupport RenderResponse redirect
						}
					}
				}
				break;
			}
		}
	}

	protected void processActionResponseState(String wicketURL, String wicketFilterPath,
		String wicketFilterQuery, ActionRequest request, ActionResponse response,
		WicketResponseState responseState) throws PortletException, IOException
	{
		if (responseState.getRedirectLocation() != null)
		{
			wicketURL = fixWicketUrl(responseState.getRedirectLocation(), wicketFilterPath,
				wicketFilterQuery);
			if (wicketURL.startsWith(wicketFilterPath))
			{
				response.setRenderParameter(
					(String)request.getAttribute(WicketPortlet.WICKET_URL_PORTLET_PARAMETER_ATTR) +
						request.getPortletMode().toString(), wicketURL);
			}
			else
			{
				response.sendRedirect(responseState.getRedirectLocation());
			}
		}
	}

	@Override
	protected void doHeaders(RenderRequest request, RenderResponse response)
	{
		// TODO implement?
		LOG.debug("doHeaders");
		// getPortletContext().get
		super.doHeaders(request, response);
	}

	@Override
	public Map<String, String[]> getContainerRuntimeOptions()
	{
		LOG.debug("getContainerRuntimeOptions");
		// TODO implement?
		return null;
	}
}
