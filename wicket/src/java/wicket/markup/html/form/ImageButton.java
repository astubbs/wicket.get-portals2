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
package wicket.markup.html.form;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import wicket.IResourceListener;
import wicket.ResourceReference;
import wicket.WicketRuntimeException;
import wicket.markup.ComponentTag;
import wicket.markup.html.WebResource;
import wicket.markup.html.image.resource.DefaultButtonImageResource;
import wicket.markup.html.image.resource.LocalizedImageResource;

/**
 * A button which renders itself as an image button resource.
 * <p>
 * For details of how ImageButtons load, generate and manage images, see
 * {@link LocalizedImageResource}.
 * 
 * @author Jonathan Locke
 */
public class ImageButton extends Button implements IResourceListener
{
	private static final long serialVersionUID = 1L;
	
	private static final Log log = LogFactory.getLog(ImageButton.class);
	
	/** The image resource this image component references */
	private LocalizedImageResource localizedImageResource = new LocalizedImageResource(this);

	/**
	 * @see wicket.Component#Component(String)
	 */
	public ImageButton(String id)
	{
		super(id);
	}

	/**
	 * Constructs an image button directly from an image resource.
	 * 
	 * @param id
	 *            See Component
	 * 
	 * @param imageResource
	 *            The image resource
	 */
	public ImageButton(final String id, final WebResource imageResource)
	{
		super(id);
		this.localizedImageResource.setResource(imageResource);
	}

	/**
	 * Constructs an image directly from an image resource.
	 * 
	 * @param id
	 *            See Component
	 * @param resourceReference
	 *            The shared image resource
	 */
	public ImageButton(final String id, final ResourceReference resourceReference)
	{
		super(id);
		localizedImageResource.setResourceReference(resourceReference);
	}

	/**
	 * Constructor
	 * 
	 * @param id
	 *            See Component
	 * @param label
	 *            The button label
	 */
	public ImageButton(final String id, final String label)
	{
		this(id, new DefaultButtonImageResource(label));
	}

	/**
	 * @see wicket.IResourceListener#onResourceRequested()
	 */
	public void onResourceRequested()
	{
		localizedImageResource.onResourceRequested();
	}

	/**
	 * Processes the component tag.
	 * 
	 * @param tag
	 *            Tag to modify
	 * @see wicket.Component#onComponentTag(ComponentTag)
	 */
	protected final void onComponentTag(final ComponentTag tag)
	{
		checkComponentTag(tag, "input");
		checkComponentTagAttribute(tag, "type", "image");
		super.onComponentTag(tag);
		localizedImageResource.setSrcAttribute(tag);
	}
	
	/**
	 * @see wicket.Component#onSessionAttach()
	 */
	protected void onSessionAttach()
	{
		try
		{
			localizedImageResource.bind();
		} 
		catch(WicketRuntimeException wre)
		{
			// If this exceptions happens here then the locale is maybe changed
			// and there is no image for that locale you are in now.
			log.error("Localized Image Resource not found for the current locale " + getLocale(), wre);
		}
	}
}
