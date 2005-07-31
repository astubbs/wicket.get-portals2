/*
 * $Id$
 * $Revision$ $Date$
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
package wicket.markup.html.list;

import java.util.List;

import wicket.model.IModel;
import wicket.version.undo.Change;

/**
 * PageableListView is similar to ListView but provides in addition pageable
 * views. A PageableListView holds pageable rows of information. The rows can be
 * re-ordered and deleted, either one at a time or many at a time.
 * 
 * @author Jonathan Locke
 */
public abstract class PageableListView extends ListView implements IPageableComponent
{
	/** The page to show. */
	private int currentPage;

	/** Number of rows per page of the list view. */
	private int rowsPerPage;

	/**
	 * Constructor
	 * 
	 * @param id
	 *            See Component
	 * @param model
	 *            See Component
	 * @param rowsPerPage
	 *            Number of rows to show on a page
	 */
	public PageableListView(final String id, final IModel model, int rowsPerPage)
	{
		super(id, model);
		this.rowsPerPage = rowsPerPage;
	}

	/**
	 * Creates a pagable list view having the given number of rows per page that
	 * uses the provided object as a simple model.
	 * 
	 * @param id
	 *            See Component
	 * @param list
	 *            See Component
	 * @param rowsPerPage
	 *            Number of rows to show on a page
	 * @see ListView#ListView(String, List)
	 */
	public PageableListView(final String id, final List list, final int rowsPerPage)
	{
		super(id, list);
		this.rowsPerPage = rowsPerPage;
	}

	/**
	 * Gets the index of the current page being displayed by this list view.
	 * 
	 * @return Returns the currentPage.
	 */
	public final int getCurrentPage()
	{
		// If first cell is out of range, bring page back into range
		while ((currentPage * rowsPerPage) > getList().size())
		{
			currentPage--;
		}

		return currentPage;
	}

	/**
	 * Gets the number of pages in this list view.
	 * 
	 * @return The number of pages in this list view
	 */
	public final int getPageCount()
	{
		return ((getList().size() + rowsPerPage) - 1) / rowsPerPage;
	}

	/**
	 * Gets the maximum number of rows on each page.
	 * 
	 * @return the maximum number of rows on each page.
	 */
	public final int getRowsPerPage()
	{
		return rowsPerPage;
	}

	/**
	 * Sets the maximum number of rows on each page.
	 * 
	 * @param rowsPerPage the maximum number of rows on each page.
	 */
	public final void setRowsPerPage(int rowsPerPage)
	{
		if (rowsPerPage < 0)
		{
			rowsPerPage = 0;
		}

		addStateChange(new RowsPerPageChange(this.rowsPerPage));
		this.rowsPerPage = rowsPerPage;
	}

	/**
	 * @see wicket.markup.html.list.ListView#getViewSize()
	 */
	public int getViewSize()
	{
		if (this.getModelObject() != null)
		{
			super.setStartIndex(this.getCurrentPage() * this.getRowsPerPage());
			super.setViewSize(this.getRowsPerPage());
		}

		return super.getViewSize();
	}

	/**
	 * Sets the current page that this list view should show.
	 * 
	 * @param currentPage
	 *            The currentPage to set.
	 */
	public final void setCurrentPage(int currentPage)
	{
		if (currentPage < 0)
		{
			currentPage = 0;
		}

		int pageCount = getPageCount();
		if ((currentPage > 0) && (currentPage >= pageCount))
		{
			currentPage = pageCount - 1;
		}
		
		addStateChange(new CurrentPageChange(this.currentPage));
		this.currentPage = currentPage;
	}


	/**
	 * Prevent users from accidentially using it. Throw an
	 * IllegalArgumentException.
	 * 
	 * @see wicket.markup.html.list.ListView#setStartIndex(int)
	 */
	public ListView setStartIndex(int startIndex) throws IllegalArgumentException
	{
		throw new UnsupportedOperationException(
				"You must not use setStartIndex() with PageableListView");
	}

	/**
	 * Prevent users from accidentially using it. Throw an
	 * IllegalArgumentException.
	 * 
	 * @param size
	 *            the view size
	 * @return This
	 * @throws IllegalArgumentException
	 * @see wicket.markup.html.list.ListView#setStartIndex(int)
	 */
	public ListView setViewSize(int size) throws IllegalArgumentException
	{
		throw new UnsupportedOperationException(
				"You must not use setViewSize() with PageableListView");
	}
	
	/**
	 * Records the changing of the current page.
	 */
	private class CurrentPageChange extends Change
	{
		/** the former 'current' page. */
		private int currentPage;

		/**
		 * Construct.
		 * @param currentPage the former 'current' page
		 */
		CurrentPageChange(int currentPage)
		{
			this.currentPage = currentPage;
		}
		/**
		 * @see wicket.version.undo.Change#undo()
		 */
		public void undo()
		{
			setCurrentPage(currentPage);
		}
	}

	/**
	 * Records the changing of the nbr of rows per page.
	 */
	private class RowsPerPageChange extends Change
	{
		/** the former nbr of rows per page. */
		private int rowsPerPage;

		/**
		 * Construct.
		 * @param rowsPerPage the former nbr of rows per page
		 */
		RowsPerPageChange(int rowsPerPage)
		{
			this.rowsPerPage = rowsPerPage;
		}
		/**
		 * @see wicket.version.undo.Change#undo()
		 */
		public void undo()
		{
			setRowsPerPage(rowsPerPage);
		}
	}

	/**
	 * Returns the total number of items in the listview
	 * 
	 * @see IPageableComponent#getItemCount()
	 */
	public int getItemCount()
	{
		return getList().size();
	}
	
	
	
}