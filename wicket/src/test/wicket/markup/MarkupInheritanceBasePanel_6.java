/*
 * $Id: MarkupInheritanceBasePanel_6.java 5860 2006-05-25 20:29:28 +0000 (Thu,
 * 25 May 2006) eelco12 $ $Revision$ $Date: 2006-05-25 20:29:28 +0000
 * (Thu, 25 May 2006) $
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
package wicket.markup;

import wicket.MarkupContainer;
import wicket.markup.html.basic.Label;
import wicket.markup.html.panel.Panel;


/**
 */
public class MarkupInheritanceBasePanel_6 extends Panel
{
	private static final long serialVersionUID = 1L;

	/**
	 * Construct.
	 * 
	 * @param id
	 */
	public MarkupInheritanceBasePanel_6(MarkupContainer parent, final String id)
	{
		super(parent, id);

		new Label(this, "panelLabel1", "label 1");
		new Label(this, "panelLabel2", "label 2");
	}
}
