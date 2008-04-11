/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.wicket.markup.html.form;

import org.apache.wicket.Page;
import org.apache.wicket.WicketTestCase;
import org.apache.wicket.markup.html.form.NestedFormsPage.NestableForm;
import org.apache.wicket.util.tester.FormTester;

/**
 * @see <a href="http://cwiki.apache.org/WICKET/nested-forms.html">"Specification"</a> of nested
 *      forms handling
 * @author Gerolf Seitz
 */
public class FormSubmitTest extends WicketTestCase
{

	private Page page;
	private NestableForm outerForm;
	private NestableForm middleForm;
	private NestableForm innerForm;

	protected void setUp() throws Exception
	{
		super.setUp();
		tester.startPage(new NestedFormsPage());
		page = tester.getLastRenderedPage();
		outerForm = (NestableForm)page.get("outerForm");
		middleForm = (NestableForm)page.get("outerForm:middleForm");
		innerForm = (NestableForm)page.get("outerForm:middleForm:innerForm");
	}


	/**
	 * 
	 */
	public void testAllFormsEnabledSubmitOuterForm()
	{
		assertEnabledState(true, true, true);

		FormTester formTester = tester.newFormTester("outerForm");
		formTester.submit("submit");

		assertOnSubmitCalled(true, true, true);
		assertOnErrorCalled(false, false, false);
	}

	/**
	 * 
	 */
	public void testAllFormsEnabledSubmitMiddleForm()
	{
		assertEnabledState(true, true, true);

		FormTester formTester = tester.newFormTester("outerForm:middleForm");
		formTester.submit("submit");

		assertOnSubmitCalled(false, true, true);
		assertOnErrorCalled(false, false, false);
	}

	/**
	 * 
	 */
	public void testAllFormsEnabledSubmitInnerForm()
	{
		assertEnabledState(true, true, true);

		FormTester formTester = tester.newFormTester("outerForm:middleForm:innerForm");
		formTester.submit("submit");

		assertOnSubmitCalled(false, false, true);
		assertOnErrorCalled(false, false, false);
	}


	/**
	 * 
	 */
	public void testMiddleFormDisabledSubmitOuterForm()
	{
		// disable middle form
		middleForm.setEnabled(false);
		assertEnabledState(true, false, true);

		// submit outer form
		FormTester formTester = tester.newFormTester("outerForm");
		formTester.submit("submit");

		assertOnSubmitCalled(true, false, false);
		assertOnErrorCalled(false, false, false);
	}

	/**
	 * 
	 */
	public void testInnerFormDisabledSubmitOuterForm()
	{
		// disable middle form
		innerForm.setEnabled(false);
		assertEnabledState(true, true, false);

		// submit outer form
		FormTester formTester = tester.newFormTester("outerForm");
		formTester.submit("submit");

		assertOnSubmitCalled(true, true, false);
		assertOnErrorCalled(false, false, false);
	}

	/**
	 * 
	 */
	public void testSubmitDisabledOuterForm()
	{
		outerForm.setEnabled(false);
		assertEnabledState(false, true, true);

		FormTester formTester = tester.newFormTester("outerForm");
		formTester.submit("submit");

		assertOnSubmitCalled(false, false, false);
		assertOnErrorCalled(false, false, false);
	}

	/**
	 * 
	 */
	public void testErrorOnInnerFormSubmitOuterForm()
	{
		assertEnabledState(true, true, true);

		causeValidationErrorAndSubmit("outerForm", "middleForm:innerForm:first");

		assertOnSubmitCalled(false, false, false);
		assertOnErrorCalled(true, true, true);
	}

	/**
	 * 
	 */
	public void testErrorOnMiddleFormSubmitOuterForm()
	{
		assertEnabledState(true, true, true);

		causeValidationErrorAndSubmit("outerForm", "middleForm:first");

		assertOnSubmitCalled(false, false, false);
		assertOnErrorCalled(true, true, false);
	}

	/**
	 * 
	 */
	public void testErrorOnMiddleFormSubmitMiddleForm()
	{
		assertEnabledState(true, true, true);

		causeValidationErrorAndSubmit("outerForm:middleForm", "first");

		assertOnSubmitCalled(false, false, false);
		assertOnErrorCalled(false, true, false);
	}

	/**
	 * 
	 */
	public void testErrorOnInnerFormSubmitMiddleForm()
	{
		assertEnabledState(true, true, true);

		causeValidationErrorAndSubmit("outerForm:middleForm", "innerForm:first");

		assertOnSubmitCalled(false, false, false);
		assertOnErrorCalled(false, true, true);
	}

	/**
	 * 
	 */
	public void testMiddleFormDisabledErrorOnOuterFormSubmitOuterForm()
	{
		middleForm.setEnabled(false);
		assertEnabledState(true, false, true);

		causeValidationErrorAndSubmit("outerForm", "first");

		assertOnSubmitCalled(false, false, false);
		assertOnErrorCalled(true, false, false);
	}

	/**
	 * 
	 */
	public void testErrorOnInnerFormDisabledMiddleFormSubmitOuterForm()
	{
		middleForm.setEnabled(false);
		assertEnabledState(true, false, true);

		causeValidationErrorAndSubmit("outerForm", "middleForm:innerForm:first");

		assertOnSubmitCalled(true, false, false);
		assertOnErrorCalled(false, false, false);
	}


	private void assertEnabledState(boolean isOuterFormEnabled, boolean isMiddleFormEnabled,
		boolean isInnerFormEnabled)
	{
		assertEquals(isOuterFormEnabled, outerForm.isEnabled());
		assertEquals(isMiddleFormEnabled, middleForm.isEnabled());
		assertEquals(isInnerFormEnabled, innerForm.isEnabled());
	}


	private void assertOnErrorCalled(boolean isOuterFormOnErrorCalled,
		boolean isMiddleFormOnErrorCalled, boolean isInnerFormOnErrorCalled)
	{
		assertEquals(isOuterFormOnErrorCalled, outerForm.onErrorCalled);
		assertEquals(isMiddleFormOnErrorCalled, middleForm.onErrorCalled);
		assertEquals(isInnerFormOnErrorCalled, innerForm.onErrorCalled);
	}


	private void assertOnSubmitCalled(boolean isOuterFormOnSubmitCalled,
		boolean isMiddleFormOnSubmitCalled, boolean isInnerFormOnSubmitCalled)
	{
		assertEquals(isOuterFormOnSubmitCalled, outerForm.onSubmitCalled);
		assertEquals(isMiddleFormOnSubmitCalled, middleForm.onSubmitCalled);
		assertEquals(isInnerFormOnSubmitCalled, innerForm.onSubmitCalled);
	}


	/**
	 * @param formToBeSubmitted
	 *            absolute path of the form to be submitted
	 * @param componentToGetError
	 *            relative path to <code>formToBeSumitted</code> of the component to be changed
	 * @return a {@link FormTester} instance
	 */
	private FormTester causeValidationErrorAndSubmit(String formToBeSubmitted,
		String componentToGetError)
	{
		FormTester formTester;
		formTester = tester.newFormTester(formToBeSubmitted);
		formTester.setValue(componentToGetError, "");
		formTester.submit("submit");
		return formTester;
	}

}
