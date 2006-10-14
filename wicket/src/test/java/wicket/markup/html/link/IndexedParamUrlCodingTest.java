/*
 * $Id: org.eclipse.jdt.ui.prefs 5004 2006-03-17 20:47:08 -0800 (Fri, 17 Mar
 * 2006) eelco12 $ $Revision: 5004 $ $Date: 2006-03-17 20:47:08 -0800 (Fri, 17
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
package wicket.markup.html.link;

import wicket.IRequestTarget;
import wicket.PageParameters;
import wicket.WicketTestCase;
import wicket.protocol.http.WebRequestCycle;
import wicket.request.IRequestCodingStrategy;
import wicket.request.RequestParameters;
import wicket.request.target.coding.IndexedParamUrlCodingStrategy;
import wicket.request.target.component.BookmarkablePageRequestTarget;

/**
 * @author jcompagner
 */
public class IndexedParamUrlCodingTest extends WicketTestCase
{

	/**
	 * Construct.
	 * 
	 * @param name
	 */
	public IndexedParamUrlCodingTest(String name)
	{
		super(name);
	}

	/**
	 * @throws Exception
	 */
	public void testIndexedLink() throws Exception
	{
		application.mount(new IndexedParamUrlCodingStrategy("/test1",
				BookmarkableHomePageLinksPage.class, null));
		application.mount(new IndexedParamUrlCodingStrategy("/test2",
				BookmarkableHomePageLinksPage.class, "mypagemap"));

		application.setupRequestAndResponse();
		WebRequestCycle cycle = application.createRequestCycle();

		PageParameters parameters = new PageParameters();
		parameters.add("0", "Integer0");
		parameters.add("1", "Integer1");

		String url1 = cycle.urlFor(
				new BookmarkablePageRequestTarget(BookmarkableHomePageLinksPage.class, parameters))
				.toString();
		assertEquals(url1, "/WicketTester/WicketTester/test1/Integer0/Integer1");
		String url2 = cycle.urlFor(
				new BookmarkablePageRequestTarget("mypagemap", BookmarkableHomePageLinksPage.class,
						parameters)).toString();
		assertEquals(url2,
				"/WicketTester/WicketTester/test2/Integer0/Integer1/wicket:pageMapName/mypagemap");

		application.setupRequestAndResponse();
		application.getServletRequest().setURL(url1);
		cycle = application.createRequestCycle();
		IRequestCodingStrategy encoder = cycle.getProcessor().getRequestCodingStrategy();

		RequestParameters requestParameters = encoder.decode(application.getWicketRequest());

		IRequestTarget target1 = cycle.getProcessor().resolve(cycle, requestParameters);
		if (target1 instanceof BookmarkablePageRequestTarget)
		{
			assertNull(((BookmarkablePageRequestTarget)target1).getPageMapName());
		}
		else
		{
			fail("url: " + url1 + " wasn't resolved to a bookmarkable target: " + target1);
		}

		application.setupRequestAndResponse();
		application.getServletRequest().setURL(url2);
		cycle = application.createRequestCycle();
		encoder = cycle.getProcessor().getRequestCodingStrategy();

		requestParameters = encoder.decode(application.getWicketRequest());

		IRequestTarget target2 = cycle.getProcessor().resolve(cycle, requestParameters);

		if (target2 instanceof BookmarkablePageRequestTarget)
		{
			assertEquals(((BookmarkablePageRequestTarget)target2).getPageMapName(), "mypagemap");
		}
		else
		{
			fail("url: " + url2 + " wasn't resolved to a bookmarkable target: " + target2);
		}
	}

}