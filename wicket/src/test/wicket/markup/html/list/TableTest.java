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
package wicket.markup.html.list;

import java.util.ArrayList;

import wicket.markup.html.list.ListItem;
import wicket.markup.html.list.PageableListView;
import wicket.model.Model;

import junit.framework.TestCase;


/**
 * Test for tables.
 * @author Juergen Donnerstag
 */
public class TableTest extends TestCase
{
	/**
	 * creates a table.
	 * @param modelListSize
	 * @param pageSize size of a page
	 * @return table
	 */
	private PageableListView createTable(final int modelListSize, final int pageSize)
	{
		ArrayList modelList = new ArrayList();
		for (int i = 0; i < modelListSize; i++)
		{
			modelList.add(new Integer(i));
		}

		return new PageableListView("table", new Model(modelList), pageSize)
		{
			private static final long serialVersionUID = 1L;

			protected void populateItem(final ListItem listItem)
			{
				// do nothing
			}
		};
	}

	/**
	 * 
	 */
	public void testTable()
	{
		PageableListView table = createTable(20, 4);
		assertEquals(4, table.getRowsPerPage());
		assertEquals(0, table.getCurrentPage());
		assertEquals(5, table.getPageCount());
		assertEquals(4, table.getViewSize());

		table = createTable(20, 6);
		assertEquals(6, table.getRowsPerPage());
		assertEquals(0, table.getCurrentPage());
		assertEquals(4, table.getPageCount());
		assertEquals(6, table.getViewSize());

		table.setCurrentPage(1);
		assertEquals(6, table.getRowsPerPage());
		assertEquals(1, table.getCurrentPage());
		assertEquals(4, table.getPageCount());
		assertEquals(6, table.getViewSize());
		assertEquals(6, table.getStartIndex());

		table.setCurrentPage(3);
		assertEquals(6, table.getRowsPerPage());
		assertEquals(3, table.getCurrentPage());
		assertEquals(4, table.getPageCount());
		assertEquals(2, table.getViewSize());
		assertEquals(18, table.getStartIndex());
	}

	/**
	 * 
	 */
	public void testEmptyTable()
	{
		PageableListView table = createTable(0, 4);
		assertEquals(4, table.getRowsPerPage());
		assertEquals(0, table.getCurrentPage());
		assertEquals(0, table.getPageCount());
		assertEquals(0, table.getViewSize());

		// null tables are a special case used for table navigation
		// bar, where there is no underlying model necessary, as
		// listItem.getIndex() is equal to the required listItem.getModelObject()
		table = new PageableListView("table", new Model(null), 10)
		{
			private static final long serialVersionUID = 1L;

			protected void populateItem(final ListItem listItem)
			{
				// do nothing
			}
		};
		assertEquals(0, table.getStartIndex());
		assertEquals(0, table.getViewSize());

		// These 2 methods are deliberately not available for Tables
		// table.setStartIndex(5);
		// table.setViewSize(10);
	}
}
