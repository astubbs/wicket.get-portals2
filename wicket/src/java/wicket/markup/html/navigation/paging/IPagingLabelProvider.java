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

/**
 * This interface is used by the PagingNavigator components to get the label of
 * the pages there are for a IPageable component. By default this is only the
 * page number.
 * 
 * @author jcompagner
 */
public interface IPagingLabelProvider
{
	/**
	 * @param page
	 *            The page number for which the label must be generated.
	 * @return The string to be displayed for this page number
	 */
	String getPageLabel(int page);
}