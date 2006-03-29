/*
 * $Id: AjaxSubmitButton.java 5125 2006-03-25 11:42:10 -0800 (Sat, 25 Mar 2006)
 * ivaynberg $ $Revision: 5155 $ $Date: 2006-03-25 11:42:10 -0800 (Sat, 25 Mar
 * 2006) $
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
package wicket.ajax.markup.html.form;

import wicket.ajax.AjaxRequestTarget;
import wicket.ajax.form.AjaxFormSubmitBehavior;
import wicket.markup.ComponentTag;
import wicket.markup.html.WebComponent;
import wicket.markup.html.form.Form;

/**
 * A link that submits a form via ajax. Since this link takes the form as a
 * constructor argument it does not need to be inside form's component
 * hierarchy.
 * 
 * @since 1.2
 * 
 * @author Igor Vaynberg (ivaynberg)
 */
public abstract class AjaxSubmitLink extends WebComponent
{
	private static final long serialVersionUID = 1L;

	/**
	 * Construct.
	 * 
	 * @param id
	 * @param form
	 */
	public AjaxSubmitLink(String id, Form form)
	{
		super(id);

		add(new AjaxFormSubmitBehavior(form, "href")
		{

			private static final long serialVersionUID = 1L;

			protected void onSubmit(AjaxRequestTarget target)
			{
				AjaxSubmitLink.this.onSubmit(target);
			}
		});

	}

	protected void onComponentTag(ComponentTag tag)
	{
		checkComponentTag(tag, "a");
		super.onComponentTag(tag);
	}

	/**
	 * Listener method invoked on form submit
	 * 
	 * @param target
	 */
	protected abstract void onSubmit(AjaxRequestTarget target);


}
