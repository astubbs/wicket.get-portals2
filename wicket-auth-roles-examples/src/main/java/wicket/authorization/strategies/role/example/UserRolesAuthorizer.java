/*
 * $Id$ $Revision$ $Date$
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
package wicket.authorization.strategies.role.example;

import wicket.Session;
import wicket.authorization.strategies.role.IRoleAuthorizer;

/**
 * The authorizer we need to provide to the authorization strategy
 * implementation
 * {@link wicket.authorization.strategies.role.annotations.AnnotationsRoleAuthorizationStrategy}.
 * 
 * @author Eelco Hillenius
 */
public class UserRolesAuthorizer implements IRoleAuthorizer
{

	/**
	 * Construct.
	 */
	public UserRolesAuthorizer()
	{
	}

	/**
	 * @see wicket.authorization.strategies.role.IRoleAuthorizer#hasAny(java.lang.String[])
	 */
	public boolean hasAny(String[] roles)
	{
		RolesSession authSession = (RolesSession)Session.get();
		return authSession.getUser().hasAnyRole(roles);
	}

}
