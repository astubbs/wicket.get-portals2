/*
 * $Id$ $Revision$
 * $Date$
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
package wicket.examples.repeater;

import wicket.MarkupContainer;
import wicket.markup.html.basic.Label;
import wicket.markup.html.link.Link;
import wicket.markup.html.panel.Panel;
import wicket.model.IModel;
import wicket.model.PropertyModel;

/**
 * Base page for component demo pages.
 * 
 * @author igor
 */
public class BasePage extends ExamplePage
{
	private Contact selected;

	/**
	 * Constructor
	 */
	public BasePage()
	{
		add(new Label(this,"selectedLabel", new PropertyModel(this, "selectedContactLabel")));
	}

	/**
	 * @return string representation of selceted contact property
	 */
	public String getSelectedContactLabel()
	{
		if (selected == null)
		{
			return "No Contact Selected";
		}
		else
		{
			return selected.getFirstName() + " " + selected.getLastName();
		}
	}

	/**
	 * 
	 */
	class ActionPanel extends Panel
	{
		/**
		 * @param id
		 *            component id
		 * @param model
		 *            model for contact
		 */
		public ActionPanel(MarkupContainer parent,final String id, IModel model)
		{
			super(parent,id, model);
			add(new Link(this,"select")
			{
				public void onClick()
				{
					BasePage.this.selected = (Contact)getParent().getModelObject();
				}
			});
		}
	}
}
