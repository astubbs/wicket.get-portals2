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
package org.apache.wicket.protocol.http;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.AccessStackPageMap;
import org.apache.wicket.Application;
import org.apache.wicket.Component;
import org.apache.wicket.IPageMap;
import org.apache.wicket.IRequestTarget;
import org.apache.wicket.Page;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.Session;
import org.apache.wicket.AccessStackPageMap.Access;
import org.apache.wicket.protocol.http.request.WebRequestCodingStrategy;
import org.apache.wicket.protocol.http.servlet.AbortWithWebErrorCodeException;
import org.apache.wicket.request.AbstractRequestCycleProcessor;
import org.apache.wicket.request.IRequestCodingStrategy;
import org.apache.wicket.request.RequestParameters;
import org.apache.wicket.request.target.component.IBookmarkablePageRequestTarget;
import org.apache.wicket.util.string.Strings;

/**
 * Default request processor implementation for normal web applications.
 * 
 * @author eelcohillenius
 */
public class WebRequestCycleProcessor extends AbstractRequestCycleProcessor
{
	private static final Log log = LogFactory.getLog(WebRequestCycleProcessor.class);

	/**
	 * Construct.
	 */
	public WebRequestCycleProcessor()
	{
	}

	/**
	 * @see org.apache.wicket.request.IRequestCycleProcessor#resolve(org.apache.wicket.RequestCycle,
	 *      org.apache.wicket.request.RequestParameters)
	 */
	public IRequestTarget resolve(final RequestCycle requestCycle,
			final RequestParameters requestParameters)
	{
		// First, see whether we can find any mount
		IRequestCodingStrategy requestCodingStrategy = requestCycle.getProcessor()
				.getRequestCodingStrategy();
		IRequestTarget mounted = requestCodingStrategy.targetForRequest(requestParameters);

		// If we've found a mount, only use it if the componentPath is null.
		// Otherwise, we'll service it later with the components.
		if (mounted != null)
		{
			if (mounted instanceof IBookmarkablePageRequestTarget)
			{
				IBookmarkablePageRequestTarget bookmarkableTarget = (IBookmarkablePageRequestTarget)mounted;
				if (requestParameters.getComponentPath() != null
						&& requestParameters.getInterfaceName() != null)
				{
					final String componentPath = requestParameters.getComponentPath();
					final Page page = Session.get().getPage(requestParameters.getPageMapName(),
							componentPath, requestParameters.getVersionNumber());
					return resolveListenerInterfaceTarget(requestCycle, page, componentPath,
							requestParameters.getInterfaceName(), requestParameters);
				}
			}

			return mounted;
		}

		final String path = requestParameters.getPath();
		IRequestTarget target = null;

		// See whether this request points to a bookmarkable page
		if (requestParameters.getBookmarkablePageClass() != null)
		{
			target = resolveBookmarkablePage(requestCycle, requestParameters);
		}
		// See whether this request points to a rendered page
		else if (requestParameters.getComponentPath() != null)
		{
			// marks whether or not we will be processing this request
			boolean processRequest = true;
			synchronized (requestCycle.getSession())
			{
				// we need to check if this request has been flagged as
				// process-only-if-path-is-active and if so make sure this
				// condition is met


				if (requestParameters.isOnlyProcessIfPathActive())
				{
					// this request has indeed been flagged as
					// process-only-if-path-is-active

					Session session = Session.get();
					IPageMap pageMap = session.pageMapForName(requestParameters.getPageMapName(),
							false);
					if (pageMap == null)
					{
						// requested pagemap no longer exists - ignore this
						// request
						processRequest = false;
					}
					else if (pageMap instanceof AccessStackPageMap)
					{
						AccessStackPageMap accessStackPageMap = (AccessStackPageMap)pageMap;
						if (accessStackPageMap.getAccessStack().size() > 0)
						{
							final Access access = (Access)accessStackPageMap.getAccessStack()
									.peek();

							final int pageId = Integer
									.parseInt(Strings.firstPathComponent(requestParameters
											.getComponentPath(), Component.PATH_SEPARATOR));

							if (pageId != access.getId())
							{
								// the page is no longer the active page
								// - ignore this request
								processRequest = false;
							}
							else
							{
								final int version = requestParameters.getVersionNumber();
								if (version != Page.LATEST_VERSION
										&& version != access.getVersion())
								{
									// version is no longer the active version -
									// ignore this request
									processRequest = false;
								}
							}
						}
					}
					else
					{
						// TODO also this should work..
					}

				}

			}
			if (processRequest)
			{
				target = resolveRenderedPage(requestCycle, requestParameters);
			}
			else
			{
				throw new PageExpiredException("Request cannot be processed");
			}
		}
		// See whether this request points to a shared resource
		else if (requestParameters.getResourceKey() != null)
		{
			target = resolveSharedResource(requestCycle, requestParameters);
		}
		// See whether this request points to the home page
		else if (Strings.isEmpty(path) || ("/".equals(path)))
		{
			target = resolveHomePageTarget(requestCycle, requestParameters);
		}

		if (target != null)
		{
			if (Application.get().getSecuritySettings().getEnforceMounts()
					&& requestCodingStrategy.pathForTarget(target) != null)
			{
				String msg = "Direct access not allowed for mounted targets";
				// the target was mounted, but we got here via another path
				// : deny the request
				log.error(msg + " [request=" + requestCycle.getRequest() + ",target=" + target
						+ ",session=" + Session.get() + "]");
				throw new AbortWithWebErrorCodeException(HttpServletResponse.SC_FORBIDDEN, msg);
			}
			return target;
		}
		else
		{
			// if we get here, we have no regconized Wicket target, and thus
			// regard this as a external (non-wicket) resource request on
			// this server
			return resolveExternalResource(requestCycle);
		}
	}

	/**
	 * @see org.apache.wicket.request.AbstractRequestCycleProcessor#newRequestCodingStrategy()
	 */
	protected IRequestCodingStrategy newRequestCodingStrategy()
	{
		return new WebRequestCodingStrategy();
	}
}
