/*
 * $Id$ $Revision$ $Date$
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
package wicket.extensions.markup.html.tree;

import wicket.MarkupContainer;
import wicket.markup.IAlternateParentProvider;
import wicket.markup.html.WebPage;
import wicket.markup.html.border.Border;

/**
 * Base page with norder.
 */
public abstract class BorderedPage extends WebPage implements IAlternateParentProvider
{
	/** Border. */
	private Border border;

	/**
	 * Constructor.
	 */
	public BorderedPage()
	{
		border = new PageBorder(this, "border");
	}

	/**
	 * 
	 * @see wicket.markup.IAlternateParentProvider#getAlternateParent(java.lang.Class, java.lang.String)
	 */
	public MarkupContainer getAlternateParent(final Class childClass, final String childId)
	{
		return (this.border == null ? this : this.border);
	}
}
