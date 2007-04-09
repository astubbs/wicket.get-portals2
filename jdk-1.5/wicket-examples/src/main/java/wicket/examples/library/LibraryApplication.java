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
package wicket.examples.library;

import wicket.Request;
import wicket.Response;
import wicket.Session;
import wicket.authorization.IAuthorizationStrategy;
import wicket.authorization.strategies.page.SimplePageAuthorizationStrategy;
import wicket.examples.WicketExampleApplication;
import wicket.settings.IRequestCycleSettings;

/**
 * WicketServlet class for example.
 * 
 * @author Jonathan Locke
 */
public final class LibraryApplication extends WicketExampleApplication
{
	/**
	 * Constructor.
	 */
	public LibraryApplication()
	{
	}

	/**
	 * @see wicket.Application#getHomePage()
	 */
	public Class getHomePage()
	{
		return Home.class;
	}

	/**
	 * @see wicket.protocol.http.WebApplication#newSession(wicket.Request,
	 *      Response)
	 */
	public Session newSession(Request request, Response response)
	{
		return new LibrarySession(LibraryApplication.this, request);
	}

	/**
	 * @see wicket.examples.WicketExampleApplication#init()
	 */
	protected void init()
	{
		getResourceSettings().setThrowExceptionOnMissingResource(false);
		getRequestCycleSettings().setRenderStrategy(IRequestCycleSettings.REDIRECT_TO_RENDER);

		// Install a simple page authorization strategy, that checks all pages
		// of type AuthenticatedWebPage.
		IAuthorizationStrategy authorizationStrategy = new SimplePageAuthorizationStrategy(
				AuthenticatedWebPage.class, SignIn.class)
		{
			protected boolean isAuthorized()
			{
				// check whether the user is logged on
				return (((LibrarySession)Session.get()).isSignedIn());
			}
		};
		getSecuritySettings().setAuthorizationStrategy(authorizationStrategy);
	}
}
