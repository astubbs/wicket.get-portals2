/*
 * $Id$
 * $Revision$ $Date$
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
package wicket.protocol.http;

import wicket.protocol.http.request.WebRequestEncoder;
import wicket.request.compound.CompoundRequestCycleProcessor;

/**
 * The default/ base implementation for request cycle processors that work in
 * web applications.
 * 
 * @author Eelco Hillenius
 */
public class DefaultWebRequestCycleProcessor extends CompoundRequestCycleProcessor
{
	/**
	 * Construct.
	 */
	public DefaultWebRequestCycleProcessor()
	{
		super(new WebRequestEncoder());
	}
}
