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
package org.apache.wicket.examples.niceurl.mounted;

import java.util.Random;

import org.apache.wicket.PageParameters;
import org.apache.wicket.examples.WicketExamplePage;
import org.apache.wicket.examples.niceurl.Home;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;


/**
 * Simple bookmarkable page that displays page parameters.
 * 
 * @author Igor Vaynberg
 */
public class Page5 extends WicketExamplePage
{
	private final Random random = new Random();

	/**
	 * Constructor
	 * 
	 * @param parameters
	 */
	public Page5(PageParameters parameters)
	{
		String p1 = "CANNOT RESOLVE FROM URL";
		if (parameters.containsKey("param1"))
		{
			StringBuffer sb = new StringBuffer();
			String[] array = parameters.getStringArray("param1");
			for (int i = 0; i < array.length; i++)
			{
				sb.append(array[i]);
				if (array.length - 1 != i)
				{
					sb.append(", ");
				}
			}

			p1 = sb.toString();
		}
		String p2 = "CANNOT RESOLVE FROM URL";
		if (parameters.containsKey("param2"))
		{
			StringBuffer sb = new StringBuffer();
			String[] array = parameters.getStringArray("param2");
			for (int i = 0; i < array.length; i++)
			{
				sb.append(array[i]);
				if (array.length - 1 != i)
				{
					sb.append(", ");
				}
			}

			p2 = sb.toString();
		}

		add(new Label("p1", p1));
		add(new Label("p2", p2));

		String newP1 = String.valueOf(random.nextInt());
		String newP2 = String.valueOf(random.nextInt());

		PageParameters params = new PageParameters();
		params.put("param1", newP1);
		params.put("param2", newP2);

		BookmarkablePageLink link = new BookmarkablePageLink("refreshLink", Page5.class, params);
		add(link);
		add(new BookmarkablePageLink("homeLink", Home.class));
	}
}
