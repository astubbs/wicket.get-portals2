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
package wicket.util.convert;

/**
 * General purpose data type converter.
 *
 * @author Eelco Hillenius
 */
public interface IConverter
{
	/**
	 * Converts the given value to class c.
	 * @param value the value to convert
	 * @param c the class to convert to
	 * @return the converted value
	 */
	public Object convert(Object value, Class c);
}
