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
package org.apache.wicket.util.license;


/**
 * Test that the license headers are in place in this project. The tests are run from
 * {@link ApacheLicenseHeaderTestCase}, but you can add project specific tests here if needed.
 * 
 * @author Frank Bille Jensen (frankbille)
 */
public class ApacheLicenceHeaderTest extends ApacheLicenseHeaderTestCase
{

	/**
	 * Construct.
	 */
	public ApacheLicenceHeaderTest()
	{
		// addHeaders = true;

		/*
		 * YUI lib. See NOTICE
		 */
		cssIgnore = new String[] { "src/main/java/org/apache/wicket/extensions/yui/calendar/assets/skins/sam/calendar.css" };

		/*
		 * YUI lib. See NOTICE
		 */
		javaScriptIgnore = new String[] {
				"src/main/java/org/apache/wicket/extensions/yui/yuiloader-beta.js",
				"src/main/java/org/apache/wicket/extensions/yui/yuiloader-beta-min.js",
				"src/main/java/org/apache/wicket/extensions/yui/yahoo/yahoo.js",
				"src/main/java/org/apache/wicket/extensions/yui/yahoo-dom-event/yahoo-dom-event.js",
				"src/main/java/org/apache/wicket/extensions/yui/event/event.js",
				"src/main/java/org/apache/wicket/extensions/yui/dom/dom.js",
				"src/main/java/org/apache/wicket/extensions/yui/calendar/calendar.js",
				"src/main/java/org/apache/wicket/extensions/yui/calendar/calendar-min.js" };
	}
}
