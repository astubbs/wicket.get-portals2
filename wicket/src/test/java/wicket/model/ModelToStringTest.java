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
package wicket.model;

import java.util.Properties;

import wicket.Component;
import wicket.MockPageWithOneComponent;
import wicket.WicketTestCase;
import wicket.markup.html.basic.Label;

/**
 * Tests the toString() method on the models in the wicket.model package.
 */
public class ModelToStringTest extends WicketTestCase
{
	/**
	 * Construct.
	 * 
	 * @param name
	 */
	public ModelToStringTest(String name)
	{
		super(name);
	}

	/**
	 * Used for models in testing.
	 */
	private static class InnerPOJO
	{
	}

	/**
	 * Tests the Model.toString() method.
	 */
	public void testModel()
	{
		Model emptyModel = new Model<String>();
		String expected = "Model:classname=[wicket.model.Model]:object=[null]";
		assertEquals(expected, emptyModel.toString());

		Model stringModel = new Model<String>("foo");
		expected = "Model:classname=[wicket.model.Model]:object=[foo]";
		assertEquals(expected, stringModel.toString());
	}

	/**
	 * Tests the PropertyModel.toString() method.
	 */
	public void testPropertyModel()
	{
		PropertyModel emptyModel = new PropertyModel("", null);


		String expected = "Model:classname=[wicket.model.PropertyModel]:expression=[null]";
		assertEquals(expected, emptyModel.toString());

		Properties properties = new Properties();
		properties.put("name", "foo");
		PropertyModel stringProperty = new PropertyModel(properties, "name");

		expected = "Model:classname=[wicket.model.PropertyModel]:expression=[name]";
		assertEquals(expected, stringProperty.toString());

		expected = "Model:classname=[wicket.model.PropertyModel]:expression=[name]";
		assertEquals(expected, stringProperty.toString());

		InnerPOJO innerPOJO = new InnerPOJO();
		PropertyModel<Integer> pojoProperty = new PropertyModel<Integer>(innerPOJO, "pojo");

		expected = "Model:classname=[wicket.model.PropertyModel]:expression=[pojo]";

		assertEquals(expected, pojoProperty.toString());
	}

	/**
	 * Tests the CompoundPropertyModel.toString() method.
	 */
	public void testCompoundPropertyModel()
	{
		CompoundPropertyModel emptyModel = new CompoundPropertyModel<String>("");
		String expected = "Model:classname=[wicket.model.CompoundPropertyModel]";
		assertEquals(expected, emptyModel.toString());

		Properties properties = new Properties();
		properties.put("name", "foo");
		CompoundPropertyModel stringProperty = new CompoundPropertyModel<Properties>(properties);

		expected = "Model:classname=[wicket.model.CompoundPropertyModel]";
		assertEquals(expected, stringProperty.toString());

		expected = "Model:classname=[wicket.model.CompoundPropertyModel]";
		assertEquals(expected, stringProperty.toString());

		InnerPOJO innerPOJO = new InnerPOJO();
		CompoundPropertyModel pojoProperty = new CompoundPropertyModel<InnerPOJO>(innerPOJO);

		expected = "Model:classname=[wicket.model.CompoundPropertyModel]";
		assertEquals(expected, pojoProperty.toString());
	}

	/**
	 * Tests the BoundCompoundPropertyModel.toString() method.
	 */
	public void testBoundCompoundPropertyModel()
	{
		BoundCompoundPropertyModel emptyModel = new BoundCompoundPropertyModel<String>("");
		String expected = "Model:classname=[wicket.model.BoundCompoundPropertyModel]:bindings=[]";
		assertEquals(expected, emptyModel.toString());

		Properties properties = new Properties();
		properties.put("name", "foo");
		BoundCompoundPropertyModel stringProperty = new BoundCompoundPropertyModel<Properties>(
				properties);

		expected = "Model:classname=[wicket.model.BoundCompoundPropertyModel]:bindings=[]";
		assertEquals(expected, stringProperty.toString());

		expected = "Model:classname=[wicket.model.BoundCompoundPropertyModel]:bindings=[]";
		assertEquals(expected, stringProperty.toString());

		InnerPOJO innerPOJO = new InnerPOJO();
		BoundCompoundPropertyModel pojoProperty = new BoundCompoundPropertyModel<InnerPOJO>(
				innerPOJO);

		expected = "Model:classname=[wicket.model.BoundCompoundPropertyModel]:bindings=[]";
		assertEquals(expected, pojoProperty.toString());

		Component component1 = pojoProperty.bind(new Label(new MockPageWithOneComponent(),
				"component"));
		expected = "Model:classname=[wicket.model.BoundCompoundPropertyModel]:bindings=[Binding(:component=["
				+ component1 + "]:expression=[component])]";
		assertEquals(expected, pojoProperty.toString());
	}

	/**
	 * Test stub for testing AbstractReadOnlyModel.toString()
	 */
	private static class MyAbstractReadOnlyModel extends AbstractReadOnlyModel
	{
		private static final long serialVersionUID = 1L;

		/**
		 * @see AbstractReadOnlyModel#getObject()
		 */
		@Override
		public Object getObject()
		{
			return "FOO";
		}
	}


	private static final class MyLoadableDetachableModel extends LoadableDetachableModel
	{
		private static final long serialVersionUID = 1L;

		@Override
		protected Object load()
		{
			return "foo";
		}
	}

	/**
	 * Tests LoadableDetachableModel.toString()
	 */
	public void testLoadableDetachableModel()
	{
		LoadableDetachableModel model = new MyLoadableDetachableModel();
		String expected = "Model:classname=[" + model.getClass().getName() + "]"
				+ ":attached=false" + ":tempModelObject=[null]";
		assertEquals(expected, model.toString());

		model.getObject();
		expected = "Model:classname=[" + model.getClass().getName() + "]" + ":attached=true"
				+ ":tempModelObject=[foo]";
		assertEquals(expected, model.toString());

		model.detach();
		expected = "Model:classname=[" + model.getClass().getName() + "]" + ":attached=false"
				+ ":tempModelObject=[null]";
		assertEquals(expected, model.toString());
	}
}
