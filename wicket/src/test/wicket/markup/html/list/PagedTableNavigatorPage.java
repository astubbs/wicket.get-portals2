/*
 * $Id: PagedTableNavigatorPage.java 5844 2006-05-24 20:53:56 +0000 (Wed, 24 May
 * 2006) joco01 $ $Revision$ $Date: 2006-05-24 20:53:56 +0000 (Wed, 24
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
package wicket.markup.html.list;

import java.util.ArrayList;
import java.util.List;

import wicket.markup.html.WebPage;
import wicket.markup.html.basic.Label;
import wicket.markup.html.navigation.paging.PagingNavigator;

/**
 * Dummy page used for resource testing.
 */
public class PagedTableNavigatorPage extends WebPage
{
	private static final long serialVersionUID = 1L;

	/**
	 * Construct. page parameters.
	 */
	public PagedTableNavigatorPage()
	{
		super();
		List list = new ArrayList();
		list.add("one");
		list.add("two");
		list.add("three");
		list.add("four");
		list.add("five");
		list.add("six");
		list.add("seven");
		list.add("eight");

		PageableListView table = new PageableListView(this, "table", list, 2)
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem listItem)
			{
				String txt = (String)listItem.getModelObject();
				new Label(listItem, "txt", txt);
			}
		};

		new PagingNavigator(this, "navigator", table);
	}

	/**
	 * @see wicket.Component#isVersioned()
	 */
	@Override
	public boolean isVersioned()
	{
		// for testing we set versioning off, because it gets too difficult to
		// maintain otherwise
		return false;
	}
}
