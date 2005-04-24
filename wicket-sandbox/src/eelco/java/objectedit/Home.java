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
package objectedit;

import java.io.Serializable;

import objectedit.beans.Person;

import wicket.PageParameters;
import wicket.markup.html.WebPage;

/**
 * @author Eelco Hillenius
 */
public class Home extends WebPage
{
	private Serializable currentObject = new Person("Fritz", "Fritzl");

	/**
	 * Constructor.
	 * @param parameters Page parameters
	 */
	public Home(final PageParameters parameters)
	{
		super();
		add(new BeanPanel("beanPanel", currentObject));
	}

	/**
	 * Gets the currentObject.
	 * @return currentObject
	 */
	public Serializable getCurrentObject()
	{
		return currentObject;
	}

	/**
	 * Sets the currentObject.
	 * @param currentObject currentObject
	 */
	public void setCurrentObject(Serializable currentObject)
	{
		this.currentObject = currentObject;
	}
}