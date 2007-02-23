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
package wicket.markup.html.link;

import wicket.WicketTestCase;
import wicket.protocol.http.WebApplication;
import wicket.util.tester.WicketTester;

/**
 * @author jcompagner
 */
public class ContextPathLinkTest extends WicketTestCase
{

	/**
	 * Construct.
	 * @param name
	 */
	public ContextPathLinkTest(String name)
	{
		super(name);
	}

	protected void setUp() throws Exception
	{
		tester = new WicketTester(ContextPathPage.class);
	}
	protected void tearDown() throws Exception
	{
		tester.destroy();
	}
	/**
	 * @throws Exception
	 */
	public void testDefaultContextPath() throws Exception
	{
		executeTest(ContextPathPage.class, "ContextPathPageDefaultContextResult.html");
	}
	
	/**
	 * @throws Exception
	 */
	public void testEmptyContextPath() throws Exception
	{
		tester.getApplication().getApplicationSettings().setContextPath("");
		executeTest(ContextPathPage.class, "ContextPathPageEmptyContextResult.html");
	}

	/**
	 * @throws Exception
	 */
	public void testNamedContextPath() throws Exception
	{
		tester.getApplication().getApplicationSettings().setContextPath("root");
		executeTest(ContextPathPage.class, "ContextPathPageRootContextResult.html");
	}

}
