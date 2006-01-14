/*
 * $Id: UnauthorizedActionException.java,v 1.1 2005/12/22 22:27:01 jonathanlocke
 * Exp $ $Revision$ $Date$
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
package wicket.authorization;

import wicket.Component;

/**
 * Exception that is thrown when an action is not authorized.
 * 
 * @author Jonathan Locke
 * @author Eelco Hillenius
 */
public class UnauthorizedActionException extends AuthorizationException
{
	private static final long serialVersionUID = 1L;

	/** The action */
	private Action action;
	
	/**
	 * Construct.
	 * 
	 * @param component
	 *            The component
	 * @param action
	 *            The action
	 */
	public UnauthorizedActionException(Component component, Action action)
	{
		super("Component " + component + " does not permit action " + action);
		this.action = action;
	}
	
	/**
	 * @return The action that was forbidden
	 */
	public Action getAction()
	{
		return action;
	}
}
