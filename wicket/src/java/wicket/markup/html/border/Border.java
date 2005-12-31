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
package wicket.markup.html.border;

import wicket.IComponentResolver;
import wicket.IComponentResolverMarker;
import wicket.MarkupContainer;
import wicket.markup.ComponentTag;
import wicket.markup.MarkupStream;
import wicket.markup.WicketTag;
import wicket.markup.html.IHeaderRenderer;
import wicket.markup.html.WebMarkupContainer;
import wicket.markup.html.internal.HtmlHeaderContainer;
import wicket.markup.parser.XmlTag;
import wicket.model.IModel;

/**
 * A border component has associated markup which is drawn and determines
 * placement of any markup and/or components nested within the border component.
 * <p>
 * The portion of the border's associated markup file which is to be used in
 * rendering the border is denoted by a &lt;wicket:border&gt; tag. The children
 * of the border component instance are then inserted into this markup,
 * replacing the first &lt;wicket:body&gt; tag in the border's associated
 * markup.
 * <p>
 * For example, if a border's associated markup looked like this:
 * <pre>
 *            &lt;html&gt;
 *            &lt;body&gt;
 *              &lt;wicket:border&gt;
 *                  First &lt;wicket:body/&gt; Last
 *              &lt;/wicket:border&gt;
 *            &lt;/body&gt;
 *            &lt;/html&gt;
 * </pre>
 * And the border was used on a page like this:
 * <pre>
 *            &lt;html&gt;
 *            &lt;body&gt;
 *              &lt;span wicket:id = &quot;myBorder&quot;&gt;
 *                  Middle
 *              &lt;/span&gt;
 *            &lt;/body&gt;
 *            &lt;/html&gt;
 * </pre>
 * Then the resulting HTML would look like this:
 * <pre>
 *            &lt;html&gt;
 *            &lt;body&gt;
 *                  First Middle Last
 *            &lt;/body&gt;
 *            &lt;/html&gt;
 * </pre>
 * In other words, the body of the myBorder component is substituted into the
 * border's associated markup at the position indicated by the
 * &lt;wicket:body&gt; tag.
 * <p>
 * Regarding &lt;wicket:body/&gt; you have two options. Either use 
 * &lt;wicket:body/&gt; (open-close tag) which will automatically be expanded 
 * to &lt;wicket:body&gt;body content&lt;/wicket:body&gt; or use
 * &lt;wicket:body&gt;preview region&lt;/wicket:body&gt; in your border's
 * markup. The preview region (everything in between the open and close tag)
 * will automatically be removed.
 *
 * @author Jonathan Locke
 */
