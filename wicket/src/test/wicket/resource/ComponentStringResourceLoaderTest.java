/*
 * $Id$
 * $Revision$
 * $Date$
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package wicket.resource;

import java.util.Locale;

import junit.framework.Assert;

import wicket.Component;
import wicket.markup.html.panel.Panel;

/**
 * Test case for the <code>ComponentStringResourceLoader</code> class.
 * @author Chris Turner
 */
public class ComponentStringResourceLoaderTest extends StringResourceLoaderTestBase
{
	/**
	 * Create the test case.
	 * @param message The test name
	 */
	public ComponentStringResourceLoaderTest(String message)
	{
		super(message);
	}

	/**
	 * Create and return the loader instance
	 * @return The loader instance to test
	 */
	protected IStringResourceLoader createLoader()
	{
		return new ComponentStringResourceLoader(new DummyApplication());
	}

	/**
	 * @see wicket.resource.StringResourceLoaderTestBase#testLoaderUnknownResources()
	 */
	public void testLoaderUnknownResources()
	{
		Component c = new DummyComponent("hello", application)
		{
			private static final long serialVersionUID = 1L;
		};
		DummyPage page = new DummyPage();
		page.add(c);
		IStringResourceLoader loader = new ComponentStringResourceLoader(new DummyApplication());
		Assert.assertNull("Missing resource should return null", loader.loadStringResource(c, "test.string.bad",
				Locale.getDefault(), null));
	}

	/**
	 *
	 */
	public void testNullComponent()
	{
		Assert.assertNull("Null component should skip resource load", loader.loadStringResource(null,
				"test.string", Locale.getDefault(), null));
	}

	/**
	 *
	 */
	public void testNonPageComponent()
	{
		Component c = new DummyComponent("hello", application)
		{
			private static final long serialVersionUID = 1L;
		};
		IStringResourceLoader loader = new ComponentStringResourceLoader(new DummyApplication());
		try
		{
			loader.loadStringResource(c, "test.string", Locale.getDefault(), null);
			Assert.fail("IllegalStateException should be thrown");
		}
		catch (IllegalStateException e)
		{
			// Expected result since component is not attached to a Page
		}
	}

	/**
	 *
	 */
	public void testPageEmbeddedComponentLoadFromPage()
	{
		DummyPage p = new DummyPage();
		DummyComponent c = new DummyComponent("hello", application);
		p.add(c);
		IStringResourceLoader loader = new ComponentStringResourceLoader(new DummyApplication());
		Assert.assertEquals("Valid resourse string should be found", "Another string", loader.loadStringResource(
				c, "another.test.string", Locale.getDefault(), null));
	}

	/**
	 *
	 */
	public void testMultiLevelEmbeddedComponentLoadFromComponent()
	{
		DummyPage p = new DummyPage();
		Panel panel = new Panel("panel");
		p.add(panel);
		DummyComponent c = new DummyComponent("hello", application);
		panel.add(c);
		IStringResourceLoader loader = new ComponentStringResourceLoader(new DummyApplication());
		Assert.assertEquals("Valid resourse string should be found", "Component string", loader
				.loadStringResource(c, "component.string", Locale.getDefault(), null));
	}

	/**
	 *
	 */
	public void testLoadDirectFromPage()
	{
		DummyPage p = new DummyPage();
		IStringResourceLoader loader = new ComponentStringResourceLoader(new DummyApplication());
		Assert.assertEquals("Valid resourse string should be found", "Another string", loader.loadStringResource(
				p, "another.test.string", Locale.getDefault(), null));
	}

	/**
	 *
	 */
	public void testSearchClassHierarchyFromPage()
	{
		DummySubClassPage p = new DummySubClassPage();
		IStringResourceLoader loader = new ComponentStringResourceLoader(new DummyApplication());
		Assert.assertEquals("Valid resource string should be found", "SubClass Test String",
				loader.loadStringResource(p, "subclass.test.string", Locale.getDefault(), null));
		Assert.assertEquals("Valid resource string should be found", "Another string",
				loader.loadStringResource(p, "another.test.string", Locale.getDefault(), null));
	}
}
