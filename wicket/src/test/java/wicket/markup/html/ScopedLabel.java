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
package wicket.markup.html;

import wicket.markup.IScopedComponent;
import wicket.markup.html.basic.Label;
import wicket.model.IModel;

/**
 * 
 * @author Juergen Donnerstag
 */
public class ScopedLabel extends Label implements IScopedComponent
{
	private static final long serialVersionUID = 1L;

	/**
	 * @param id
	 * @param model
	 */
	public ScopedLabel(String id, IModel model)
	{
		super(id, model);
	}

	/**
	 * @param id
	 * @param label
	 */
	public ScopedLabel(String id, String label)
	{
		super(id, label);
	}

	/**
	 * @param id
	 */
	public ScopedLabel(String id)
	{
		super(id);
	}

	/**
	 * @see IScopedComponent#isRenderableInSubContainers()
	 */
	public boolean isRenderableInSubContainers()
	{
		return true;
	}
}
