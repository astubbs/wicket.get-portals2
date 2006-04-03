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
package wicket.examples.displaytag.utils;

import wicket.markup.ComponentTag;
import wicket.markup.MarkupStream;
import wicket.markup.html.basic.Label;
import wicket.markup.html.list.PageableListView;
import wicket.markup.html.navigation.paging.PagingNavigator;

/**
 * @author Juergen Donnerstag
 */
public class MyPageableListViewNavigator extends PagingNavigator
{
    /**
     * 
     * @param id
     * @param pageableListView
     */
    public MyPageableListViewNavigator(final String id,
		final PageableListView pageableListView)
    {
        super(id, pageableListView);

		// model = null; the headline text will be auto-generated during
		// handleBody.
		add(new Label("headline", (String)null)
		{
			// Dynamically - at runtime - create the text
			protected void onComponentTagBody(final MarkupStream markupStream,
					final ComponentTag openTag)
			{
				CharSequence text = getHeadlineText(pageableListView);
				replaceComponentTagBody(markupStream, openTag, text);
			}
		});
    }

	/**
	 * Subclasses may override it to provide their own text.
	 * 
	 * @param pageableListView
	 *            the pageable list view
	 * @return head line text
	 */
	protected CharSequence getHeadlineText(final PageableListView pageableListView)
	{
		int firstListItem = pageableListView.getCurrentPage() * pageableListView.getRowsPerPage();
		StringBuffer buf = new StringBuffer(80);
		buf.append(String.valueOf(pageableListView.getList().size())).append(
				" items found, displaying ").append(String.valueOf(firstListItem + 1)).append(
				" to ").append(String.valueOf(firstListItem + pageableListView.getRowsPerPage()))
				.append(".");

		return buf;
	}
}
