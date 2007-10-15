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

import java.text.ParseException;

import org.apache.wicket.markup.ComponentTag;


/**
 * Base class for markup filters
 *
 * @see org.apache.wicket.markup.MarkupParser
 *
 * @author Jonathan Locke
 */
public abstract class AbstractMarkupFilter implements IMarkupFilter
{
	/** The next MarkupFilter in the chain */
	private IMarkupFilter parent;

	/**
	 * Construct.
	 */
	public AbstractMarkupFilter()
	{
	}

	/**
	 * Construct.
	 *
	 * @param parent
	 *            The parent of this component The next element in the chain.
	 */
	public AbstractMarkupFilter(final IMarkupFilter parent)
	{
		this.parent = parent;
	}

	/**
	 * @return The next MarkupFilter in the chain
	 */
	public final IMarkupFilter getParent()
	{
		return parent;
	}

	/**
	 * Set new parent.
	 *
	 * @param parent
	 *            The parent of this component The next element in the chain
	 */
	public final void setParent(final IMarkupFilter parent)
	{
		this.parent = parent;
	}

	/**
	 * A convenience function to retrieve the next tag (same as nextTag()),
	 * however assuming that it is a ComponentTag.
	 *
	 * @return ComponentTag
	 * @throws ParseException
	 */
	protected final ComponentTag nextComponentTag() throws ParseException
	{
		return (ComponentTag)getParent().nextTag();
	}
}
