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
package org.apache.wicket.markup;

import java.io.IOException;
import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.wicket.Application;
import org.apache.wicket.Page;
import org.apache.wicket.markup.parser.IMarkupFilter;
import org.apache.wicket.markup.parser.IXmlPullParser;
import org.apache.wicket.markup.parser.filter.BodyOnLoadHandler;
import org.apache.wicket.markup.parser.filter.HeadForceTagIdHandler;
import org.apache.wicket.markup.parser.filter.HtmlHandler;
import org.apache.wicket.markup.parser.filter.HtmlHeaderSectionHandler;
import org.apache.wicket.markup.parser.filter.TagTypeHandler;
import org.apache.wicket.markup.parser.filter.WicketLinkTagHandler;
import org.apache.wicket.markup.parser.filter.WicketMessageTagHandler;
import org.apache.wicket.markup.parser.filter.WicketNamespaceHandler;
import org.apache.wicket.markup.parser.filter.WicketRemoveTagHandler;
import org.apache.wicket.markup.parser.filter.WicketTagIdentifier;
import org.apache.wicket.settings.IMarkupSettings;
import org.apache.wicket.util.resource.ResourceStreamNotFoundException;
import org.apache.wicket.util.string.AppendingStringBuffer;



/**
 * This is a Wicket MarkupParser specifically for (X)HTML. It makes use of a
 * streaming XML parser to read the markup and IMarkupFilters to remove
 * comments, identify Wicket relevant tags, apply html specific treatments etc..
 * <p>
 * The result will be an Markup object, which is basically a list, containing
 * Wicket relevant tags and RawMarkup.
 * 
 * @see IMarkupFilter
 * @see IMarkupParserFactory
 * @see IMarkupSettings
 * @see Markup
 * 
 * @author Jonathan Locke
 * @author Juergen Donnerstag
 */
public class MarkupParser
{
	/** Conditional comment section, which is NOT treated as a comment section */
	private static final Pattern CONDITIONAL_COMMENT = Pattern
			.compile("\\[if .+\\]>(.|\n|\r)*<!\\[endif\\]");

	/** The XML parser to use */
	private final IXmlPullParser xmlParser;

	/** The markup handler chain: each filter has a specific task */
	private IMarkupFilter markupFilterChain;

	/** The markup created by reading the markup file */
	private final Markup markup;

	/** Temporary variable: Application.get().getMarkupSettings() */
	private final IMarkupSettings markupSettings;

	/**
	 * Constructor.
	 * 
	 * @param xmlParser
	 *            The streaming xml parser to read and parse the markup
	 */
	public MarkupParser(final IXmlPullParser xmlParser)
	{
		this.xmlParser = xmlParser;
		this.markup = new Markup();
		this.markupSettings = Application.get().getMarkupSettings();
	}

	/**
	 * In case you want to analyze markup which BY DEFAULT does not use "wicket"
	 * to find relevant tags.
	 * 
	 * @param namespace
	 */
	public final void setWicketNamespace(final String namespace)
	{
		this.markup.setWicketNamespace(namespace);
	}

	/**
	 * Applications which subclass initFilterChain() might also wish to access
	 * the markup resource stream.
	 * 
	 * @return The markup resource stream
	 */
	protected MarkupResourceStream getMarkupResourceStream()
	{
		return this.markup.getResource();
	}

	/**
	 * Create a new markup filter chain and initialize with all default filters
	 * required.
	 */
	private final void initializeMarkupFilters()
	{
		// Chain together all the different markup filters and configure them
		this.markupFilterChain = xmlParser;

		appendMarkupFilter(new WicketTagIdentifier(markup));
		appendMarkupFilter(new TagTypeHandler());
		appendMarkupFilter(new HtmlHandler());
		appendMarkupFilter(new WicketRemoveTagHandler());
		appendMarkupFilter(new WicketLinkTagHandler());
		appendMarkupFilter(new WicketNamespaceHandler(markup));

		// Provided the wicket component requesting the markup is known ...
		final MarkupResourceStream resource = markup.getResource();
		if (resource != null)
		{
			final ContainerInfo containerInfo = resource.getContainerInfo();
			if (containerInfo != null)
			{
				appendMarkupFilter(new WicketMessageTagHandler());
				appendMarkupFilter(new BodyOnLoadHandler());

				// Pages require additional handlers
				if (Page.class.isAssignableFrom(containerInfo.getContainerClass()))
				{
					appendMarkupFilter(new HtmlHeaderSectionHandler(this.markup));
				}

				appendMarkupFilter(new HeadForceTagIdHandler(containerInfo.getContainerClass()));
			}
		}
	}

