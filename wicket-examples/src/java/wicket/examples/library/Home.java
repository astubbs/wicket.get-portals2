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
package wicket.examples.library;

import java.util.ArrayList;
import java.util.List;

import wicket.PageParameters;
import wicket.markup.html.basic.Label;
import wicket.markup.html.list.ListItem;
import wicket.markup.html.list.PageableListView;
import wicket.markup.html.navigation.paging.PagingNavigator;
import wicket.model.Model;
import wicket.model.PropertyModel;

/**
 * Page that displays a list of books and lets the user re-order them.
 * 
 * @author Jonathan Locke
 */
public final class Home extends AuthenticatedWebPage
{
	/**
	 * Constructor
	 * 
	 * @param parameters
	 */
	public Home(final PageParameters parameters)
	{
		// Add table of books
		final PageableListView listView;
		add(listView = new PageableListView(this, "books", new PropertyModel(this, "books"), 4)
		{
			@Override
			public void populateItem(final ListItem listItem)
			{
				final Book book = (Book)listItem.getModelObject();
				listItem.add(BookDetails.link(listItem, "details", book, getLocalizer().getString(
						"noBookTitle", this)));
				listItem.add(new Label(listItem, "author", new Model(book)));
				listItem.add(moveUpLink(listItem, "moveUp", listItem));
				listItem.add(moveDownLink(listItem, "moveDown", listItem));
				listItem.add(removeLink(listItem, "remove", listItem));
				listItem.add(EditBook.link(listItem, "edit", book.getId()));
			}
		});
		add(new PagingNavigator(this, "navigator", listView));
	}

	/**
	 * 
	 * @return List of books
	 */
	public List getBooks()
	{
		// Note: checkAccess() (and thus login etc.) happen after the Page
		// has been instantiated. Thus, you can not realy on user != null.
		// Note2: In any case, all components must be associated with a
		// wicket tag.
		User user = getLibrarySession().getUser();
		if (user == null)
		{
			return new ArrayList();
		}

		return user.getBooks();
	}
}
