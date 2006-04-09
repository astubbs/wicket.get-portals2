/*
 * $Id: ISessionStore.java 3777 2006-01-14 14:49:01 -0800 (Sat, 14 Jan 2006)
 * jonathanlocke $ $Revision: 3777 $ $Date: 2006-01-14 14:49:01 -0800 (Sat, 14
 * Jan 2006) $
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
package wicket.session;

import java.util.List;

import wicket.SessionFacade;

/**
 * The actual store that is used by {@link wicket.Session} to store its
 * attributes.
 * 
 * @author Eelco Hillenius
 */
public interface ISessionStore
{
	/**
	 * Gets the attribute value with the given name
	 * 
	 * @param name
	 *            The name of the attribute to store
	 * @return The value of the attribute
	 */
	Object getAttribute(final String name);

	/**
	 * @return List of attributes for this session
	 */
	List getAttributeNames();


	/**
	 * @return The session id
	 */
	String getId();

	/**
	 * Invalidates the session.
	 */
	void invalidate();

	/**
	 * Removes the attribute with the given name.
	 * 
	 * @param name
	 *            the name of the attribute to remove
	 */
	void removeAttribute(String name);

	/**
	 * Adds or replaces the attribute with the given name and value.
	 * 
	 * @param name
	 *            the name of the attribute
	 * @param value
	 *            the value of the attribute
	 */
	void setAttribute(String name, Object value);

	/**
	 * Clean up method which should be called on session invalidation by the
	 * implementation of {@link SessionFacade}.
	 */
	void destroy();
}
