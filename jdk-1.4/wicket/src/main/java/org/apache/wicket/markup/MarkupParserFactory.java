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
package org.apache.wicket.markup;

import org.apache.wicket.Application;
import org.apache.wicket.markup.parser.IMarkupFilter;
import org.apache.wicket.markup.parser.XmlPullParser;
import org.apache.wicket.markup.parser.filter.EnclosureHandler;
import org.apache.wicket.markup.parser.filter.PrependContextPathHandler;

/**
 * Default implementation of IMarkupParserFactory
 * 
 * @author Igor Vaynberg (ivaynberg)
 */
public class MarkupParserFactory implements IMarkupParserFactory
{
	private IMarkupFilter[] filters;

	// FIXME right now we add the two default filters in 3 places, refactor so
	// it is done in a single place

	/**
	 * Construct.
	 * 
	 * @param application
	 *            Application settings necessary to configure the parser
	 */
	public MarkupParserFactory(final Application application)
	{
		this.filters = new IMarkupFilter[] { new PrependContextPathHandler(application),
				new EnclosureHandler() };
	}

	/**
	 * Construct.
	 * 
	 * @param application
	 *            Application settings necessary to configure the parser
	 * @param filters
	 *            additional markup filters
	 */
	public MarkupParserFactory(final Application application, IMarkupFilter[] filters)
	{
		this.filters = new IMarkupFilter[filters.length + 2];
		System.arraycopy(filters, 0, this.filters, 0, filters.length);
		this.filters[filters.length - 1] = new PrependContextPathHandler(application);
		this.filters[filters.length] = new EnclosureHandler();
	}

	/**
	 * Construct.
	 * 
	 * @param application
	 *            Application settings necessary to configure the parser
	 * @param filter
	 *            additional markup filter
	 */
	public MarkupParserFactory(final Application application, IMarkupFilter filter)
	{
		this.filters = new IMarkupFilter[] { filter, new PrependContextPathHandler(application),
				new EnclosureHandler() };
	}

	/**
	 * @see org.apache.wicket.markup.IMarkupParserFactory#newMarkupParser()
	 */
	public MarkupParser newMarkupParser()
	{
		final MarkupParser parser = new MarkupParser(new XmlPullParser())
		{
			public void initFilterChain()
			{
				if (filters != null)
				{
					for (int i = 0; i < filters.length; i++)
					{
						appendMarkupFilter(filters[i]);
					}
				}
			}
		};
		return parser;
	}
}