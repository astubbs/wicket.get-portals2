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
package wicket.markup.html.image.resource;

import java.io.Serializable;
import java.util.Locale;

import wicket.Application;
import wicket.Component;
import wicket.IResourceFactory;
import wicket.IResourceListener;
import wicket.Resource;
import wicket.ResourceReference;
import wicket.WicketRuntimeException;
import wicket.markup.ComponentTag;
import wicket.util.lang.Objects;
import wicket.util.parse.metapattern.Group;
import wicket.util.parse.metapattern.MetaPattern;
import wicket.util.parse.metapattern.OptionalMetaPattern;
import wicket.util.parse.metapattern.parsers.MetaPatternParser;
import wicket.util.string.Strings;

/**
 * THIS CLASS IS INTENDED FOR INTERNAL USE IN IMPLEMENTING LOCALE SENSITIVE
 * COMPONENTS THAT USE IMAGE RESOURCES AND SHOULD NOT BE USED DIRECTLY BY
 * END-USERS.
 * <p>
 * This class contains the logic for extracting static image resources
 * referenced by the SRC attribute of component tags and keeping these static
 * image resources in sync with the component locale.
 * <p>
 * If no image is specified by the SRC attribute of an IMG tag, then any VALUE
 * attribute is inspected. If there is a VALUE attribute, it must be of the form
 * "[factoryName]:[sharedImageName]?:[specification]". [factoryName] is the name
 * of a resource factory that has been added to Application (for example,
 * DefaultButtonImageResourceFactory is installed by default under the name
 * "buttonFactory"). The [sharedImageName] value is optional and gives a name
 * under which a given generated image is shared. For example, a cancel button
 * image generated by the VALUE attribute "buttonFactory:cancelButton:Cancel" is
 * shared under the name "cancelButton" and this specification will cause a
 * component to reference the same image resource no matter what page it appears
 * on, which is a very convenient and efficient way to create and share images.
 * The [specification] string which follows the second colon is passed directly
 * to the image factory and its format is dependent on the specific image
 * factory. For details on the default buttonFactory, see
 * {@link wicket.markup.html.image.resource.DefaultButtonImageResourceFactory}.
 * <p>
 * Finally, if there is no SRC attribute and no VALUE attribute, the Image
 * component's model is converted to a String and that value is used as a path
 * to load the image.
 * 
 * @author Jonathan Locke
 */
public final class LocalizedImageResource implements Serializable, IResourceListener
{
	/** The component that is referencing this image resource */
	private Component component;

	/** The locale of the image resource */
	private Locale locale;

	/** The image resource this image component references */
	private Resource resource;

	/** The resource reference */
	private ResourceReference resourceReference;

	/** The style of the image resource */
	private String style;

	/**
	 * Parses image value specifications of the form "[factoryName]:
	 * [shared-image-name]?:[specification]"
	 * 
	 * @author Jonathan Locke
	 */
	private static final class ImageValueParser extends MetaPatternParser
	{
		/** Factory name */
		private static final Group factoryName = new Group(MetaPattern.VARIABLE_NAME);

		/** Image reference name */
		private static final Group imageReferenceName = new Group(MetaPattern.VARIABLE_NAME);

		/** Factory specification string */
		private static final Group specification = new Group(MetaPattern.ANYTHING_NON_EMPTY);

		/** Meta pattern. */
		private static final MetaPattern pattern = new MetaPattern(new MetaPattern[] { factoryName,
				MetaPattern.COLON,
				new OptionalMetaPattern(new MetaPattern[] { imageReferenceName }),
				MetaPattern.COLON, specification });

		/**
		 * Construct.
		 * 
		 * @param input
		 *            to parse
		 */
		private ImageValueParser(final CharSequence input)
		{
			super(pattern, input);
		}

		/**
		 * @return The factory name
		 */
		private String getFactoryName()
		{
			return factoryName.get(matcher());
		}

		/**
		 * @return Returns the imageReferenceName.
		 */
		private String getImageReferenceName()
		{
			return imageReferenceName.get(matcher());
		}

		/**
		 * @return Returns the specification.
		 */
		private String getSpecification()
		{
			return specification.get(matcher());
		}
	}

	/**
	 * Constructor
	 * 
	 * @param component
	 *            The component that owns this localized image resource
	 */
	public LocalizedImageResource(final Component component)
	{
		this.component = component;
		this.locale = component.getLocale();
		this.style = component.getStyle();
	}

	/**
	 * Binds this resource if it is shared
	 */
	public final void bind()
	{
		// If we have a resource reference
		if (resourceReference != null)
		{
			// Bind the reference to the application
			resourceReference.bind(component.getApplication());

			// Then dereference the resource
			resource = resourceReference.getResource();
		}
	}

	/**
	 * @see wicket.IResourceListener#onResourceRequested()
	 */
	public final void onResourceRequested()
	{
		bind();
		resource.onResourceRequested();
	}

