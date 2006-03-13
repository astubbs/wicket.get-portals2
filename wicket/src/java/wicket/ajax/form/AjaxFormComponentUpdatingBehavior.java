/*
 * $Id: AjaxFormComponentUpdatingBehavior.java,v 1.4 2006/02/02 18:49:46
 * ivaynberg Exp $ $Revision$ $Date: 2006-03-09 01:08:00 -0800 (Thu, 09
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
package wicket.ajax.form;

import wicket.WicketRuntimeException;
import wicket.ajax.AjaxEventBehavior;
import wicket.ajax.AjaxRequestTarget;
import wicket.markup.html.form.FormComponent;

/**
 * A behavior that updates the hosting FormComponent via ajax when an event it
 * is attached to is triggered.
 */
public abstract class AjaxFormComponentUpdatingBehavior extends AjaxEventBehavior
{
	/**
	 * Construct.
	 * 
	 * @param event
	 *            event to trigger this behavior
	 */
	public AjaxFormComponentUpdatingBehavior(final String event)
	{
		super(event);
	}

	/**
	 * 
	 * @see wicket.behavior.AbstractAjaxBehavior#onBind()
	 */
	protected void onBind()
	{
		if (!(getComponent() instanceof FormComponent))
		{
			throw new WicketRuntimeException("Behavior " + getClass().getName()
					+ " can only be added to an isntance of a FormComponent");
		}
	}


	/**
	 * 
	 * @return FormComponent
	 */
	protected final FormComponent getFormComponent()
	{
		return (FormComponent)getComponent();
	}

	/**
	 * @see wicket.ajax.AjaxEventBehavior#getEventHandler()
	 */
	protected final String getEventHandler()
	{
		return getCallbackScript("wicketAjaxPost('"+getCallbackUrl()+"', wicketSerialize(this)");
	}

	/**
	 * @see wicket.ajax.AjaxEventBehavior#onCheckEvent(java.lang.String)
	 */
	protected void onCheckEvent(String event)
	{
		if ("href".equalsIgnoreCase(event))
		{
			throw new IllegalArgumentException(
					"this behavior cannot be attached to an 'href' event");
		}
	}


	/**
	 * 
	 * @see wicket.ajax.AjaxEventBehavior#onEvent(wicket.ajax.AjaxRequestTarget)
	 */
	protected final void onEvent(final AjaxRequestTarget target)
	{
		final FormComponent formComponent = getFormComponent();
		formComponent.inputChanged();
		formComponent.validate();
		if (formComponent.hasErrorMessage())
		{
			formComponent.invalid();
		}
		else
		{
			formComponent.valid();
			formComponent.updateModel();
			// TODO Ajax: Do we need to persist values for persistent components
		}

		onUpdate(target);
	}

	/**
	 * Listener invoked on the ajax request. This listener is invoked after the
	 * component's model has been updated.
	 * 
	 * @param target
	 */
	protected abstract void onUpdate(final AjaxRequestTarget target);
}
