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
package org.apache.wicket.examples.template;

import org.apache.wicket.examples.WicketWebTestCase;

import junit.framework.Test;

/**
 * jWebUnit test for Hello World.
 */
public class TemplateTest extends WicketWebTestCase
{
	/**
	 * 
	 * @return Test
	 */
	public static Test suite()
	{
		return suite(TemplateTest.class);
	}

	/**
	 * Construct.
	 * 
	 * @param name
	 *            name of test
	 */
	public TemplateTest(String name)
	{
		super(name);
	}

	/**
	 * Test page.
	 * 
	 * @throws Exception
	 */
	public void testHelloWorld() throws Exception
	{
		beginAt("/template");
		assertTitleEquals("Template example, page 1");
		assertTextPresent("Lorem ipsum dolor sit amet, consectetuer");
	}
}
