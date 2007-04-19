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
package org.apache.wicket.markup.parser;

import org.apache.wicket.WicketTestCase;

/**
 * Quite some tests are already with MarkupParser.
 * 
 * @author Juergen Donnerstag
 */
public class PrependContextPathHandlerTest extends WicketTestCase
{
	/**
	 * Construct.
	 * 
	 * @param name
	 */
	public PrependContextPathHandlerTest(String name)
	{
		super(name);
	}

	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception
	{
		super.setUp();
	}

	/**
	 * 
	 * @throws Exception
	 */
	public final void testBasics() throws Exception
	{
		executeTest(Page_1.class, "PageExpectedResult_1.html");
	}
	
	/**
	 * @throws Exception
	 */
	public final void testWithRootContext() throws Exception
	{
		tester.getApplication().getApplicationSettings().setContextPath("");
		executeTest(Page_1.class, "PageExpectedResult_rootcontext.html");
	}

	/**
	 * @throws Exception
	 */
	public final void testWithOtherContext() throws Exception
	{
		tester.getApplication().getApplicationSettings().setContextPath("/other");
		executeTest(Page_1.class, "PageExpectedResult_othercontext.html");
	}

	/**
	 * @throws Exception
	 */
	public final void testAnchors() throws Exception
	{
		tester.getApplication().getApplicationSettings().setContextPath("/other");
		executeTest(Page_2.class, "Page2ExpectedResult.html");
	}
	
}
