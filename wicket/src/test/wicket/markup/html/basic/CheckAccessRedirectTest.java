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
package wicket.markup.html.basic;

import junit.framework.TestCase;
import wicket.markup.html.list.DiffUtil;
import wicket.protocol.http.MockWebApplication;

/**
 * @author jcompagner
 */
public class CheckAccessRedirectTest extends TestCase
{

	/**
	 * Construct.
	 */
	public CheckAccessRedirectTest()
	{
		super();
	}

	
	/**
	 * @throws Exception
	 */
	public void testRenderHomePagePageRedirect()  throws Exception
	{
		MockWebApplication application = new MockWebApplication(null);
		
		application.setHomePage(CheckAccessPageRedirect.class);

		// Do the processing
		application.setupRequestAndResponse();
		application.processRequestCycle();

		assertEquals(RedirectPage.class, application.getLastRenderedPage().getClass());

		// Validate the document
		String document = application.getServletResponse().getDocument();
		assertTrue(DiffUtil.validatePage(document, this.getClass(), "RedirectPage.html"));
		
	}

	/**
	 * @throws Exception
	 */
	public void testRenderHomePageClassRedirect()  throws Exception
	{
		MockWebApplication application = new MockWebApplication(null);
		
		application.setHomePage(CheckAccessClassRedirect.class);

		// Do the processing
		application.setupRequestAndResponse();
		application.processRequestCycle();

		assertEquals(RedirectPage.class, application.getLastRenderedPage().getClass());

		// Validate the document
		String document = application.getServletResponse().getDocument();
		assertTrue(DiffUtil.validatePage(document, this.getClass(), "RedirectPage.html"));
		
	}
	
}
