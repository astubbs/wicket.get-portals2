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
package org.apache.wicket.model;

import org.apache.wicket.WicketTestCase;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.util.value.ValueMap;

/**
 * Tests the inheritance of models.
 */
public class InheritedModelTest extends WicketTestCase
{
	/**
	 * Tests the CPM inheritance by setting a different root model using a
	 * rendered scenario.
	 */
	public void testCompoundPropertyModelRendered()
	{
		ValueMap data1 = new ValueMap();
		data1.put("label", "foo");

		ValueMap data2 = new ValueMap();
		data2.put("label", "bar");

		InheritedTestPage page = new InheritedTestPage();

		tester.setupRequestAndResponse();
		page.setModel(new CompoundPropertyModel(data1));
		tester.startPage(page);
		tester.assertLabel("label", "foo");

		tester.setupRequestAndResponse();
		page.setModel(new CompoundPropertyModel(data2));
		tester.startPage(page);
		tester.assertLabel("label", "bar");
	}

	/**
	 * Tests the CPM by setting a different root model using a direct scenario.
	 */
	public void testCompoundPropertyModelDirect()
	{
		ValueMap data1 = new ValueMap();
		data1.put("label", "foo");

		ValueMap data2 = new ValueMap();
		data2.put("label", "bar");

		WebMarkupContainer parent = new WebMarkupContainer("foo");
		Label label = new Label("label");
		parent.add(label);

		parent.setModel(new CompoundPropertyModel(data1));
		assertEquals("foo", label.getModelObject());

		parent.setModel(new CompoundPropertyModel(data2));
		assertEquals("bar", label.getModelObject());

		data2.put("label", "foo");
		assertEquals("foo", label.getModelObject());
	}
}
