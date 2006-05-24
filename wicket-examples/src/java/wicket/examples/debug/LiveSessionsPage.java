/*
 * $Id$
 * $Revision$
 * $Date$
 * 
 * ==============================================================================
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
/*
 * $Id$
 * $Revision$
 * $Date$
 *
 * ==============================================================================
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package wicket.examples.debug;

import java.util.ArrayList;

import wicket.Application;
import wicket.Component;
import wicket.markup.html.WebPage;
import wicket.markup.html.basic.Label;
import wicket.markup.html.image.Image;
import wicket.markup.html.link.Link;
import wicket.markup.html.list.ListItem;
import wicket.markup.html.list.PageableListView;
import wicket.markup.html.navigation.paging.PagingNavigator;
import wicket.model.Model;
import wicket.protocol.http.RequestLogger;
import wicket.protocol.http.WebApplication;
import wicket.protocol.http.RequestLogger.SessionData;
import wicket.util.lang.Bytes;

/**
 * @author jcompagner
 */
public class LiveSessionsPage extends WebPage
{
	private static final long serialVersionUID = 1L;

	/**
	 * Construct.
	 */
	public LiveSessionsPage()
	{
		add(new Image(this,"bug"));
		
		add(new ApplicationView(this,"application", Application.get()));
		
		Link link = new Link(this,"togglelink")
		{
			private static final long serialVersionUID = 1L;

			public void onClick() 
			{
				WebApplication webApplication = (WebApplication)Application.get();
				RequestLogger requestLogger = webApplication.getRequestLogger();
				if(requestLogger == null)
				{
					webApplication.setRequestLogger(new RequestLogger());
				}
				else
				{
					webApplication.setRequestLogger(null);
				}
			}
		};
		link.add( new Label(link,"toggletext", new Model()
		{
			private static final long serialVersionUID = 1L;
			
			public Object getObject(Component component) 
			{
				WebApplication webApplication = (WebApplication)Application.get();
				RequestLogger requestLogger = webApplication.getRequestLogger();
				if(requestLogger == null)
				{
					return "Enable request recording";
				}
				else
				{
					return "Disable request recording";
				}
			};
			
		}));
		add(link);
		add(new Label(this,"totalSessions",new Model()
		{
			private static final long serialVersionUID = 1L;

			public Object getObject(Component component)
			{
				return new Integer(getRequestLogger().getTotalCreatedSessions());
			}
		}));
		add(new Label(this,"peakSessions",new Model()
		{
			private static final long serialVersionUID = 1L;
		
			public Object getObject(Component component)
			{
				return new Integer(getRequestLogger().getLiveSessions().size());
			}
		}));
		add(new Label(this,"liveSessions",new Model()
		{
			private static final long serialVersionUID = 1L;
		
			public Object getObject(Component component)
			{
				return new Integer(getRequestLogger().getPeakSessions());
			}
		}));
		
		Model sessionModel = new Model()
		{
			private static final long serialVersionUID = 1L;
			
			public Object getObject(Component component)
			{
				return new ArrayList(getRequestLogger().getLiveSessions());
			}
		};
		PageableListView listView = new PageableListView(this,"sessions",sessionModel,50)
		{
			private static final long serialVersionUID = 1L;
			
			protected void populateItem(ListItem item) 
			{
				final SessionData sd = (SessionData)item.getModelObject();
				Link link = new Link(item,"id")
				{
					private static final long serialVersionUID = 1L;
					/**
					 * @see wicket.markup.html.link.Link#onClick()
					 */
					public void onClick()
					{
						setResponsePage(new RequestsPage(sd));
					}
				};
				link.add( new Label(link,"id",new Model(sd.getId())));
				item.add( link);
				item.add( new Label(item,"requestCount",new Model(new Integer(sd.getRequests().size()))) );
				item.add( new Label(item,"requestsTime",new Model(sd.getRequestsTime())) );
				item.add( new Label(item,"sessionSize",new Model(Bytes.bytes(sd.getSessionSize()))) );
			}
		};
		add(listView);
		
		PagingNavigator navigator = new PagingNavigator(this,"navigator",listView);
		add(navigator);
	}
	
	RequestLogger getRequestLogger()
	{
		WebApplication webApplication = (WebApplication)Application.get();
		final RequestLogger requestLogger;
		if(webApplication.getRequestLogger() == null)
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
