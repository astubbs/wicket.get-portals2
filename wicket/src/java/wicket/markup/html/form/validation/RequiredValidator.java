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
package wicket.markup.html.form.validation;

/**
 * Validator that ensures a component has a non-null and non-empty value. If the
 * component's value is null or empty (a value is considered empty if it just
 * contains whitespace) when its containing form is submitted then the
 * errorMessage() method will be called by the framework.
 * 
 * @author Jonathan Locke
 */
public class RequiredValidator extends StringValidator
{
	/** Singleton instance */
	private static final RequiredValidator instance = new RequiredValidator();
	
	/**
	 * @return Instance of required validator
	 */
	public static RequiredValidator getInstance()
	{
		return instance;
	}
	
	/**
	 * @see wicket.markup.html.form.validation.StringValidator#onValidate(java.lang.String)
	 */
	public void onValidate(String value)
	{
		// Check value
		if (value == null || value.trim().equals(""))
		{
			error();
		}
	}

	/**
	 * @see Object#toString()
	 */
	public String toString()
	{
		return "[RequiredValidator]";
	}
}