	/**
	 * By default don't do anything. Subclasses may append additional markup
	 * filters if required.
	 * 
	 * @see #appendMarkupFilter(IMarkupFilter)
	 */
	protected void initFilterChain()
	{
	}

	/**
	 * Append a new filter to the list of already pre-configured markup filters.
	 * To be used by subclasses which implement {@link #initFilterChain()}.
	 * 
	 * @param filter
	 *            The filter to be appended
	 */
	public final void appendMarkupFilter(final IMarkupFilter filter)
	{
		filter.setParent(this.markupFilterChain);
		this.markupFilterChain = filter;
	}

	/**
	 * Reads and parses markup from a file.
	 * 
	 * @param resource
	 *            The file
	 * @return The markup
	 * @throws IOException
	 * @throws ResourceStreamNotFoundException
	 */
	public final Markup readAndParse(final MarkupResourceStream resource) throws IOException,
			ResourceStreamNotFoundException
	{
		// Remove all existing markup elements
		this.markup.reset();

		// For diagnostic purposes
		this.markup.setResource(resource);

		// Initialize the xml parser
		this.xmlParser.parse(resource.getInputStream(), this.markupSettings.getDefaultMarkupEncoding());

		// parse the xml markup and tokenize it into org.apache.wicket relevant markup
		// elements
		parseMarkup();

		this.markup.setEncoding(xmlParser.getEncoding());
		this.markup.setXmlDeclaration(xmlParser.getXmlDeclaration());

		return this.markup;
	}

	/**
	 * Parse the markup.
	 * 
	 * @param string
	 *            The markup
	 * @return The markup
	 * @throws IOException
	 * @throws ResourceStreamNotFoundException
	 */
	public final Markup parse(final String string) throws IOException,
			ResourceStreamNotFoundException
	{
		// Remove all existing markup elements
		this.markup.reset();

		// Initialize the xml parser
		this.xmlParser.parse(string);

		// parse the xml markup and tokenize it into org.apache.wicket relevant markup
		// elements
		parseMarkup();

		this.markup.setEncoding(xmlParser.getEncoding());
		this.markup.setXmlDeclaration(xmlParser.getXmlDeclaration());

		return this.markup;
	}

	/**
	 * Scans the given markup and extracts balancing tags.
	 * 
	 */
	private void parseMarkup()
	{
		// Initialize the markup filter chain
		initializeMarkupFilters();

		// Allow subclasses to extend the filter chain
		initFilterChain();

		// Get relevant settings from the Application
		final boolean stripComments = this.markupSettings.getStripComments();
		final boolean compressWhitespace = this.markupSettings.getCompressWhitespace();

		try
		{
			// allways remember the latest index (size)
			int size = this.markup.size();

			// Loop through tags
			for (ComponentTag tag; null != (tag = (ComponentTag)markupFilterChain.nextTag());)
			{
				boolean add = (tag.getId() != null);
				if (!add && tag.getXmlTag().isClose())
				{
					add = ((tag.getOpenTag() != null) && (tag.getOpenTag().getId() != null));
				}

				// Add tag to list?
				if (add || tag.isModified())
				{
					final CharSequence text = xmlParser.getInputFromPositionMarker(tag.getPos());

					// Add text from last position to tag position
					if (text.length() > 0)
					{
						String rawMarkup = text.toString();

						if (stripComments)
						{
							rawMarkup = removeComment(rawMarkup);
						}

						if (compressWhitespace)
						{
							rawMarkup = compressWhitespace(rawMarkup);
						}

						// Make sure you add it at the correct location.
						// IMarkupFilters might have added elements as well.
						this.markup.addMarkupElement(size, new RawMarkup(rawMarkup));
					}

					if (add)
					{
						// Add to list unless preview component tag remover
						// flagged
						// as removed
						if (!WicketRemoveTagHandler.IGNORE.equals(tag.getId()))
						{
							this.markup.addMarkupElement(tag);
						}
					}
					else if (tag.isModified())
					{
						this.markup.addMarkupElement(new RawMarkup(tag.toCharSequence()));
					}

					xmlParser.setPositionMarker();
				}

				// allways remember the latest index (size)
				size = this.markup.size();
			}
		}
		catch (final ParseException ex)
		{
			// Add remaining input string
			final CharSequence text = xmlParser.getInputFromPositionMarker(-1);
			if (text.length() > 0)
			{
				this.markup.addMarkupElement(new RawMarkup(text));
			}

			this.markup.setEncoding(xmlParser.getEncoding());
			this.markup.setXmlDeclaration(xmlParser.getXmlDeclaration());

			final MarkupStream markupStream = new MarkupStream(markup);
			markupStream.setCurrentIndex(this.markup.size() - 1);
			throw new MarkupException(markupStream, ex.getMessage(), ex);
		}

		// Add tail?
		final CharSequence text = xmlParser.getInputFromPositionMarker(-1);
		if (text.length() > 0)
		{
			String rawMarkup = text.toString();

			if (stripComments)
			{
				rawMarkup = removeComment(rawMarkup);
			}

			if (compressWhitespace)
			{
				rawMarkup = compressWhitespace(rawMarkup);
			}

			// Make sure you add it at the correct location.
			// IMarkupFilters might have added elements as well.
			this.markup.addMarkupElement(new RawMarkup(rawMarkup));
		}

		// Make all tags immutable and the list of elements unmodifable
		this.markup.makeImmutable();
	}

