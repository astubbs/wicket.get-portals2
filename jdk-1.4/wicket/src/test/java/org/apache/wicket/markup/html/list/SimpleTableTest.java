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
package org.apache.wicket.markup.html.list;

import org.apache.wicket.WicketTestCase;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;



/**
 * Test for simple table behavior.
 */
public class SimpleTableTest extends WicketTestCase
{
	/**
	 * Construct.
	 * @param arg0
	 */
	public SimpleTableTest(String arg0)
	{
		super(arg0);
	}

	/**
	 * Test simple table behavior.
	 * @throws Exception
	 */
	public void testSimpleTable_1() throws Exception
	{
		executeTest(SimpleTablePage_1.class, "SimpleTablePageExpectedResult_1.html");

		// Does re-render do as well ??
	    ListView view = (ListView)tester.getLastRenderedPage().get("table");
	    assertNotNull(view);
		tester.processRequestCycle(view);
		String document = tester.getServletResponse().getDocument();
		assertNotNull(document);
		assertFalse("".equals(document));
		assertEquals("<li wicket:id=\"table\"><span wicket:id=\"txt\">one</span></li><li wicket:id=\"table\"><span wicket:id=\"txt\">two</span></li><li wicket:id=\"table\"><span wicket:id=\"txt\">three</span></li>", document);

		// Does re-render do as well ??
	    ListItem item = (ListItem)tester.getLastRenderedPage().get("table:0");
	    assertNotNull(item);
		tester.processRequestCycle(item);
		document = tester.getServletResponse().getDocument();
		assertNotNull(document);
		assertFalse("".equals(document));
		assertEquals("<li wicket:id=\"table\"><span wicket:id=\"txt\">one</span></li>", document);

		// Does re-render do as well ??
	    Label label = (Label)tester.getLastRenderedPage().get("table:1:txt");
	    assertNotNull(label);
		tester.processRequestCycle(label);
		document = tester.getServletResponse().getDocument();
		assertNotNull(document);
		assertFalse("".equals(document));
		assertEquals("<span wicket:id=\"txt\">two</span>", document);
	}

	/**
	 * Test simple table behavior.
	 * @throws Exception
	 */
	public void testSimpleTable_2() throws Exception
	{
		executeTest(SimpleTablePage_2.class, "SimpleTablePageExpectedResult_2.html");

		// Does re-render do as well ??
	    ListView view = (ListView)tester.getLastRenderedPage().get("table");
	    assertNotNull(view);
		tester.processRequestCycle(view);
		String document = tester.getServletResponse().getDocument();
		assertNotNull(document);
		assertFalse("".equals(document));
		assertEquals("<li wicket:id=\"table\"><span wicket:id=\"txt\">one</span></li><li wicket:id=\"table\"><span wicket:id=\"txt\">two</span></li><li wicket:id=\"table\"><span wicket:id=\"txt\">three</span></li>", document);

		// Does re-render do as well ??
	    view = (ListView)tester.getLastRenderedPage().get("table");
	    assertNotNull(view);
		tester.processRequestCycle(view);
		document = tester.getServletResponse().getDocument();
		assertNotNull(document);
		assertFalse("".equals(document));
		assertEquals("<li wicket:id=\"table\"><span wicket:id=\"txt\">one</span></li><li wicket:id=\"table\"><span wicket:id=\"txt\">two</span></li><li wicket:id=\"table\"><span wicket:id=\"txt\">three</span></li>", document);
	}
}
