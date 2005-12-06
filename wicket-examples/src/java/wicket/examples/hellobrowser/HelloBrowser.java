/*
 * $Id$ $Revision$
 * $Date$
 * 
 * ==================================================================== Licensed
 * under the Apache License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the
 * License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package wicket.examples.hellobrowser;

import wicket.examples.WicketExamplePage;
import wicket.markup.html.basic.Label;
import wicket.protocol.http.ClientProperties;
import wicket.protocol.http.request.WebClientInfo;

/**
 * Client snooping page.
 * 
 * @author Eelco Hillenius
 */
public class HelloBrowser extends WicketExamplePage
{
	/**
	 * Construct.
	 */
	public HelloBrowser()
	{
		// add a label that outputs a the client info object; it will result in
		// the calls RequestCycle.getClientInfo -> Session.getClientInfo ->
		// RequestCycle.newClientInfo. this is done once by default and
		// afterwards cached in the session object. This application uses
		// a custom requestcycle that overrides newClientInfo to not only
		// look at the user-agent request header, but also snoops javascript
		// properties by redirecting to a special page.

		// don't use a property model here or anything else that is resolved
		// during rendering, as changing the request target during rendering
		// is not allowed.
		ClientProperties properties = ((WebClientInfo)getRequestCycle().getClientInfo())
				.getProperties();

		add(new Label("clientinfo", properties.toString()));
	}
}