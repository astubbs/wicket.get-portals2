/*
 * $Id: AbstractDecimalConverter.java,v 1.2 2005/02/09 04:55:38 jonathanlocke
 * Exp $ $Revision$ $Date$
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
package wicket.util.convert.converters;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Base class for all number converters.
 * 
 * @author Jonathan Locke
 */
public abstract class AbstractDecimalConverter extends AbstractNumberConverter
{
	/**
	 * @param locale 
	 * @return Returns the numberFormat.
	 */
	public final NumberFormat getNumberFormat(Locale locale)
	{
		// TODO should we cache this on locale (some caching already happens in getInstance itself!
		// Then we do know for sure that one instance of decimal format is thread safe
		return  DecimalFormat.getInstance(locale);
	}
}
