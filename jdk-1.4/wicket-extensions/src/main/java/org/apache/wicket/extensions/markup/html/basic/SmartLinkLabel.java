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
package org.apache.wicket.extensions.markup.html.basic;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;


/**
 * If you have email addresses or web URLs in the data that you are displaying,
 * then you can automatically display those pieces of data as hyperlinks, you
 * will not have to take any action to convert that data.
 * <p>
 * Email addresses will be wrapped with a &lt;a
 * href="mailto:xxx"&gt;xxx&lt;/a&gt; tag, where "xxx" is the email address that
 * was detected.
 * <p>
 * Web URLs will be wrapped with a &lt;a href="xxx"&gt;xxx&lt;/a&gt; tag, where
 * "xxx" is the URL that was detected (it can be any valid URL type, http://,
 * https://, ftp://, etc...)
 * 
 * @author Juergen Donnerstag
 */
public class SmartLinkLabel extends Label
{
	private static final long serialVersionUID = 1L;

	/** Email address pattern */
	private static final Pattern emailPattern = Pattern.compile("[\\w\\.-]+@[\\w\\.-]+",
			Pattern.DOTALL);

	private static final String emailReplacePattern = "<a href=\"mailto:$0\">$0</a>";

	/** URL pattern */
	private static final Pattern urlPattern = Pattern.compile(
			"([a-zA-Z]+://[\\w\\.\\-\\:\\/]+)[\\w\\.:\\-/?&=%]*", Pattern.DOTALL);

	private static final String urlReplacePattern = "<a href=\"$0\">$1</a>";

	/**
	 * @see Label#Label(String, String)
	 */
	public SmartLinkLabel(String name, String label)
	{
		super(name, label);
	}

	/**
	 * @see Label#Label(String, IModel)
	 */
	public SmartLinkLabel(String name, IModel model)
	{
		super(name, model);
	}

	/**
	 * @see Label#Label(String)
	 */
	public SmartLinkLabel(String name)
	{
		super(name);
	}

	/**
	 * @see org.apache.wicket.Component#onComponentTagBody(org.apache.wicket.markup.MarkupStream,
	 *      org.apache.wicket.markup.ComponentTag)
	 */
	protected void onComponentTagBody(final MarkupStream markupStream, final ComponentTag openTag)
	{
		replaceComponentTagBody(markupStream, openTag, getSmartLink(getModelObjectAsString()));
	}

	/**
	 * Replace all email and URL addresses
	 * 
	 * @param text
	 *            Text to be modified
	 * @return Modified Text
	 */
	protected CharSequence getSmartLink(final CharSequence text)
	{
		return smartLink(text);
	}
	
	/**
	 * Replace all email and URL addresses
	 * 
	 * @param text
	 *            Text to be modified
	 * @return Modified Text
	 */
	static CharSequence smartLink(final CharSequence text)
	{
		if (text == null)
		{
			return text;
		}

		Matcher matcher = emailPattern.matcher(text);
		String work = matcher.replaceAll(emailReplacePattern);

		matcher = urlPattern.matcher(work);
		work = matcher.replaceAll(urlReplacePattern);

		return work;
	}
}