public abstract class Border extends WebMarkupContainer implements IComponentResolver, 
	IComponentResolverMarker, IHeaderRenderer
{
	/** Will be true, once the first <wicket:body> has been seen */
	private transient boolean haveSeenBodyTag = false;
	
	/** The open tag for this border component. */
	private transient ComponentTag openTag;
	
	/**
     * @see wicket.Component#Component(String)
	 */
	public Border(final String id)
	{
		super(id);
	}

	/**
     * @see wicket.Component#Component(String, IModel)
	 */
	public Border(final String id, final IModel model)
	{
		super(id, model);
	}	

	/**
	 * 
	 * @see wicket.MarkupContainer#isTransparent()
	 */
	public boolean isTransparent()
	{
		if (getMarkupStream() == null)
		{
			return true;
		}
		
		return super.isTransparent();
	}
	
	/**
	 * Border makes use of a &lt;wicket:body&gt; tag to identify the position
	 * to insert within the border's body. As &lt;wicket:body&gt; is a special
     * tag and MarkupContainer is not able to handle it, we do that here.
     * <p>
     * You have two options. Either use &lt;wicket:body/&gt; (open-close tag) 
     * which will automatically be expanded to 
     * &lt;wicket:body&gt;body content&lt;/wicket:body&gt; or use
     * &lt;wicket:body&gt;preview region&lt;/wicket:body&gt; in your border's
     * markup. The preview region (everything in between the open and close tag)
     * will automatically be removed.
	 *
	 * @see IComponentResolver#resolve(MarkupContainer, MarkupStream, ComponentTag)
	 * 
	 * @param container
	 *            The container parsing its markup
	 * @param markupStream
	 *            The current markupStream
	 * @param tag
	 *            The current component tag while parsing the markup
	 * @return True if componentId was handled by the resolver, false otherwise.
	 */
	public boolean resolve(final MarkupContainer container, final MarkupStream markupStream,
			final ComponentTag tag)
	{
		// Determine if tag is a <wicket:body> tag
		// If we're being asked to resolve a component for a <wicket:body> tag
		if (!(tag instanceof WicketTag))
        {
            return false;
        }
		
		final WicketTag wtag = (WicketTag) tag;
        if (!wtag.isBodyTag())
        {
            return false;
        }

        // Ok, it is a wicket:body tag. Now render its body
        ComponentTag bodyTag = tag;
        if (tag.isOpen())
        {
            // It is open-preview-close already.
            // Only RawMarkup is allowed within the preview region, which
            // gets stripped from output
            markupStream.next();
            markupStream.skipRawMarkup();
        }
        else if (tag.isOpenClose())
        {
            // Automatically expand <wicket:body/> to <wicket:body>...</wicket:body>
            // in order for the html to look right: insert the body in between the
            // wicket tags instead of behind the open-close tag.
            bodyTag = tag.mutable();
            bodyTag.setType(XmlTag.OPEN);
        }
        else
        {
			markupStream.throwMarkupException("A <wicket:body> tag must be an open or open-close tag.");
        }
        
		// Render the body tag
		renderComponentTag(bodyTag);

		// If markup stream is null, that indicates we already recursed into
		// this block of log and set it to null (below). If we did that,
		// then we want to go up another level of border nesting.
		Border border = this;
		if (border.getMarkupStream() == null)
		{
			// Find Border at or above parent of this border
			final MarkupContainer borderParent = border.getParent();
			border = (Border)((borderParent instanceof Border) ? borderParent : borderParent
					.findParent(Border.class));
		}

		// Get the border's markup
		final MarkupStream borderMarkup = border.findMarkupStream();

		// Set markup of border to null. This allows us to find the border's
		// parent's markup. It also indicates that we've been here in the
		// log just above.
		border.setMarkupStream(null);

		// Draw the children of the border component using its original
		// in-line markup stream (not the border's associated markup stream)
		border.renderComponentTagBody(border.findMarkupStream(), border.openTag);

		// Restore border markup so it can continue rendering
		border.setMarkupStream(borderMarkup);
		
		// Render body close tag: </wicket:body>
		if (tag.isOpenClose())
		{
			markupStream.next();
		    bodyTag.setType(XmlTag.CLOSE);
			renderComponentTag(bodyTag);
		}

        // There shall exactly only one body tag per border
        if (border.haveSeenBodyTag == true)
        {
			markupStream.throwMarkupException(
			        "There must be exactly one <wicket:body> tag for each border compoment.");
        }
        
        border.haveSeenBodyTag = true;
		
		return true;
	}

	/**
	 * Render the tag body
	 * 
	 * @see wicket.Component#onComponentTagBody(wicket.markup.MarkupStream,
	 *      wicket.markup.ComponentTag)
	 */
	protected final void onComponentTagBody(final MarkupStream markupStream, final ComponentTag openTag)
	{
		// Save open tag for callback later to render body
		this.openTag = openTag;
		
		// initialize
        this.haveSeenBodyTag = false;

		// Render the associated markup
		renderAssociatedMarkup("border",
				"Markup for a border component must begin a tag like '<wicket:border>'");

        // There shall exactly only one body tag per border
        if (haveSeenBodyTag == false)
        {
			markupStream.throwMarkupException(
			        "There must be exactly one <wicket:body> tag for each border compoment.");
        }
	}

	/**
	 * "Visit all components of the component hierarchie and ask if they have
	 * something to contribute to the header section of the page. If yes,
	 * child components will return a MarkupContainer of there header
	 * section which gets (auto) added to the component hierarchie and
	 * immediately rendered.". In case of bordered Pages, the header component
	 * is not added to the Page, but to the Border component. Thus, in order
	 * to handle bordered pages properly, we must pass the request down one more 
	 * level. In case of a bordered page, it is a Page component.
     *
     * @param container The current html header container
	 */
	public final void renderHeaderSections(final HtmlHeaderContainer container)
	{
		MarkupContainer parent = getParent();

		if (parent instanceof IHeaderRenderer)
		{
		    ((IHeaderRenderer)parent).renderHeaderSections(container);
		}
	}
}
