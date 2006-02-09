/*
 * $Id$
 * $Revision$ $Date$
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
package wicket.authorization.strategies.role.example.pages;

import wicket.authorization.strategies.role.metadata.MetaDataRoleAuthorizationStrategy;
import wicket.markup.html.WebPage;

/**
 * Non-bookmarkable page that may only be accessed by users that have role
 * ADMIN.
 * 
 * @author Eelco Hillenius
 */
public class AdminInternalPage extends WebPage
{
	/*
	 * We do it as a static call here, which is mainly for the purpose of the
	 * example. Typically, you probably do this somewhere central, like in
	 * {@link Application#init)
	 */
	static
	{
		MetaDataRoleAuthorizationStrategy.authorize(AdminInternalPage.class, "ADMIN");
	}

	/**
	 * Construct.
	 * 
	 * @param dummy
	 *            just a parameter to make this page non-bookmarkable
	 */
	public AdminInternalPage(String dummy)
	{
	}
}
