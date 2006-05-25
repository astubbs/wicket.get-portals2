/*
 * $Id: HeaderSectionMyLabel2.java 5860 2006-05-25 20:29:28 +0000 (Thu, 25 May
 * 2006) eelco12 $ $Revision$ $Date: 2006-05-25 20:29:28 +0000 (Thu, 25
 * May 2006) $
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

import wicket.MarkupContainer;
import wicket.markup.html.basic.Label;
import wicket.markup.html.internal.HtmlHeaderContainer;
import wicket.model.Model;


/**
 * Mock page for testing.
 * 
 * @author Chris Turner
 */
public class HeaderSectionMyLabel2 extends Label
{
	private static final long serialVersionUID = 1L;

	/**
	 * Construct.
	 * 
	 * @param id
	 * @param label
	 */
	public HeaderSectionMyLabel2(MarkupContainer parent, final String id, final String label)
	{
		super(parent, id, new Model(label));
	}

	/**
	 * @param container
	 */
	@Override
	public void renderHead(HtmlHeaderContainer container)
	{
		this.getResponse().write("text added by contributor");
	}
}
