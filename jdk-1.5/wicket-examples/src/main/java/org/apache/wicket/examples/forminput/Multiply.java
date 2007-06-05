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
package org.apache.wicket.examples.forminput;

import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

/**
 * Displays how a {@link FormComponentPanel} can be used. Needs a model that
 * resolves to an Integer object.
 * <p>
 * Note that setting a new model value for this example wouldn't make sense, as
 * it would mismatch with the lhs and rhs. You would use this component's model
 * (value) primarily to write the result to some object, without ever directly
 * setting it in code yourself.
 * </p>
 * 
 * @author eelcohillenius
 */
public class Multiply extends FormComponentPanel
{
	private TextField left;

	private int lhs = 0;

	private int rhs = 0;

	private TextField right;

	/**
	 * Construct.
	 * 
	 * @param id
	 *            The component id
	 */
	public Multiply(String id)
	{
		super(id);
		init();
	}

	/**
	 * Construct.
	 * 
	 * @param id
	 *            The component id
	 * @param model
	 *            The model
	 */
	public Multiply(String id, IModel model)
	{
		super(id, model);
		init();
	}

	/**
	 * @return gets lhs
	 */
	public int getLhs()
	{
		return lhs;
	}

	/**
	 * @return gets rhs
	 */
	public int getRhs()
	{
		return rhs;
	}

	/**
	 * @param lhs
	 *            the lhs to set
	 */
	public void setLhs(int lhs)
	{
		this.lhs = lhs;
	}

	/**
	 * @param rhs
	 *            the rhs to set
	 */
	public void setRhs(int rhs)
	{
		this.rhs = rhs;
	}

	/**
	 * @see org.apache.wicket.markup.html.form.FormComponent#getConvertedInput()
	 */
	@Override
	public Object getConvertedInput()
	{
		// note that earlier versions did override updateModel, which looked
		// somewhat better, but wasn't useful for when you want to do
		// validations with either normal validators or form validators
		Integer lhs = (Integer)left.getConvertedInput();
		Integer rhs = (Integer)right.getConvertedInput();
		return lhs * rhs;
	}

	private void init()
	{
		add(left = new TextField("left", new PropertyModel(this, "lhs"), Integer.class));
		add(right = new TextField("right", new PropertyModel(this, "rhs"), Integer.class));
		left.setRequired(true);
		right.setRequired(true);
	}
}
