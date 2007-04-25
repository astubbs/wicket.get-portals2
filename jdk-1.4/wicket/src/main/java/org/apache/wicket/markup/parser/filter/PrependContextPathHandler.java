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
package org.apache.wicket.markup.parser.filter;

import java.text.ParseException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.Application;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.IMarkupParserFactory;
import org.apache.wicket.markup.MarkupElement;
import org.apache.wicket.markup.MarkupParserFactory;
import org.apache.wicket.markup.parser.AbstractMarkupFilter;
import org.apache.wicket.settings.IMarkupSettings;


/**
 * This is a markup inline filter which by default is added to the list of
 * markup filters. It is configured int the {@link MarkupParserFactory default}
 * implementation of {@link IMarkupParserFactory markup parser factory}, which
 * is set in {@link IMarkupSettings the markup settings}.
 * 
 * The purpose of the filter is to prepend the web apps context path to all href
 * and src attributes found in the markup which contain a relative URL like
 * "myDir/myPage.gif". It is applied to all non wicket component tags
 * (attributes).
 * 
 * @author Juergen Donnerstag
 */
public final class PrependContextPathHandler extends AbstractMarkupFilter
{
	/** List of attribute names considered */
	private static final String attributeNames[] = new String[] { "href", "src", "background" };

	/** Logging */
	private static final Log log = LogFactory.getLog(PrependContextPathHandler.class);

	private final Application application;

	/**
	 * This constructor will get the context path from the application settings.
	 * When it is not set the context path will be automatically resolved. This
	 * should work in most cases, and support the following clustering scheme
	 * 
	 * <pre>
	 *    node1.mydomain.com[/appcontext]
	 *    node2.mydomain.com[/appcontext]
	 *    node3.mydomain.com[/appcontext]
	 * </pre>
	 * 
	 * If it is set then you can map to other context like in clusters
	 * 
	 * <pre>
	 *    node1.mydomain.com/mycontext1/
	 *    node2.mydomain.com/mycontext2/
	 *    node3.mydomain.com/mycontext3/
	 *    mydomain.com/mycontext (load balancer)
	 * </pre>
	 * 
	 * or as a virtual server (app server and webserver)
	 * 
	 * <pre>
	 *    appserver.com/context mapped to webserver/ (context path should be '/')
	 * </pre>
	 * 
	 * @param application
	 *            The application object
	 * 
	 */
	public PrependContextPathHandler(final Application application)
	{
		this.application = application;
	}

	/**
	 * Get the next MarkupElement from the parent MarkupFilter and handle it if
	 * the specific filter criteria are met. Depending on the filter, it may
	 * return the MarkupElement unchanged, modified or it remove by asking the
	 * parent handler for the next tag.
	 * 
	 * @see org.apache.wicket.markup.parser.IMarkupFilter#nextTag()
	 * @return Return the next eligible MarkupElement
	 */
	public MarkupElement nextTag() throws ParseException
	{
		// Get the next tag. If null, no more tags are available
		final ComponentTag tag = (ComponentTag)getParent().nextTag();
		if (tag == null || tag.getId() != null)
		{
			return tag;
		}

		// Don't touch any wicket:id component
		if (tag.getId() != null)
		{
			return tag;
		}

		// this call should always get the default of the application or the
		// overriden one.
		String contextPath = application.getApplicationSettings().getContextPath();
		if (contextPath == null)
		{
			contextPath = "";
		}
		else if (contextPath.endsWith("/") == false)
		{
			contextPath += "/";
		}

		if (contextPath.length() > 0)
		{
			// Modify all relevant attributes
			for (int i = 0; i < attributeNames.length; i++)
			{
				String attrName = attributeNames[i];
				String attrValue = tag.getAttributes().getString(attrName);
				if ((attrValue != null) && (attrValue.startsWith("/") == false)
						&& (attrValue.indexOf(":") < 0) && !(attrValue.startsWith("#")))
				{
					String url = contextPath + attrValue;
					tag.getAttributes().put(attrName, url);
					tag.setModified(true);
				}
			}
		}

		return tag;
	}
}
