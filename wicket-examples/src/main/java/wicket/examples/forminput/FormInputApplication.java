/*
 * $Id: FormInputApplication.java 5394 2006-04-16 06:36:52 -0700 (Sun, 16 Apr
 * 2006) jdonnerstag $ $Revision$ $Date: 2006-04-16 06:36:52 -0700 (Sun,
 * 16 Apr 2006) $
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
package wicket.examples.forminput;

import java.awt.Font;
import java.util.Locale;

import wicket.Page;
import wicket.SharedResources;
import wicket.examples.WicketExampleApplication;
import wicket.markup.html.image.resource.DefaultButtonImageResource;

/**
 * Application class for form input example.
 * 
 * @author Eelco Hillenius
 */
public class FormInputApplication extends WicketExampleApplication
{
	/**
	 * Constructor.
	 */
	public FormInputApplication()
	{
	}

	/**
	 * @see wicket.Application#getHomePage()
	 */
	@Override
	public Class< ? extends Page> getHomePage()
	{
		return FormInput.class;
	}

	/**
	 * @see wicket.protocol.http.WebApplication#init()
	 */
	@Override
	protected void init()
	{
		getResourceSettings().setThrowExceptionOnMissingResource(false);

		// Chinese buttons
		Font font = new Font("SimSun", Font.BOLD, 16);
		DefaultButtonImageResource imgSave = new DefaultButtonImageResource("\u4FDD\u5B58");
		imgSave.setFont(font);
		DefaultButtonImageResource imgReset = new DefaultButtonImageResource("\u91CD\u7F6E");
		imgReset.setFont(font);
		SharedResources sharedResources = getSharedResources();
		sharedResources.add("save", Locale.SIMPLIFIED_CHINESE, imgSave);
		sharedResources.add("reset", Locale.SIMPLIFIED_CHINESE, imgReset);
	}
}
