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
package wicket.markup.html.navigation.paging;

import java.io.Serializable;

/**
 * Components that implement this interface will be pageable, they should return
 * the pagecount so that an object/component knows how many pages it can use for
 * the setCurrentPage method.
 * 
 * The PageableListView is one example that is Pageable. But also a Form could
 * be pageable so that you can scroll to sets of records that you display in
 * that form with any navigator you want.
 * 
 * @author jcompagner
 */
public interface IPageable extends Serializable
{
	/**
	 * @return The current page that is or will be rendered rendered.
	 */
	int getCurrentPage();

	/**
	 * Sets the a page that should be rendered.
	 * 
	 * @param page
	 *            The page that should be rendered.
	 */
	void setCurrentPage(int page);

	/**
	 * Gets the total number of pages this pageable object has.
	 * 
	 * @return The total number of pages this pageable object has
	 */
	int getPageCount();
}