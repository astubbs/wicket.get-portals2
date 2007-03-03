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
package wicket.markup.html.resources;

import wicket.AttributeModifier;
import wicket.MarkupContainer;
import wicket.ResourceReference;
import wicket.behavior.HeaderContributor;
import wicket.markup.html.WebMarkupContainer;
import wicket.model.IModel;
import wicket.model.Model;

/**
 * Base class for components that render references to packaged resources.
 * @deprecated use {@link HeaderContributor} instead
 * 
 * @author Eelco Hillenius
 */
@Deprecated
public class PackagedResourceReference extends WebMarkupContainer
{
	private static final long serialVersionUID = 1L;

	/**
	 * Construct.
	 * 
	 * @param parent
	 *            The parent of this component
	 * 
	 * @param id
	 *            component id
	 * @param referer
	 *            the class that is refering; is used as the relative root for
	 *            gettting the resource
	 * @param file
	 *            relative location of the packaged file
	 * @param attributeToReplace
	 *            the attribute to replace of the target tag
	 */
	public PackagedResourceReference(MarkupContainer parent, final String id, final Class referer,
			final String file, final String attributeToReplace)
	{
		this(parent, id, referer, new Model<String>(file), attributeToReplace);
	}

	/**
	 * Construct.
	 * 
	 * @param parent
	 *            The parent of this component
	 * 
	 * @param id
	 *            component id
	 * @param referer
	 *            the class that is refering; is used as the relative root for
	 *            gettting the resource
	 * @param file
	 *            model that supplies the relative location of the packaged
	 *            file. Must return an instance of {@link String}
	 * @param attributeToReplace
	 *            the attribute to replace of the target tag
	 */
	public PackagedResourceReference(MarkupContainer parent, final String id, final Class referer,
			final IModel<String> file, final String attributeToReplace)
	{
		super(parent, id);

		if (referer == null)
		{
			throw new IllegalArgumentException("Referer may not be null");
		}
		if (file == null)
		{
			throw new IllegalArgumentException("File may not be null");
		}
		if (attributeToReplace == null)
		{
			throw new IllegalArgumentException("AttributeToReplace may not be null");
		}

		IModel<CharSequence> srcReplacement = new Model<CharSequence>()
		{
			private static final long serialVersionUID = 1L;

			@Override
			public CharSequence getObject()
			{
				String o = file.getObject();
				if (o == null)
				{
					throw new IllegalArgumentException("The model must provide a non-null object");
				}
				String f = getConverter(o.getClass()).convertToString(o, getLocale());
				ResourceReference ref = new ResourceReference(referer, f);
				return urlFor(ref);
			}
		};
		add(new AttributeModifier(attributeToReplace, true, srcReplacement));
	}

	/**
	 * Construct.
	 * 
	 * @param parent
	 *            The parent of this component
	 * 
	 * @param id
	 *            component id
	 * @param resourceReference
	 *            the reference to the resource
	 * @param attributeToReplace
	 *            the attribute to replace of the target tag
	 */
	public PackagedResourceReference(MarkupContainer parent, final String id,
			final ResourceReference resourceReference, final String attributeToReplace)
	{
		this(parent, id, new Model<ResourceReference>(resourceReference), attributeToReplace);
	}

	/**
	 * Construct.
	 * 
	 * @param parent
	 *            The parent of this component
	 * 
	 * @param id
	 *            component id
	 * @param resourceReference
	 *            the reference to the resource. Must return an instance of
	 *            {@link ResourceReference}
	 * @param attributeToReplace
	 *            the attribute to replace of the target tag
	 */
	public PackagedResourceReference(MarkupContainer parent, final String id,
			final IModel<ResourceReference> resourceReference, final String attributeToReplace)
	{
		super(parent, id);

		if (resourceReference == null)
		{
			throw new IllegalArgumentException("ResourceReference may not be null");
		}
		if (attributeToReplace == null)
		{
			throw new IllegalArgumentException("AttributeToReplace may not be null");
		}

		IModel<CharSequence> srcReplacement = new Model<CharSequence>()
		{
			private static final long serialVersionUID = 1L;

			@Override
			public CharSequence getObject()
			{
				ResourceReference o = resourceReference.getObject();
				if (o == null)
				{
					throw new IllegalArgumentException("The model must provide a non-null object");
				}
				return urlFor(o);
			}
		};
		add(new AttributeModifier(attributeToReplace, true, srcReplacement));
	}
}
