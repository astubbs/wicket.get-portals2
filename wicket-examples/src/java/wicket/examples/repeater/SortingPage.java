package wicket.examples.repeater;

import wicket.AttributeModifier;
import wicket.Component;
import wicket.extensions.markup.html.repeater.data.DataView;
import wicket.extensions.markup.html.repeater.data.sort.OrderByBorder;
import wicket.extensions.markup.html.repeater.pageable.Item;
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
		final DataView dataView = new DataView("sorting", new SortableContactDataProvider())
		{

			protected void populateItem(final Item item)
			{
				Contact contact = (Contact)item.getModelObject();
				item.add(new ActionPanel("actions", item.getModel()));
				item.add(new Label("contactid", String.valueOf(contact.getId())));
				item.add(new Label("firstname", contact.getFirstName()));
				item.add(new Label("lastname", contact.getLastName()));
				item.add(new Label("homephone", contact.getHomePhone()));
				item.add(new Label("cellphone", contact.getCellPhone()));

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

		add(new OrderByBorder("orderByFirstName", "firstName", dataView));

		add(new OrderByBorder("orderByLastName", "lastName", dataView));

		add(dataView);

		add(new PagingNavigator("navigator", dataView));
	}
}
