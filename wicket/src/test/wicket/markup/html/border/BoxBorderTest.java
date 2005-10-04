/*
 * $Id$
 * $Revision$ $Date$
 * 
 * ======================================================================== 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may 
 * not use this file except in compliance with the License. You may obtain 
 * a copy of the License at
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import wicket.WicketTestCase;

/**
 * Test the component: WicketComponentTree
 * 
 * @author Juergen Donnerstag
 */
public class BoxBorderTest extends WicketTestCase
{
	private static Log log = LogFactory.getLog(BoxBorderTest.class);

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
	 * @throws Exception
	 */
	public void test1() throws Exception
	{
		executeTest(BoxBorderTestPage.class, "BoxBorderTestPage_ExpectedResult.html");
	}
	
	/**
	 * Test a simply page containing the debug component
	 * @throws Exception
	 */
	public void test2() throws Exception
	{
		executeTest(BoxBorderTestPage_2.class, "BoxBorderTestPage_ExpectedResult_2.html");
	}
}