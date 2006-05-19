/*
 * $Id$ $Revision:
 * 1.3 $ $Date$
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
package wicket.util.value;

import java.util.Map;

/**
 * ValueMap for attributes.
 * 
 * @author Eelco Hillenius
 */
public final class AttributeMap<K,V> extends ValueMap<K,V>
{
	private static final long serialVersionUID = 1L;

	/**
	 * Constructs an empty map.
	 */
	public AttributeMap()
	{
		super();
	}

	/**
	 * Copy constructor.
	 * 
	 * @param map
	 *            Map to be copied
	 */
	public AttributeMap(Map<? extends K, ? extends V> map)
	{
		super(map);
	}
}
