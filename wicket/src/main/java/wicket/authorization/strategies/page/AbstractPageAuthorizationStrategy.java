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
package wicket.authorization.strategies.page;

import wicket.Component;
import wicket.Page;
import wicket.authorization.Action;
import wicket.authorization.IAuthorizationStrategy;

/**
 * An abstract base class for implementing simple authorization of Pages. Users
 * should override {@link #isPageAuthorized(Class)}, which gets called for Page
 * classes when they are being constructed.
 * 
 * @author Jonathan Locke
 * @author Eelco Hillenius
 */
public abstract class AbstractPageAuthorizationStrategy implements IAuthorizationStrategy
{
	/**
	 * @see wicket.authorization.IAuthorizationStrategy#isActionAuthorized(wicket.Component,
	 *      wicket.authorization.Action)
	 */
	public boolean isActionAuthorized(final Component component, final Action action)
	{
		return true;
	}

	/**
	 * @see wicket.authorization.IAuthorizationStrategy#isInstantiationAuthorized(java.lang.Class)
	 */
	@SuppressWarnings("unchecked")
	public final boolean isInstantiationAuthorized(final Class<? extends Component> componentClass)
	{
		if (instanceOf(componentClass, Page.class))
		{
			return isPageAuthorized((Class<? extends Page>)componentClass);
		}
		return true;
	}

	/**
	 * Works like instanceof operator where instanceOf(a, b) is the runtime
	 * equivalent of (a instanceof b).
	 * 
	 * @param type
	 *            The type to check
	 * @param superType
	 *            The interface or superclass that the type needs to implement
	 *            or extend
	 * @return True if the type is an instance of the superType
	 */
	protected boolean instanceOf(final Class<? extends Component> type, final Class<?> superType)
	{
		return superType.isAssignableFrom(type);
	}

	/**
	 * Whether to page may be created. Returns true by default.
	 * 
	 * @param pageClass
	 *            The Page class
	 * @return True if to page may be created
	 */
	protected boolean isPageAuthorized(Class<? extends Page> pageClass)
	{
		return true;
	}
}
