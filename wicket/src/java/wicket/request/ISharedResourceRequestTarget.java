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
package wicket.request;

import wicket.IRequestTarget;
import wicket.Resource;

/**
 * Target that denotes a shared {@link wicket.Resource}.
 * 
 * @author Eelco Hillenius
 */
public interface ISharedResourceRequestTarget extends IRequestTarget
{

	/**
	 * Gets the shared resource.
	 * 
	 * @return the shared resource
	 */
	public abstract Resource getResource();

	/**
	 * Gets the key of the resource.
	 * 
	 * @return the key of the resource
	 */
	public abstract String getResourceKey();

}