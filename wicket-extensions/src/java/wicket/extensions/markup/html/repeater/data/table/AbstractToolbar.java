/*
 * $Id: AbstractToolbar.java 5840 2006-05-24 13:49:09 -0700 (Wed, 24 May 2006)
 * joco01 $ $Revision$ $Date: 2006-05-24 13:49:09 -0700 (Wed, 24 May
 * 2006) $
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
package wicket.extensions.markup.html.repeater.data.table;

import wicket.MarkupContainer;
import wicket.markup.html.panel.Panel;
import wicket.model.IModel;

/**
 * A base class for data table toolbars
 * 
 * @param <V>
 *            type of model
 * 
 * 
 * @see DefaultDataTable
 * 
 * @author Igor Vaynberg (ivaynberg)
 * 
 */
public abstract class AbstractToolbar<V> extends Panel<V>
{
	private static final long serialVersionUID = 1L;

	private DataTable table;

	/**
	 * Constructor
	 * 
	 * @param parent
	 *            parent component
	 * @param id
	 *            component id
	 * 
	 * @param model
	 *            model
	 * @param table
	 *            data table this toolbar will be attached to
	 */
	public AbstractToolbar(MarkupContainer parent, String id, IModel<V> model, DataTable table)
	{
		super(parent, id, model);
		this.table = table;
	}

	/**
	 * Constructor
	 * 
	 * @param parent
	 *            parent component
	 * @param id
	 *            component id
	 * 
	 * @param table
	 *            data table this toolbar will be attached to
	 */
	public AbstractToolbar(MarkupContainer parent, String id, DataTable table)
	{
		super(parent, DataTable.TOOLBAR_COMPONENT_ID);
		this.table = table;
	}

	/**
	 * @return DataTable this toolbar is attached to
	 */
	protected DataTable getTable()
	{
		return table;
	}
}
