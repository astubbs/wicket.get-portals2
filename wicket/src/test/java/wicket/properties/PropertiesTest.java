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
package wicket.properties;

import java.util.Locale;

import wicket.WicketTestCase;
import wicket.protocol.http.WebRequestCycle;
import wicket.resource.loader.WicketBundleStringResourceLoader;
import wicket.util.tester.WicketTester;

/**
 * 
 * @author Juergen Donnerstag
 */
public class PropertiesTest extends WicketTestCase
{
	/**
	 * Construct.
	 * 
	 * @param name
	 */
	public PropertiesTest(final String name)
	{
		super(name);
	}

	@Override
	protected void setUp() throws Exception
	{
		tester = new WicketTester(new MyApplication());
	}

	/**
	 * 
	 * 
	 */
	public void test_1()
	{
		// Add the string resource loader with the special Bundle like
		// behavior
		tester.getApplication().getResourceSettings().addStringResourceLoader(
				new WicketBundleStringResourceLoader(tester.getApplication()));

		tester.setupRequestAndResponse();
		WebRequestCycle cycle = tester.createRequestCycle();
		TestPage page = new TestPage();
		cycle.getSession().setLocale(Locale.GERMANY);
		cycle.getSession().setStyle("mystyle");

		page.getString("test1");
		page.getString("test2");
		page.getString("test3");
		page.getString("test4");
	}

	/**
	 * 
	 * 
	 */
	public void test_2()
	{
		// Add the string resource loader with the special Bundle like
		// behavior
		tester.getApplication().getResourceSettings().addStringResourceLoader(
				new WicketBundleStringResourceLoader(tester.getApplication()));

		tester.setupRequestAndResponse();
		WebRequestCycle cycle = tester.createRequestCycle();
		cycle.getSession().setLocale(Locale.GERMANY);
		TestPage page = new TestPage()
		{
			private static final long serialVersionUID = 1L;

			@Override
			public String getVariation()
			{
				return "mystyle";
			}
		};

		page.getString("test1");
		page.getString("test2");
		page.getString("test3");
		page.getString("test4");
	}
}
