/*
 * $Id: BoxBorderTest.java 4831 2006-03-08 13:32:22 -0800 (Wed, 08 Mar 2006)
 * jdonnerstag $ $Revision$ $Date: 2006-03-08 13:32:22 -0800 (Wed, 08 Mar
 * 2006) $
 * 
 * ========================================================================
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
package wicket.markup.html.border;

import wicket.Page;
import wicket.WicketRuntimeException;
import wicket.WicketTestCase;
import wicket.markup.html.form.Form;
import wicket.markup.html.form.TextField;
import wicket.protocol.http.MockHttpServletRequest;
import wicket.util.diff.DiffUtil;

/**
 * Test the component: PageView
 * 
 * @author Juergen Donnerstag
 */
public class BoxBorderTest extends WicketTestCase
{
	// private static final Log log = LogFactory.getLog(BoxBorderTest.class);

	/**
	 * Create the test.
	 * 
	 * @param name
	 *            The test name
	 */
	public BoxBorderTest(String name)
	{
		super(name);
	}

	/**
	 * Test a simply page containing the debug component
	 * 
	 * @throws Exception
	 */
	public void test1() throws Exception
	{
		executeTest(BoxBorderTestPage_1.class, "BoxBorderTestPage_ExpectedResult_1.html");
	}

	/**
	 * Test a simply page containing the debug component
	 * 
	 * @throws Exception
	 */
	public void test2() throws Exception
	{
		executeTest(BoxBorderTestPage_2.class, "BoxBorderTestPage_ExpectedResult_2.html");
	}

	/**
	 * Test a simply page containing the debug component
	 * 
	 * @throws Exception
	 */
	public void test3() throws Exception
	{
		executeTest(BoxBorderTestPage_3.class, "BoxBorderTestPage_ExpectedResult_3.html");

		application.getLastRenderedPage().get("border");
		Form form = (Form)application.getLastRenderedPage().get("border:myForm");

		TextField input = (TextField)application.getLastRenderedPage().get("border:myForm:borderBody:name");
		assertEquals("", input.getModelObjectAsString());

		application.setupRequestAndResponse();

		MockHttpServletRequest mockRequest = application.getServletRequest();
		mockRequest.setRequestToComponent(form);
		mockRequest.setParameter(input.getInputName(), "jdo");

		application.processRequestCycle();

		input = (TextField)application.getLastRenderedPage().get("border:myForm:borderBody:name");
		assertEquals("jdo", input.getModelObjectAsString());
	}

	/**
	 * Test to ensure WicketRuntimeException is thrown when Markup and Object hierarchy
	 * does not match with a Border involved.
	 */
	public void test4()
	{
		Class<? extends Page> pageClass = BorderTestHierarchyPage_4.class;

		System.out.println("=== " + pageClass.getName() + " ===");
		application.setHomePage(pageClass);
		application.setupRequestAndResponse();

		WicketRuntimeException wicketRuntimeException = null;
		try
		{
			application.processRequestCycle();
		}
		catch (WicketRuntimeException e)
		{
			wicketRuntimeException = e;
		}

		assertNotNull("Markup does not match component hierarchy, but exception not thrown.",
				wicketRuntimeException);
	}

	/**
	 * Test to ensure border render wrapped settings functions properly.
	 * 
	 * @throws Exception
	 */
	public void testRenderWrapped() throws Exception
	{
		executeTest(BorderRenderWrappedTestPage_1.class,
				"BorderRenderWrappedTestPage_ExpectedResult_1.html");
	}

	/**
	 * Test a simply page containing the debug component
	 * 
	 * @throws Exception
	 */
	public void test5() throws Exception
	{
		executeTest(BoxBorderTestPage_5.class, "BoxBorderTestPage_ExpectedResult_5.html");
	}

	/**
	 * Test a simply page containing the debug component
	 * 
	 * @throws Exception
	 */
	public void test6() throws Exception
	{
		executeTest(BoxBorderTestPage_6.class, "BoxBorderTestPage_ExpectedResult_6.html");
	}

	/**
	 * Test a simply page containing the debug component
	 * 
	 * @throws Exception
	 */
	public void test7() throws Exception
	{
		executeTest(BoxBorderTestPage_7.class, "BoxBorderTestPage_ExpectedResult_7.html");
		
		application.clickLink("link");

		String document = application.getServletResponse().getDocument();
		assertTrue(DiffUtil.validatePage(document, this.getClass(), "BoxBorderTestPage_ExpectedResult_7-1.html"));
	}
}