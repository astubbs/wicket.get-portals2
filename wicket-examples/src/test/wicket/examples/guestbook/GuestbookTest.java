/*
 * $Id$ $Revision$
 * $Date$
 * 
 * ==================================================================== Licensed
 * under the Apache License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the
 * License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package wicket.examples.guestbook;

import junit.framework.Test;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import wicket.examples.WicketWebTestCase;

/**
 * jWebUnit test for Hello World.
 */
public class GuestbookTest extends WicketWebTestCase
{
	private static Log log = LogFactory.getLog(GuestbookTest.class);

	/**
	 * 
	 * @return Test
	 */
	public static Test suite()
	{
		return suite(GuestbookTest.class);
	}

	/**
	 * Construct.
	 * 
	 * @param name
	 *            name of test
	 */
	public GuestbookTest(String name)
	{
		super(name);
	}

	/**
	 * Sets up the test.
	 * 
	 * @throws Exception
	 */
	public void setUp() throws Exception
	{
		super.setUp();
		GuestBook.clear();
	}

	/**
	 * Test page.
	 * 
	 * @throws Exception
	 */
	public void test_1() throws Exception
	{
		beginAt("/guestbook");

		this.dumpResponse(System.out);
		assertTitleEquals("Wicket Examples - guestbook");
		// this.assertXpathNodeNotPresent("//*[@wicket:id='comments']");
		this.assertElementNotPresent("comments");

		assertFormPresent("commentForm");
		this.assertFormElementPresent("text");
		this.setFormElement("text", "test-1");
		this.submit();

		this.dumpResponse(System.err);
		assertTitleEquals("Wicket Examples - guestbook");
		assertFormPresent("commentForm");
		this.assertFormElementPresent("text");
		this.assertElementPresent("comments");
		// assertTextInElement() seems to be buggy
		// this.assertTextInElement("text", "test-1");
		this.assertTextPresent("test-1");
		this.setFormElement("text", "test-2");
		this.submit();

		assertTitleEquals("Wicket Examples - guestbook");
		this.assertElementPresent("comments");
		// assertTextInElement() seems to be buggy
		// this.assertTextInElement("text", "test-1");
		this.assertTextPresent("test-1");
		// this.assertTextInElement("text", "test-2");
		this.assertTextPresent("test-2");
	}
}
