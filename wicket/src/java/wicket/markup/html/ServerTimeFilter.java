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
package wicket.markup.html;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import wicket.IResponseFilter;
import wicket.RequestCycle;
import wicket.util.string.AppendingStringBuffer;

/**
 * This filter logs the server time so the time it takes from the start of a request and the end of the response.
 * It will log this to the standard logger, and will also change the response buffer if it finds a <head></head> part
 * to insert the script:
 * 
 * <script>
 * window.defaultStatus = 'Server time: 0.01s' 
 * </script>
 * 
 * @author jcompagner
 */
public class ServerTimeFilter implements IResponseFilter
{
	private static Log log = LogFactory.getLog(ServerTimeFilter.class);
	
	/**
	 * @see wicket.IResponseFilter#filter(java.lang.StringBuffer)
	 */
	public AppendingStringBuffer filter(AppendingStringBuffer responseBuffer)
	{
		int index = responseBuffer.indexOf("<head>");
		long timeTaken = System.currentTimeMillis() - RequestCycle.get().getStartTime();
		if(index != -1)
		{
			AppendingStringBuffer script = new AppendingStringBuffer(75);
			script.append("\n<script>\nwindow.defaultStatus='Server time: ");
			script.append( ((double)timeTaken)/1000);
			script.append("s';\n</script>\n");
			responseBuffer.insert(index+6, script);
		}
		log.info( timeTaken + "ms server time taken for request " + RequestCycle.get().getRequest().getURL() + " response size: " + responseBuffer.length());
		return responseBuffer;
	}

}