	/**
	 * Remove whitespaces from the raw markup
	 * 
	 * @param rawMarkup
	 * @return rawMarkup
	 */
	protected String compressWhitespace(String rawMarkup)
	{
		// We don't want to compress whitespace inside <pre> tags, so we look
		// for matches and:
		// - Do whitespace compression on everything before the first match.
		// - Append the <pre>.*?</pre> match with no compression.
		// - Loop to find the next match.
		// - Append with compression everything between the two matches.
		// - Repeat until no match, then special-case the fragment after the
		// last <pre>.

		Pattern preBlock = Pattern.compile("<pre>.*?</pre>", Pattern.DOTALL | Pattern.MULTILINE);
		Matcher m = preBlock.matcher(rawMarkup);
		int lastend = 0;
		StringBuffer sb = null;
		while (true)
		{
			boolean matched = m.find();
			String nonPre = matched ? rawMarkup.substring(lastend, m.start()) : rawMarkup
					.substring(lastend);
			nonPre = nonPre.replaceAll("[ \\t]+", " ");
			nonPre = nonPre.replaceAll("( ?[\\r\\n] ?)+", "\n");

			// Don't create a StringBuffer if we don't actually need one.
			// This optimises the trivial common case where there is no <pre>
			// tag at all down to just doing the replaceAlls above.
			if (lastend == 0)
			{
				if (matched)
				{
					sb = new StringBuffer(rawMarkup.length());
				}
				else
				{
					return nonPre;
				}
			}
			sb.append(nonPre);
			if (matched)
			{
				sb.append(m.group());
				lastend = m.end();
			}
			else
			{
				break;
			}
		}
		return sb.toString();
	}

	/**
	 * Remove all comment sections (&lt;!-- .. --&gt;) from the raw markup. For
	 * reasons I don't understand, the following regex
	 * <code>"<!--(.|\n|\r)*?-->"<code>
	 * causes a stack overflow in some circumstances (jdk 1.5) 
	 * 
	 * @param rawMarkup
	 * @return raw markup
	 */
	private String removeComment(String rawMarkup)
	{
		int pos1 = rawMarkup.indexOf("<!--");
		while (pos1 >= 0)
		{
			final int pos2 = rawMarkup.indexOf("-->", pos1 + 4);

			final AppendingStringBuffer buf = new AppendingStringBuffer(rawMarkup.length());
			if ((pos2 >= 0) && (pos1 > 0))
			{
				final String comment = rawMarkup.substring(pos1 + 4, pos2);
				if (CONDITIONAL_COMMENT.matcher(comment).matches() == false)
				{
					buf.append(rawMarkup.substring(0, pos1 - 1));
					if (rawMarkup.length() >= pos2 + 4)
					{
						buf.append(rawMarkup.substring(pos2 + 4));
					}
					rawMarkup = buf.toString();
				}
			}
			pos1 = rawMarkup.length() <= pos1 + 2 ? -1 : rawMarkup.indexOf("<!--", pos1 + 4);
		}
		return rawMarkup;
	}
}
