/*
 * $Id$
 * $Revision$
 * $Date$
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package wicket.examples.wizard.framework;

import wicket.RequestCycle;

/**
 * Represents an exit node in a wizard.
 *
 * @author Eelco Hillenius
 */
public interface Exit extends Node
{
	/**
	 * Gets the button label.
	 * @return the label
	 */
	String getLabel();

	/**
	 * Finish this wizard.
	 * @param requestCycle the current request cycle
	 */
	void exit(RequestCycle requestCycle);
}
