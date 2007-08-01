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
package org.apache.wicket.ajax;

import org.apache.wicket.WicketTestCase;

/**
 * @author jcompagner
 */
public class DomReadyOrderTest extends WicketTestCase
{
	/**
	 * @throws Exception
	 */
	public void testDomReadyOrder() throws Exception
	{
		tester.processRequestCycle(DomReadyOrderPage.class);
		tester.assertResultPage(DomReadyOrderPage.class, "DomReadyOrderPage_expected.html");

		tester.executeAjaxEvent("test", "onclick");
		tester.assertResultPage(DomReadyOrderPage.class, "DomReadyOrderPage_ajax_expected.html");
	}
}
