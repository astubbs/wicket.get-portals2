/*
 * $Id: RolesAnnotAuthorizationStrategy.java,v 1.1 2006/02/02 08:12:42 eelco12
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
package wicket.authorization.strategies.role;

import wicket.authorization.IAuthorizationStrategy;

/**
 * Base strategy that uses an instance of
 * {@link wicket.authorization.strategies.role.IRoleAuthorizer}.
 * 
 * @author Eelco Hillenius
 */
public abstract class AbstractRoleAuthorizationStrategy implements IAuthorizationStrategy
{
	/** the authorizer delegate. */
	private final IRoleAuthorizer roleAuthorizer;

	/**
	 * Construct.
	 * 
	 * @param roleAuthorizer
	 *            the authorizer delegate
	 */
	public AbstractRoleAuthorizationStrategy(IRoleAuthorizer roleAuthorizer)
	{
		if (roleAuthorizer == null)
		{
			throw new IllegalArgumentException("roleAuthorizer must be not null");
		}
		this.roleAuthorizer = roleAuthorizer;
	}

	/**
	 * Gets whether any of the given roles applies to the authorizer.
	 * 
	 * @param roles
	 *            the roles
	 * @return whether any of the given roles applies to the authorizer
	 */
	protected final boolean hasAny(String[] roles)
	{
		boolean authorized;
		if (!isDefault(roles))
		{
			authorized = roleAuthorizer.hasAny(roles);
		}
		else
		{
			authorized = true;
		}
		return authorized;
	}

	/**
	 * Gets whether the provided value is a 'default' value (meaning an empty
	 * string).
	 * 
	 * @param value
	 *            the value
	 * @return whether the provided value is a 'default' value
	 */
	protected final boolean isDefault(String[] value)
	{
		if (value == null || (value.length == 1 && value[0].equals("")))
		{
			return true;
		}
		return false;
	}
}
