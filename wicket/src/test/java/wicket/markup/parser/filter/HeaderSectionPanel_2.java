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
package wicket.markup.parser.filter;

import wicket.MarkupContainer;
import wicket.markup.html.link.ExternalLink;
import wicket.markup.html.panel.Panel;


/**
 * Mock page for testing.
 * 
 * @author Chris Turner
 */
public class HeaderSectionPanel_2 extends Panel
{
	private static final long serialVersionUID = 1L;

	/**
	 * Construct.
	 * 
	 * @param parent
	 * @param id
	 */
	public HeaderSectionPanel_2(MarkupContainer parent, final String id)
	{
		super(parent, id);
		new ExternalLink(this, "cssHref", "myStyle.css");
	}
}
