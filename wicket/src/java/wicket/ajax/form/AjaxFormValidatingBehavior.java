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
package wicket.ajax.form;

import wicket.Component;
import wicket.Component.IVisitor;
import wicket.ajax.AjaxRequestTarget;
import wicket.markup.html.form.Form;
import wicket.markup.html.form.FormComponent;
import wicket.markup.html.panel.FeedbackPanel;

/**
 * Ajax event behavior that submits the form and updates all form feedback
 * panels on the page. Useful for providing instant feedback.
 * 
 * @author Igor Vaynberg (ivaynberg)
 */
public class AjaxFormValidatingBehavior extends AjaxFormSubmitBehavior
{
	private static final long serialVersionUID = 1L;

	/**
	 * Construct.
	 * 
	 * @param form
	 *            form that will be submitted via ajax
	 * @param event
	 *            javascript event this behavior will be invoked on, like
	 *            onclick
	 */
	public AjaxFormValidatingBehavior(Form form, String event)
	{
		super(form, event);
	}

	protected void onSubmit(final AjaxRequestTarget target)
	{
		getComponent().getPage().visitChildren(FeedbackPanel.class, new IVisitor()
		{
			public Object component(Component component)
			{
				target.addComponent(component);
				return IVisitor.CONTINUE_TRAVERSAL;
			}

		});
	}

	/**
	 * Adds this behavior to all form components of the specified form
	 * 
	 * @param form
	 * @param event
	 */
	public static void addToAllFormComponents(final Form form, final String event)
	{
		form.visitChildren(FormComponent.class, new IVisitor()
		{

			public Object component(Component component)
			{
				component.add(new AjaxFormValidatingBehavior(form, event));
				return IVisitor.CONTINUE_TRAVERSAL_BUT_DONT_GO_DEEPER;
			}

		});
	}

}
