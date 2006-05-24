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

import wicket.AttributeModifier;
import wicket.Component;
import wicket.extensions.markup.html.repeater.data.DataView;
import wicket.extensions.markup.html.repeater.data.sort.OrderByBorder;
import wicket.extensions.markup.html.repeater.refreshing.Item;
import wicket.markup.html.basic.Label;
import wicket.markup.html.navigation.paging.PagingNavigator;
import wicket.model.AbstractReadOnlyModel;

/**
 * page that demonstrates dataview and sorting
 * 
 * @see wicket.extensions.markup.html.repeater.data.DataView
 * @see wicket.extensions.markup.html.repeater.data.sort.OrderByBorder
 * @see wicket.extensions.markup.html.repeater.data.sort.OrderByLink
 * 
 * @author igor
 * 
 */
public class SortingPage extends BasePage
{
	/**
	 * constructor
	 */
	public SortingPage()
	{
		SortableContactDataProvider dp = new SortableContactDataProvider();
		final DataView dataView = new DataView(this,"sorting", dp)
		{
			protected void populateItem(final Item item)
			{
				Contact contact = (Contact)item.getModelObject();
				item.add(new ActionPanel(item,"actions", item.getModel()));
				item.add(new Label(item,"contactid", String.valueOf(contact.getId())));
				item.add(new Label(item,"firstname", contact.getFirstName()));
				item.add(new Label(item,"lastname", contact.getLastName()));
				item.add(new Label(item,"homephone", contact.getHomePhone()));
				item.add(new Label(item,"cellphone", contact.getCellPhone()));

				item.add(new AttributeModifier("class", true, new AbstractReadOnlyModel()
				{
					public Object getObject(Component component)
					{
						return (item.getIndex() % 2 == 1) ? "even" : "odd";
					}
				}));
			}
		};

		dataView.setItemsPerPage(8);

		add(new OrderByBorder(this,"orderByFirstName", "firstName", dp)
		{
			protected void onSortChanged()
			{
				dataView.setCurrentPage(0);
			}
		});

		add(new OrderByBorder(this,"orderByLastName", "lastName", dp)
		{
			protected void onSortChanged()
			{
				dataView.setCurrentPage(0);
			}
		});

		add(dataView);

		add(new PagingNavigator(this,"navigator", dataView));
	}
}