	/**
	 * @param resource
	 *            The resource to set.
	 */
	public final void setResource(final Resource resource)
	{
		this.resource = resource;
	}

	/**
	 * @param resourceReference
	 *            The resource to set.
	 */
	public final void setResourceReference(final ResourceReference resourceReference)
	{
		this.resourceReference = resourceReference;
		bind();
	}

	/**
	 * @param tag
	 *            The tag to inspect for an optional src attribute that might
	 *            reference an image.
	 * @throws WicketRuntimeException
	 *             Thrown if an image is required by the caller, but none can be
	 *             found.
	 */
	public final void setSrcAttribute(final ComponentTag tag)
	{
		// If locale has changed from the initial locale used to attach image
		// resource, then we need to reload the resource in the new locale
		if (!Objects.equal(locale, component.getLocale())
				|| !Objects.equal(style, component.getStyle()))
		{
			// Get new component locale and style
			this.locale = component.getLocale();
			this.style = component.getStyle();

			// Invalidate current resource so it will be reloaded/recomputed
			this.resourceReference = null;
			this.resource = null;
		}

		// Need to load image resource for this component?
		if (resource == null && resourceReference == null)
		{
			// Get SRC attribute of tag
			final String src = tag.getString("src");
			if (src != null)
			{
				// Try to load static image
				loadStaticImage(src);
			}
			else
			{
				// Get VALUE attribute of tag
				final String value = tag.getString("value");
				if (value != null)
				{
					// Try to generate an image using an image factory
					newImage(value);
				}
				else
				{
					// Load static image using model object as the path
					loadStaticImage(component.getModelObjectAsString());
				}
			}
		}

		// Get URL for resource
		final String url;
		if (this.resourceReference != null)
		{
			// Create URL to shared resource
			url = component.getPage().urlFor(resourceReference.getPath());
		}
		else
		{
			// Create URL to component
			url = component.urlFor(IResourceListener.class);
		}

		// Set the SRC attribute to point to the component or shared resource
		tag.put("src", Strings.replaceAll(component.getResponse().encodeURL(url), "&", "&amp;"));
	}

	/**
	 * @param application
	 *            The application
	 * @param factoryName
	 *            The name of the image resource factory
	 * @return The resource factory
	 * @throws WicketRuntimeException
	 *             Thrown if factory cannot be found
	 */
	private IResourceFactory getImageResourceFactory(final Application application,
			final String factoryName)
	{
		final IResourceFactory factory = application.getResourceFactory(factoryName);

		// Found factory?
		if (factory == null)
		{
			throw new WicketRuntimeException("Could not find image resource factory named "
					+ factoryName);
		}
		return factory;
	}

	/**
	 * Tries to load static image at the given path and throws an exception if
	 * the image cannot be located.
	 * 
	 * @param path
	 *            The path to the image
	 * @throws WicketRuntimeException
	 *             Thrown if the image cannot be located
	 */
	private void loadStaticImage(final String path)
	{
		final Class scope = component.findParentWithAssociatedMarkup().getClass();
		final Package basePackage = scope.getPackage();
		this.resourceReference = new ResourceReference(scope, path)
		{
			/**
			 * @see wicket.ResourceReference#newResource()
			 */
			protected Resource newResource()
			{
				return StaticImageResource.get(basePackage, path, locale, style);
			}
		};
		resourceReference.setLocale(locale);
		resourceReference.setStyle(style);
		bind();
	}

	/**
	 * Generates an image resource based on the attribute values on tag
	 * 
	 * @param value
	 *            The value to parse
	 */
	private void newImage(final String value)
	{
		// Parse value
		final ImageValueParser valueParser = new ImageValueParser(value);

		// Does value match parser?
		if (valueParser.matches())
		{
			final String imageReferenceName = valueParser.getImageReferenceName();
			final String specification = valueParser.getSpecification();
			final String factoryName = valueParser.getFactoryName();
			final Application application = component.getApplication();

			// Do we have a reference?
			if (!Strings.isEmpty(imageReferenceName))
			{
				// Is resource already available via the application?
				if (application.getSharedResources().get(Application.class, imageReferenceName, locale, style) == null)
				{
					// Resource not available yet, so create it with factory and
					// share via Application
					final Resource imageResource = getImageResourceFactory(application, factoryName)
							.newResource(specification, locale, style);
					application.getSharedResources().add(Application.class, imageReferenceName, locale, style,
							imageResource);
				}

				// Create resource reference
				this.resourceReference = new ResourceReference(Application.class,
						imageReferenceName);
				resourceReference.setLocale(locale);
				resourceReference.setStyle(style);
			}
			else
			{
				this.resource = (ImageResource)getImageResourceFactory(application, factoryName)
						.newResource(specification, locale, style);
			}
		}
		else
		{
			throw new WicketRuntimeException(
					"Could not generate image for value attribute '"
							+ value
							+ "'.  Was expecting a value attribute of the form \"[resourceFactoryName]:[resourceReferenceName]?:[factorySpecification]\".");
		}
	}

}
