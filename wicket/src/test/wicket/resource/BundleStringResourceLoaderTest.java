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

import wicket.resource.BundleStringResourceLoader;
import wicket.resource.IStringResourceLoader;
import junit.framework.Assert;

/**
 * Test case for the <code>BundleStringResourceLoader</code> class.
 * @author Chris Turner
 */
public class BundleStringResourceLoaderTest extends StringResourceLoaderTestBase
{

	/**
	 * Create the test case.
	 * @param message The test name
	 */
	public BundleStringResourceLoaderTest(String message)
	{
		super(message);
	}

	/**
	 * Create and return the loader instance.
	 * @return The loader instance to test
	 */
	protected IStringResourceLoader createLoader()
	{
		return new BundleStringResourceLoader("wicket.resource.DummyResources");
	}

	/**
	 * @see wicket.resource.StringResourceLoaderTestBase#testLoaderValidKeyStyleNoLocale()
	 */
	public void testLoaderValidKeyStyleNoLocale()
	{
		String s = loader.get(component, "test.string", null, "alt");
		Assert.assertEquals("Resource should be loaded", "This is a test", s);
	}

	/**
	 * @see wicket.resource.StringResourceLoaderTestBase#testLoaderUnknownResources()
	 */
	public void testLoaderUnknownResources()
	{
		IStringResourceLoader loader = new BundleStringResourceLoader("unknown.resource");
		Assert.assertNull("Unknown resource should return null", loader.get(component,
				"test.string", Locale.getDefault(), null));
	}

}

// 
