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
package org.apache.wicket.examples.debug;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.Application;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PageableListView;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.protocol.http.IRequestLogger;
import org.apache.wicket.protocol.http.RequestLogger;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.RequestLogger.RequestData;
import org.apache.wicket.protocol.http.RequestLogger.SessionData;
import org.apache.wicket.util.lang.Bytes;


/**
 * @author jcompagner
 */
public class RequestsPage extends WebPage
{
	private static final long serialVersionUID = 1L;
	private final SimpleDateFormat sdf = new SimpleDateFormat("dd MMM hh:mm:ss.SSS");

	/**
	 * Construct.
	 * 
	 * @param sessionData
	 */
	public RequestsPage(final SessionData sessionData)
	{
		add(new Image("bug"));
		if (sessionData == null)
		{
			add(new Label("id").setVisible(false));
			add(new Label("sessionInfo").setVisible(false));
			add(new Label("startDate").setVisible(false));
			add(new Label("lastRequestTime").setVisible(false));
			add(new Label("numberOfRequests").setVisible(false));
			add(new Label("totalTimeTaken").setVisible(false));
			add(new Label("size").setVisible(false));
			add(new WebMarkupContainer("sessionid"));
		}
		else
		{
			add(new Label("id", new Model<String>(sessionData.getSessionId())));
			add(new Label("sessionInfo", new Model<Serializable>(
				(Serializable)sessionData.getSessionInfo())));
			add(new Label("startDate", new Model<String>(sdf.format(sessionData.getStartDate()))));
			add(new Label("lastRequestTime", new Model<String>(
				sdf.format(sessionData.getLastActive()))));
			add(new Label("numberOfRequests", new Model<Long>(sessionData.getNumberOfRequests())));
			add(new Label("totalTimeTaken", new Model<Long>(sessionData.getTotalTimeTaken())));
			add(new Label("size", new Model<Bytes>(Bytes.bytes(sessionData.getSessionSize()))));
			add(new WebMarkupContainer("sessionid").setVisible(false));
		}

		IModel<List<RequestData>> requestsModel = new AbstractReadOnlyModel<List<RequestData>>()
		{
			private static final long serialVersionUID = 1L;

			@Override
			public ArrayList<RequestData> getObject()
			{
				List<RequestData> requests = getRequestLogger().getRequests();
				if (sessionData != null)
				{
					ArrayList<RequestData> returnValues = new ArrayList<RequestData>();
					for (int i = 0; i < requests.size(); i++)
					{
						RequestData data = requests.get(i);
						if (sessionData.getSessionId().equals(data.getSessionId()))
						{
							returnValues.add(data);
						}
					}
					return returnValues;
				}
				return new ArrayList<RequestData>(requests);
			}
		};
		PageableListView<RequestData> listView = new PageableListView<RequestData>("requests",
			requestsModel, 50)
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<RequestData> item)
			{
				RequestData rd = item.getModelObject();
				item.add(new Label("id", new Model<String>(rd.getSessionId())).setVisible(sessionData == null));
				item.add(new Label("startDate", new Model<String>(sdf.format(rd.getStartDate()))));
				item.add(new Label("timeTaken", new Model<Long>(rd.getTimeTaken())));
				item.add(new Label("eventTarget", new Model<String>(rd.getEventTarget())));
				item.add(new Label("responseTarget", new Model<String>(rd.getResponseTarget())));
				item.add(new Label("alteredObjects", new Model<String>(rd.getAlteredObjects())))
					.setEscapeModelStrings(false);
				item.add(new Label("sessionSize", new Model<Bytes>(Bytes.bytes(rd.getSessionSize()
					.longValue()))));
			}
		};
		add(listView);

		PagingNavigator navigator = new PagingNavigator("navigator", listView);
		add(navigator);
	}

	IRequestLogger getRequestLogger()
	{
		WebApplication webApplication = (WebApplication)Application.get();
		final IRequestLogger requestLogger;
		if (webApplication.getRequestLogger() == null)
		{
			// make default one.
			requestLogger = new RequestLogger();
		}
		else
		{
			requestLogger = webApplication.getRequestLogger();
		}
		return requestLogger;
	}
}
