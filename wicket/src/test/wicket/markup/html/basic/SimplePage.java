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
package wicket.markup.html.basic;

import wicket.AttributeModifier;
import wicket.PageParameters;
import wicket.markup.html.WebMarkupContainer;
import wicket.markup.html.WebPage;
import wicket.markup.html.border.Border;
import wicket.markup.html.panel.Panel;
import wicket.model.Model;


/**
 * Mock page for testing.
 *
 * @author Chris Turner
 */
public class SimplePage extends WebPage 
{
	/**
	 * Construct.
	 * @param parameters
	 */
	public SimplePage(final PageParameters parameters) 
	{
		add(new Label("myLabel", "Test Label"));
		
	    WebMarkupContainer container = new WebMarkupContainer("test");
	    container.setRenderBodyOnly(true);
	    add(container);

	    Panel panel = new SimplePanel("myPanel");
	    panel.setRenderBodyOnly(true);
	    add(panel);

	    Border border = new SimpleBorder("myBorder");
	    add(border);

	    Border border2 = new SimpleBorder("myBorder2");
	    border2.setRenderBodyOnly(false);
	    border2.add(new AttributeModifier("testAttr", true, new Model("myValue")));
	    add(border2);
    }
}
