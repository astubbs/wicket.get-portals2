/*
 * $Id: org.eclipse.jdt.ui.prefs 5004 2006-03-17 20:47:08 -0800 (Fri, 17 Mar
 * 2006) eelco12 $ $Revision: 5004 $ $Date: 2006-03-17 20:47:08 -0800 (Fri, 17
 * Mar 2006) $
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
package wicket.extensions.wizard;

import wicket.markup.html.form.Button;
import wicket.model.ResourceModel;

/**
 * Base class for buttons that work with {@link Wizard the wizard component}.
 * It uses resource bundles to display the button label.
 * <p>
 * When wizard buttons are presses (and they pass validation if that is
 * relevant), they pass control to {@link #doAction() their action method},
 * which should do the real work.
 * </p>
 * 
 * @author Eelco Hillenius
 */
public abstract class WizardButton extends Button
{
	/**
	 * The enclosing wizard.
	 */
	private final Wizard wizard;

	/**
	 * Construct.
	 * 
	 * @param id
	 *            The component id
	 * @param wizard
	 *            The wizard
	 * @param labelResourceKey
	 *            The resource key of the button's label
	 */
	public WizardButton(String id, Wizard wizard, String labelResourceKey)
	{
		super(id, new ResourceModel(labelResourceKey));
		this.wizard = wizard;
	}

	/**
	 * Called when this button is clicked.
	 */
	protected abstract void doAction();

	/**
	 * Gets the {@link Wizard}.
	 * 
	 * @return The wizard
	 */
	protected final Wizard getWizard()
	{
		return wizard;
	}

	/**
	 * Gets the {@link IWizardModel wizard model}.
	 * 
	 * @return The wizard model
	 */
	protected final IWizardModel getWizardModel()
	{
		return getWizard().getWizardModel();
	}

	/**
	 * @see wicket.markup.html.form.Button#onSubmit()
	 */
	protected final void onSubmit()
	{
		doAction();
	}
}