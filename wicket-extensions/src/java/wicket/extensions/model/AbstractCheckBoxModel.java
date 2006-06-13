/*
 * $Id: Add.java 5883 2006-05-26 10:12:48Z joco01 $ $Revision: 5883 $ $Date:
 * 2006-05-26 00:46:21 +0200 (vr, 26 mei 2006) $
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
package wicket.extensions.model;

import wicket.Component;
import wicket.model.IModel;

/**
 * Model adapter that makes working with models for checkboxes easier.
 * 
 * @author Igor Vaynberg (ivaynberg)
 * 
 * @deprecated I think this model can be got rid of now we have generics, no?
 */
public abstract class AbstractCheckBoxModel implements IModel<Boolean>
{
	private static final long serialVersionUID = 1L;

	/**
	 * @see wicket.model.IDetachable#detach()
	 */
	public void detach()
	{
	}

	/**
	 * @see wicket.model.IModel#getNestedModel()
	 */
	public IModel getNestedModel()
	{
		return null;
	}

	/**
	 * @see wicket.model.IModel#getObject()
	 */
	public Boolean getObject()
	{
		return isSelected(component) ? Boolean.TRUE : Boolean.FALSE;
	}


	/**
	 * Returns model's value
	 * 
	 * @param component
	 * @return true to indicate the checkbox should be selected, false otherwise
	 */
	public abstract boolean isSelected(Component component);

	/**
	 * @see wicket.model.IModel#setObject(java.lang.Object)
	 */
	public void setObject(Boolean object)
	{
		boolean sel = Boolean.TRUE.equals(object);
		setSelected(component, sel);
	}


	/**
	 * Callback for setting the model's value to true or false
	 * 
	 * @param component
	 * @param sel
	 *            true if the checkbox is selected, false otherwise
	 */
	public abstract void setSelected(Component component, boolean sel);
}
