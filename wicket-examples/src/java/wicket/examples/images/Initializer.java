/*
 * $Id$ $Revision:
 * 2438 $ $Date$
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
package wicket.examples.images;

import wicket.Application;
import wicket.IInitializer;
import wicket.markup.html.image.resource.DefaultButtonImageResource;

/**
 * Initializer class for shared resources in the images application.
 * 
 * See wicket.properties file in root of examples project to see how this file
 * gets invoked for the images application.
 * 
 * @author Jonathan Locke
 */
public class Initializer implements IInitializer
{
	/**
	 * @see wicket.IInitializer#init(wicket.Application)
	 */
	public void init(Application application)
	{
		application.getSharedResources().add("cancelButton",
				new DefaultButtonImageResource("Cancel"));
	}
}
