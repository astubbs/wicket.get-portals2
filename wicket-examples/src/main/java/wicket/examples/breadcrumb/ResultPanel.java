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
package wicket.examples.breadcrumb;

import wicket.extensions.breadcrumb.IBreadCrumbModel;
import wicket.extensions.breadcrumb.panel.BreadCrumbPanel;
import wicket.markup.html.basic.Label;

/**
 * Test bread crumb enabled panel.
 * 
 * @author Eelco Hillenius
 */
public class ResultPanel extends BreadCrumbPanel
{
	/**
	 * Construct.
	 * 
	 * @param id
	 * @param breadCrumbModel
	 * @param result
	 *            The 'result' to display as a label
	 */
	public ResultPanel(final String id, final IBreadCrumbModel breadCrumbModel, String result)
	{
		super(id, breadCrumbModel);

		if (result == null || "".equals(result.trim()))
		{
			result = "(hey, you didn't even provide some input!)";
		}

		add(new Label("result", result));
	}

	/**
	 * @see wicket.extensions.breadcrumb.IBreadCrumbParticipant#getTitle()
	 */
	public String getTitle()
	{
		return "result";
	}
}
