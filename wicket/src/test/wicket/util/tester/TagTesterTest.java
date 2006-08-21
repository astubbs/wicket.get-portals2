/*
 * $Id$ 
 * $Revision$ 
 * $Date$
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
package wicket.util.tester;

import junit.framework.TestCase;

/**
 * Test of TagTester
 * 
 * @author Frank Bille (billen)
 */
public class TagTesterTest extends TestCase
{
	/** Mock markup 1 */
	public static final String MARKUP_1 = "<p id=\"test\" class=\"class1\"><span class=\"class2\" id=\"test2\">mock</span></p>";


	/**
	 * Test the static factory method
	 */
	public void testCreateTagByAttribute()
	{
		TagTester tester = TagTester.createTagByAttribute(null, null, null);
		assertNull(tester);

		tester = TagTester.createTagByAttribute("<p id=\"test\">mock</p>", null, null);
		assertNull(tester);

		tester = TagTester.createTagByAttribute("<p id=\"test\">mock</p>", "id", null);
		assertNull(tester);

		tester = TagTester.createTagByAttribute("<p id=\"test\">mock</p>", "id", "test");
		assertNotNull(tester);
	}

	/**
	 * Test that getName returns the correct tag name.
	 */
	public void testGetName()
	{
		TagTester tester = TagTester.createTagByAttribute(MARKUP_1, "id", "test");
		assertNotNull(tester);

		assertEquals("p", tester.getName());


		tester = TagTester.createTagByAttribute(MARKUP_1, "id", "test2");
		assertNotNull(tester);

		assertEquals("span", tester.getName());
	}

	/**
	 * Test that hasAttribute return true if the tag has the given attribute.
	 * 
	 * It also tests that the order of the attributes doesn't matter.
	 */
	public void testHasAttribute()
	{
		TagTester tester = TagTester.createTagByAttribute(MARKUP_1, "id", "test");
		assertNotNull(tester);

		assertTrue(tester.hasAttribute("class"));


		tester = TagTester.createTagByAttribute(MARKUP_1, "id", "test2");
		assertNotNull(tester);

		assertTrue(tester.hasAttribute("class"));
	}

	/**
	 * Get attribute should return the value of the attribute.
	 * 
	 * If the attribute doesn't exist on the tag, the method should return null.
	 */
	public void testGetAttribute()
	{
		TagTester tester = TagTester.createTagByAttribute(MARKUP_1, "id", "test");
		assertNotNull(tester);

		assertEquals("class1", tester.getAttribute("class"));

		// Nested
		tester = TagTester.createTagByAttribute(MARKUP_1, "id", "test2");
		assertNotNull(tester);

		assertEquals("class2", tester.getAttribute("class"));

		// Case insensitive
		tester = TagTester.createTagByAttribute(MARKUP_1, "id", "test2");
		assertNotNull(tester);

		assertEquals("class2", tester.getAttribute("CLASS"));

		// Return null if no attribute
		tester = TagTester.createTagByAttribute(MARKUP_1, "id", "test");
		assertNotNull(tester);

		assertNull(tester.getAttribute("noattribute"));

		// Test that an empty attribute is returned as an empty string
		tester = TagTester.createTagByAttribute("<p id=\"test\" empty=\"\">Mock</p>", "id", "test");
		assertNotNull(tester);

		assertEquals("", tester.getAttribute("empty"));
	}

	/**
	 * getAttributeContains should only return true if the attribute value
	 * contains the expected value. It should not be case in-sensitive and not
	 * trim the attribute value.
	 */
	public void testGetAttributeContains()
	{
		TagTester tester = TagTester.createTagByAttribute(MARKUP_1, "id", "test");
		assertNotNull(tester);

		assertTrue(tester.getAttributeContains("class", "ss1"));
		assertTrue(tester.getAttributeContains("class", "clas"));
		assertTrue(tester.getAttributeContains("class", "s"));
		assertTrue(tester.getAttributeContains("class", "1"));
		assertTrue(tester.getAttributeContains("CLASS", "1"));
		assertFalse(tester.getAttributeContains("class", "classs"));
		assertFalse(tester.getAttributeContains("class", "CLASS"));
		assertFalse(tester.getAttributeContains("class", "cLass1"));
		assertFalse(tester.getAttributeContains("class", "class1 "));
		assertFalse(tester.getAttributeContains("class", " class1"));
	}

	/**
	 * Test the convenience method getAttributeIs, which returns true if the
	 * attributes value is exactly the same as the parameter.
	 */
	public void testGetAttributeIs()
	{
		TagTester tester = TagTester.createTagByAttribute(MARKUP_1, "id", "test");
		assertNotNull(tester);

		assertTrue(tester.getAttributeIs("class", "class1"));
		assertFalse(tester.getAttributeIs("class", "class1 "));
		assertFalse(tester.getAttributeIs("class", " class1"));
		assertFalse(tester.getAttributeIs("class", "Class1"));

		assertTrue(tester.getAttributeIs("noattribute", null));
		assertFalse(tester.getAttributeIs("noattribute", "somevalue"));
		assertFalse(tester.getAttributeIs("class", null));
	}

	/**
	 * getAttributeEndsWith behaves the same as getAttributeContains, but the
	 * parameter which should be contained must only be at the end.
	 */
	public void testGetAttributeEndsWith()
	{
		TagTester tester = TagTester.createTagByAttribute(MARKUP_1, "id", "test");
		assertNotNull(tester);

		assertTrue(tester.getAttributeEndsWith("class", "1"));
		assertTrue(tester.getAttributeEndsWith("class", ""));
		assertTrue(tester.getAttributeEndsWith("class", "ss1"));
		assertTrue(tester.getAttributeEndsWith("class", "lass1"));

		assertFalse(tester.getAttributeEndsWith("class", "1 "));
		assertFalse(tester.getAttributeEndsWith("class", " "));
		assertFalse(tester.getAttributeEndsWith("class", "class 1"));
		assertFalse(tester.getAttributeEndsWith("class", "SS1"));
	}
}
