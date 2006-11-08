/*
 * $Id: org.eclipse.jdt.ui.prefs 5004 2006-03-17 20:47:08 -0800 (Fri, 17 Mar
 * 2006) eelco12 $ $Revision$ $Date: 2006-03-17 20:47:08 -0800 (Fri, 17
 * Mar 2006) $
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
package wicket.util.tester.apps_8;

import wicket.Component;
import wicket.MarkupContainer;
import wicket.markup.ComponentTag;
import wicket.markup.MarkupStream;
import wicket.markup.html.WebPage;
import wicket.markup.html.internal.HtmlBodyContainer;
import wicket.markup.parser.filter.BodyOnLoadHandler;

/**
 * @author Juergen Donnerstag
 */
public class EmailPage extends WebPage
{
	private static final long serialVersionUID = 1L;

	/**
	 * Construct
	 * 
	 */
	public EmailPage()
	{
		new MyHtmlBodyContainer(this, BodyOnLoadHandler.BODY_ID);
	}
	
	/**
	 * 
	 */
	public static class MyHtmlBodyContainer extends HtmlBodyContainer
	{
		private static final long serialVersionUID = 1L;
		
		/**
		 * Construct
		 * 
		 * @see Component#Component(MarkupContainer,String)
		 */
		public MyHtmlBodyContainer(MarkupContainer parent, final String id)
		{
			super(parent, id);
		}

		@Override
		protected void onComponentTagBody(MarkupStream markupStream, ComponentTag openTag)
		{
			getResponse().write("Something after the body open tag");
			super.onComponentTagBody(markupStream, openTag);
			getResponse().write("Something before the body close tag");
		}
	}
}