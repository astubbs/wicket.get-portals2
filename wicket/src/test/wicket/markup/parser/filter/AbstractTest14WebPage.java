/*
 * $Id$
 * $Revision$ $Date$
 * 
 * ==================================================================== Licensed
 * under the Apache License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the
 * License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package wicket.markup.parser.filter;

import wicket.markup.html.WebPage;
import wicket.markup.html.border.Border;

/**
 * 
 * @author pz65n8
 */
public abstract class AbstractTest14WebPage extends WebPage
{
	private Border border;

	/**
	 * 
	 */
	public AbstractTest14WebPage()
	{
		// Create border and add it to the page
		border = new HeaderSectionBorder_2("border");
		border.setTransparentResolver(true);
		super.add(border);
	}
//	
//	/**
//	 * @param child
//	 * @return xxx
//	 */
//	public MarkupContainer add(final Component child)
//	{
//		border.add(child);
//		return this;
//	}
//
//	/**
//	 * 
//	 */
//	public void removeAll()
//	{
//		border.removeAll();
//	}
//
//	/**
//	 * @param child
//	 * @return xxx 
//	 */
//	public MarkupContainer replace(final Component child)
//	{
//		return border.replace(child);
//	}
//
//	/**
//	 * @param component
//	 * @return xxx
//	 */
//	public boolean autoAdd(Component component)
//	{
//		return border.autoAdd(component);
//	}
//
}
