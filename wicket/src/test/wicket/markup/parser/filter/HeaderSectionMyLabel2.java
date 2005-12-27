/*
 * $Id$
 * $Revision$
 * $Date$
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package wicket.markup.parser.filter;

import wicket.markup.html.IHeaderContributor;
import wicket.markup.html.basic.Label;
import wicket.markup.html.internal.HtmlHeaderContainer;
import wicket.model.Model;


/**
 * Mock page for testing.
 *
 * @author Chris Turner
 */
public class HeaderSectionMyLabel2 extends Label implements IHeaderContributor
{
	private static final long serialVersionUID = 1L;

	/**
	 * Construct.
	 * @param id
	 * @param label
	 */
	public HeaderSectionMyLabel2(final String id, final String label) 
	{
	    super(id, new Model(label));
    }

	/**
	 * @see wicket.markup.html.IHeaderContributor#renderHead(wicket.markup.html.internal.HtmlHeaderContainer)
	 * @param container
	 */
	public void renderHead(HtmlHeaderContainer container)
	{
	    this.getResponse().write("text added by contributor");
	}
}
