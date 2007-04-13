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

import org.apache.wicket.Application;
import org.apache.wicket.Request;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.Session;

/**
 * A session subclass for the HTTP protocol.
 * 
 * @author Jonathan Locke
 */
public class WebSession extends Session
{
	/** log. careful, this log is used to trigger profiling too! */
	// private static final Log log = LogFactory.getLog(WebSession.class);
	private static final long serialVersionUID = 1L;

	/** True, if session has been invalidated */
	private transient boolean sessionInvalidated = false;

	/**
	 * Constructor. Note that {@link RequestCycle} is not available until this
	 * constructor returns.
	 * 
	 * @param application
	 *            The application
	 * @param request
	 *            The current request
	 */
	public WebSession(final Application application, Request request)
	{
		super(application, request);
	}

	/**
	 * Constructor. Note that {@link RequestCycle} is not available until this
	 * constructor returns.
	 * 
	 * @param application
	 *            The application
	 * @param request
	 *            The current request
	 */
	public WebSession(final WebApplication application, Request request)
	{
		super(application, request);
	}

	/**
	 * Invalidates this session at the end of the current request. If you need
	 * to invalidate the session immediately, you can do this by calling
	 * invalidateNow(), however this will remove all Wicket components from this
	 * session, which means that you will no longer be able to work with them.
	 */
	public void invalidate()
	{
		sessionInvalidated = true;
	}

	/**
	 * Invalidates this session immediately. Calling this method will remove all
	 * Wicket components from this session, which means that you will no longer
	 * be able to work with them.
	 */
	public void invalidateNow()
	{
		sessionInvalidated = true; // set this for isSessionInvalidated
		getSessionStore().invalidate(RequestCycle.get().getRequest());
	}

	/**
	 * Whether the session is invalid now, or will be invalidated by the end of
	 * the request. Clients should rarely need to use this method if ever.
	 * 
	 * @return Whether the session is invalid when the current request is done
	 * 
	 * @see #invalidate()
	 * @see #invalidateNow()
	 */
	public final boolean isSessionInvalidated()
	{
		return sessionInvalidated;
	}

	/**
	 * Called on the end of handling a request, when the RequestCycle is about
	 * to be detached from the current thread.
	 * 
	 * @see org.apache.wicket.Session#detach()
	 */
	protected void detach()
	{
		if (sessionInvalidated)
		{
			invalidateNow();
		}
	}

	/**
	 * Updates the session, e.g. for replication purposes.
	 */
	protected void update()
	{
		if (sessionInvalidated == false)
		{
			super.update();
		}
	}
}
