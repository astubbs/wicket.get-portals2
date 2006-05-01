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

/**
 * Models a cancel button in the wizard. When pressed, it calls
 * {@link Wizard#onCancel()} which should do the real work.
 * 
 * @author Eelco Hillenius
 */
public final class CancelButton extends WizardButton
{
	private static final long serialVersionUID = 1L;

	/**
	 * Construct.
	 * 
	 * @param id
	 *            The component id
	 * @param wizard
	 *            The wizard
	 */
	public CancelButton(String id, Wizard wizard)
	{
		super(id, wizard, "wicket.extensions.wizard.cancel");
		setDefaultFormProcessing(false);
	}

	/**
	 * @see wicket.extensions.wizard.WizardButton#doAction()
	 */
	public void doAction()
	{
		getWizard().onCancel();
	}

	/**
	 * @see wicket.Component#isEnabled()
	 */
	public boolean isEnabled()
	{
		return true;
	}

	/**
	 * @see wicket.Component#isVisible()
	 */
	public boolean isVisible()
	{
		return getWizardModel().isCancelVisible();
	}
}