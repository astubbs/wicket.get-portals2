/*
 * $Id$ $Revision:
 * 1.8 $ $Date$
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
package wicket;

import wicket.ajax.AjaxInitializer;
import wicket.behavior.IBehaviorListener;
import wicket.markup.html.form.IFormSubmitListener;
import wicket.markup.html.form.IOnChangeListener;
import wicket.markup.html.link.ILinkListener;
import wicket.markup.html.tree.TreeComponentInitializer;

/**
 * Initializer for components in wicket core library.
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
		// Register listener interfaces explicitly (even though they implicitly
		// register when loaded) because of deserialization of an object that
		// implements an interface does not load the interfaces it implements!
		IBehaviorListener.INTERFACE.register();
		IFormSubmitListener.INTERFACE.register();
		ILinkListener.INTERFACE.register();
		IOnChangeListener.INTERFACE.register();
		IRedirectListener.INTERFACE.register();
		IResourceListener.INTERFACE.register();

		// initialize the tree component
		new TreeComponentInitializer().init(application);

		// initialize ajax components
		new AjaxInitializer().init(application);
	}
}
