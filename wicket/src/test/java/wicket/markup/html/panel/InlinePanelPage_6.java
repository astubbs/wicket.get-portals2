/*
 * $Id: InlinePanelPage_2.java 5844 2006-05-24 20:53:56 +0000 (Wed, 24 May 2006)
 * joco01 $ $Revision: 5883 $ $Date: 2006-05-24 20:53:56 +0000 (Wed, 24 May
 * 2006) $
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
package wicket.markup.html.panel;

import wicket.markup.html.WebPage;
import wicket.markup.html.basic.Label;
import wicket.markup.html.link.Link;


/**
 * 
 * @author Juergen Donnerstag
 */
public class InlinePanelPage_6 extends WebPage
{
	private static final long serialVersionUID = 1L;

	/**
	 * Construct.
	 * 
	 */
	public InlinePanelPage_6()
	{
		Fragment panel1 = new Fragment(this, "myPanel1", "frag1");
		
		Link link = new Link(panel1,"link")
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick()
			{
			}
		};
		
		new Label(link, "label1", "my label text");
	}
}