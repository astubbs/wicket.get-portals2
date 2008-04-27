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
package org.apache.wicket.markup.repeater;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.model.IModel;

/**
 * Item that sets class="even" or class="odd" attributes based on its index
 * 
 * @author Igor Vaynberg (ivaynberg)
 * @param <T>
 *            type of Item's model object
 * 
 */
public class OddEvenItem<T> extends Item<T>
{
	private static final long serialVersionUID = 1L;

	private static final String CLASS_EVEN = "even";
	private static final String CLASS_ODD = "odd";

	/**
	 * Constructor
	 * 
	 * @param id
	 *            component id
	 * @param index
	 *            item index
	 * @param model
	 *            item model
	 */
	public OddEvenItem(String id, int index, IModel<T> model)
	{
		super(id, index, model);
	}

	@Override
	protected void onComponentTag(ComponentTag tag)
	{
		super.onComponentTag(tag);
		tag.put("class", (getIndex() % 2 == 0) ? CLASS_EVEN : CLASS_ODD);
	}

}
