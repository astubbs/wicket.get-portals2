/*
 * $Id$ $Revision$
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
package wicket.util.convert;

import java.io.Serializable;
import java.util.Locale;

/**
 * Converter for a specific data type. The type of the conversion is implicit in
 * the class which implements ITypeConverter. For example, a BooleanConverter
 * which implements this interface might convert values from Object to Boolean.
 * 
 * @author Jonathan Locke
 */
public interface ITypeConverter extends Serializable
{
	/**
	 * Converts the given value
	 * 
	 * @param value
	 *            The value to convert
	 * @param locale 
	 * @return The converted value
	 */
	public Object convert(Object value, Locale locale);
}