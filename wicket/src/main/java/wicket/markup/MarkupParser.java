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
package wicket.markup;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import wicket.Page;
import wicket.WicketRuntimeException;
import wicket.markup.parser.IMarkupFilter;
import wicket.markup.parser.IXmlPullParser;
import wicket.markup.parser.XmlPullParser;
import wicket.markup.parser.filter.BodyOnLoadHandler;
import wicket.markup.parser.filter.HeadForceTagIdHandler;
import wicket.markup.parser.filter.HtmlHandler;
import wicket.markup.parser.filter.HtmlHeaderSectionHandler;
import wicket.markup.parser.filter.PrependContextPathHandler;
import wicket.markup.parser.filter.TagTypeHandler;
import wicket.markup.parser.filter.WicketLinkTagHandler;
import wicket.markup.parser.filter.WicketMessageTagHandler;
import wicket.markup.parser.filter.WicketNamespaceHandler;
import wicket.markup.parser.filter.WicketRemoveTagHandler;
import wicket.markup.parser.filter.WicketTagIdentifier;
import wicket.settings.IMarkupSettings;
import wicket.util.resource.ResourceStreamNotFoundException;
import wicket.util.string.AppendingStringBuffer;

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

	/** The root markup fragment of the markup being parsed */
	private final MarkupFragment rootFragment;

	/** The current list of markup elements */
	private List<MarkupElement> markupElements;

	/** True if comments are to be removed */
	private boolean stripComments;

	/** True if whitespaces are to be compressed */
	private boolean compressWhitespace;

	/**
	 * The default markup encoding which is replaced by MarkupParserFactory with
	 * the Application default
	 */
	private String defaultMarkupEncoding = "UTF-8";

	/**
	 * Constructor.
	 * 
	 * @param resource
	 *            The markup resource (file)
	 */
	public MarkupParser(final MarkupResourceStream resource)
	{
		this(new XmlPullParser(), resource);
	}

	/**
	 * Constructor.
	 * 
	 * @param xmlParser
	 *            The XML parser to use instead of the default on
	 * @param resource
	 *            The markup resource (file)
	 */
	public MarkupParser(final IXmlPullParser xmlParser, final MarkupResourceStream resource)
	{
		// Create the markup before we initialize the chain
		this.markup = new Markup();
		this.markup.setResource(resource);
		this.rootFragment = new MarkupFragment(this.markup);

		// Create a XML parser
		this.xmlParser = xmlParser;

		// Initialize the markup filter chain
		initializeMarkupFilters();
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
	 * Sets compressWhitespace.
	 * 
	 * @param compressWhitespace
	 *            compressWhitespace
	 */
	public final void setCompressWhitespace(final boolean compressWhitespace)
	{
		this.compressWhitespace = compressWhitespace;
	}

	/**
	 * Sets defaultMarkupEncoding.
	 * 
	 * @param defaultMarkupEncoding
	 *            defaultMarkupEncoding
	 */
	public final void setDefaultMarkupEncoding(final String defaultMarkupEncoding)
	{
		this.defaultMarkupEncoding = defaultMarkupEncoding;
	}

	/**
	 * Sets stripComments.
	 * 
	 * @param stripComments
	 *            stripComments
	 */
	public final void setStripComments(final boolean stripComments)
	{
		this.stripComments = stripComments;
	}

	/**
	 * Gets the Markup of the resource
	 * 
	 * @return The markup resource stream
	 */
	public final IMarkup getMarkup()
	{
		return this.rootFragment.getMarkup();
	}

	/**
	 * Like internal filters, user provided filters might need access to the
	 * markup as well. But be careful, the list contains only the markup
	 * elements processed so far.
	 * 
	 * @return The markup resource stream
	 */
	public final List<MarkupElement> getTemporaryListOfMarkupElements()
	{
		return this.markupElements;
	}

	/**
	 * Create a new markup filter chain and initialize with all default filters
	 * required.
	 */
	private final void initializeMarkupFilters()
	{
		// Chain together all the different markup filters and configure them
		this.markupFilterChain = xmlParser;
		xmlParser.setPriority(0);

		registerMarkupFilter(new WicketTagIdentifier(this.markup));
		registerMarkupFilter(new TagTypeHandler());
		registerMarkupFilter(new HtmlHandler());
		registerMarkupFilter(new WicketRemoveTagHandler());
		registerMarkupFilter(new WicketLinkTagHandler());
		registerMarkupFilter(new WicketNamespaceHandler(this.markup));

		// Provided the wicket component requesting the markup is known ...
		final MarkupResourceStream resource = this.markup.getResource();
		if (resource != null)
		{
			final ContainerInfo containerInfo = resource.getContainerInfo();
			if (containerInfo != null)
			{
				registerMarkupFilter(new WicketMessageTagHandler());
				registerMarkupFilter(new BodyOnLoadHandler());

				// Pages require additional handlers
				if (Page.class.isAssignableFrom(containerInfo.getContainerClass()))
				{
					registerMarkupFilter(new HtmlHeaderSectionHandler(this));
				}

				registerMarkupFilter(new HeadForceTagIdHandler(containerInfo.getContainerClass()));
			}
		}
		
		registerMarkupFilter(new PrependContextPathHandler());
	}

	/**
	 * Append a new filter to the list of already pre-configured markup filters.
	 * To be used by subclasses which implement {@link #initFilterChain()}.
	 * 
	 * @param filter
	 *            The filter to be appended
	 */
	public final void registerMarkupFilter(final IMarkupFilter filter)
	{
		IMarkupFilter parent = this.markupFilterChain;
		IMarkupFilter prevParent = null;
		while (parent.getPriority() > filter.getPriority())
		{
			prevParent = parent;
			parent = parent.getParent();
			if (parent == null)
			{
				throw new WicketRuntimeException("The filters priority is too low. Must be > 0. Filter: " + filter);
			}
		}
		filter.setParent(parent);
		if (parent == this.markupFilterChain)
		{
			this.markupFilterChain = filter;
		}
		else
		{
			prevParent.setParent(filter);
		}
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
	public final MarkupFragment readAndParse() throws IOException, ResourceStreamNotFoundException
	{
		// Initialize the xml parser
		this.xmlParser
				.parse(this.markup.getResource().getInputStream(), this.defaultMarkupEncoding);

		// parse the xml markup and tokenize it into wicket relevant markup
		// elements
		final MarkupFragment fragment = parseMarkup();

		this.markup.setEncoding(this.xmlParser.getEncoding());
		this.markup.setXmlDeclaration(this.xmlParser.getXmlDeclaration());

		return fragment;
	}

	/**
	 * Scans the given markup and extracts balancing tags.
	 * 
	 * @return The markup associated with the resource
	 */
	private MarkupFragment parseMarkup()
	{
		this.markupElements = new ArrayList<MarkupElement>(20);

		try
		{
			// always remember the latest index (size)
			int size = this.markupElements.size();

			// Loop through tags
			for (ComponentTag tag; null != (tag = (ComponentTag)this.markupFilterChain.nextTag());)
			{
				boolean add = (tag.getId() != null);
				if (!add && tag.getXmlTag().isClose())
				{
					add = ((tag.getOpenTag() != null) && (tag.getOpenTag().getId() != null));
				}

				// Add tag to list?
				if (add || tag.isModified())
				{
					final CharSequence text = this.xmlParser.getInputFromPositionMarker(tag
							.getPos());

					// Add text from last position to tag position
					if (text.length() > 0)
					{
						String rawMarkup = text.toString();

						if (this.stripComments)
						{
							rawMarkup = removeComment(rawMarkup);
						}

						if (this.compressWhitespace)
						{
							rawMarkup = compressWhitespace(rawMarkup);
						}

						// Make sure you add it at the correct location.
						// IMarkupFilters might have added elements as well.
						this.markupElements.add(size, new RawMarkup(rawMarkup));
					}

					if (add)
					{
						// Add to list unless preview component tag remover
						// flagged as removed
						if (!WicketRemoveTagHandler.IGNORE.equals(tag.getId()))
						{
							this.markupElements.add(tag);
						}
					}
					else if (tag.isModified())
					{
						this.markupElements.add(new RawMarkup(tag.toCharSequence()));
					}

					this.xmlParser.setPositionMarker();
				}

				// allways remember the latest index (size)
				size = this.markupElements.size();
			}
		}
		catch (final ParseException ex)
		{
			int size = this.markupElements.size();

			// Add remaining input string
			final CharSequence text = this.xmlParser.getInputFromPositionMarker(-1);
			if (text.length() > 0)
			{
				this.markupElements.add(new RawMarkup(text));
			}

			this.markup.setEncoding(this.xmlParser.getEncoding());
			this.markup.setXmlDeclaration(this.xmlParser.getXmlDeclaration());

			// Create a MarkupStream and position it at the error location
			for (MarkupElement elem : this.markupElements)
			{
				this.rootFragment.addMarkupElement(elem);
			}
			MarkupStream markupStream = new MarkupStream(this.rootFragment);
			markupStream.setCurrentIndex(size);

			throw new MarkupException(markupStream, ex.getMessage(), ex);
		}

		// Add tail?
		final CharSequence text = this.xmlParser.getInputFromPositionMarker(-1);
		if (text.length() > 0)
		{
			String rawMarkup = text.toString();

			if (this.stripComments)
			{
				rawMarkup = removeComment(rawMarkup);
			}

			if (this.compressWhitespace)
			{
				rawMarkup = compressWhitespace(rawMarkup);
			}

			this.markupElements.add(new RawMarkup(rawMarkup));
		}

		// Convert the list into a MarkupFragment tree
		if (this.markupElements.size() > 0)
		{
			Stack<MarkupFragment> stack = new Stack<MarkupFragment>();
			stack.push(this.rootFragment);
			MarkupFragment currentFragment = this.rootFragment;

			for (MarkupElement elem : this.markupElements)
			{
				if (elem instanceof RawMarkup)
				{
					currentFragment.addMarkupElement(elem);
				}
				else
				{
					ComponentTag tag = (ComponentTag)elem;
					if (tag.isOpen() || tag.isOpenClose())
					{
						// Push the current markup fragment on the stack
						stack.push(currentFragment);

						// and create a new MarkupFragment
						MarkupFragment newFragment = new MarkupFragment(this.rootFragment
								.getMarkup());

						// add the new fragment to its parent
						currentFragment.addMarkupElement(newFragment);

						// make the new fragment the current one
						currentFragment = newFragment;
					}

					// Add the tag to the fragment
					currentFragment.addMarkupElement(tag);

					// If required "close" the fragment and continue with the
					// parent fragment
					if (tag.isOpenClose() || tag.isClose() || tag.hasNoCloseTag())
					{
						currentFragment = stack.pop();
					}
				}
			}
			
			// Avoid an "empty" root fragment. Return the first fragment instead
			if (this.rootFragment.size() == 1)
			{
				MarkupElement elem = this.rootFragment.get(0);
				if (elem instanceof MarkupFragment)
				{
					return (MarkupFragment) elem;
				}
			}
		}

		return this.rootFragment;
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
	 * <code>"<!--(.|\n|\r)*-->"<code>
	 * causes a stack overflow in some circumstances (jdk 1.5)
	 * <p>
	 * Conditional comments such as <ocde>&lt;!--[if IE]&gt;...&lt;![endif]&gt;<code>
	 * are NOT treated as comments. 
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
					buf.append(rawMarkup.substring(pos2 + 4));
					rawMarkup = buf.toString();
				}
			}
			pos1 = rawMarkup.indexOf("<!--", pos1 + 4);
		}
		return rawMarkup;
	}
}
