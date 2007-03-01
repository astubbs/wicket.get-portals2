/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package wicket;

import java.io.Serializable;

/**
 * A key to a piece of metadata associated with a Component at runtime. The key
 * contains type information that can be used to check the type of any metadata
 * value for the key when the value is set on the given Component. MetaDataKey
 * is abstract in order to force the creation of a subtype. That subtype is used
 * to test for identity when looking for the metadata because actual object
 * identity would suffer from problems under serialization. So, the correct way
 * to declare a MetaDataKey is like this: public static MetaDataKey ROLE = new
 * MetaDataKey(Role.class) { }
 * 
 * @author Jonathan Locke
 * 
 * @param <T>
 */
public abstract class MetaDataKey<T extends Serializable> implements IClusterable
{
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor.
	 */
	public MetaDataKey()
	{
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		return getClass().isInstance(obj);
	}

	/**
	 * @param metaData
	 *            Array of metadata to search
	 * @return The entry value
	 */
	@SuppressWarnings("unchecked")
	T get(MetaDataEntry[] metaData)
	{
		if (metaData != null)
		{
			for (MetaDataEntry m : metaData)
			{
				if (equals(m.key))
				{
					return (T)m.object;
				}
			}
		}
		return null;
	}

	/**
	 * @param metaData
	 *            The array of metadata
	 * @param object
	 *            The object to set
	 * @return Any new metadata array (if it was reallocated)
	 */
	MetaDataEntry[] set(MetaDataEntry[] metaData, final T object)
	{
		boolean set = false;
		if (metaData != null)
		{
			for (MetaDataEntry m : metaData)
			{
				if (equals(m.key))
				{
					m.object = object;
					set = true;
				}
			}
		}
		if (!set)
		{
			MetaDataEntry m = new MetaDataEntry();
			m.key = this;
			m.object = object;
			if (metaData == null)
			{
				metaData = new MetaDataEntry[1];
				metaData[0] = m;
			}
			else
			{
				final MetaDataEntry[] newMetaData = new MetaDataEntry[metaData.length + 1];
				System.arraycopy(metaData, 0, newMetaData, 0, metaData.length);
				newMetaData[metaData.length] = m;
				metaData = newMetaData;
			}
		}
		return metaData;
	}
}
