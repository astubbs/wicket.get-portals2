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
package wicket.markup.html.form;

import wicket.IRequestListener;

/**
 * Listener method for OnChange events of dropdown lists and onclick events of
 * CheckBoxes and RadioChoice components. When any of those components
 * wantOnSelectionChangedNotifications() method returns true, a javascript
 * onchange or onclick handler will be generated that calls this interface
 * method when the user changes the selection.
 * 
 * @author Eelco Hillenius
 */
public interface IOnChangeListener extends IRequestListener
{
	/**
	 * Called when a new option is selected.
	 */
	void onSelectionChanged();
}
