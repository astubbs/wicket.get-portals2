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
package wicket.protocol.http.portlet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import wicket.markup.html.pages.AccessDeniedPage;
import wicket.protocol.http.IWebApplicationFactory;
import wicket.protocol.http.WebApplication;
import wicket.protocol.http.WicketFilter;
import wicket.protocol.http.WicketServlet;

/**
 * 
 * Dummy utility servlet to support dynamic resources with portlets
 * 
 * See:
 * 
 * http://weblogs.java.net/blog/wholder/archive/2005/02/session_session.html
 * http://issues.apache.org/jira/browse/PLUTO-53
 * 
 * @deprecated Use WicketPortletFilter instead
 * @author Janne Hietam&auml;ki
 */
public class WicketPortletServlet extends WicketServlet
{
	private static final Log log = LogFactory.getLog(WicketPortletServlet.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected final IWebApplicationFactory getApplicationFactory()
	{
		return new IWebApplicationFactory(){

			public WebApplication createApplication(WicketServlet servlet)
			{
				return new WebApplication(){

					public Class getHomePage()
					{
						return AccessDeniedPage.class;
					}					
				};
			}

			public WebApplication createApplication(WicketFilter filter)
			{
				return new WebApplication(){

					public Class getHomePage()
					{
						return AccessDeniedPage.class;
					}					
				};
			}
		};
	}
}