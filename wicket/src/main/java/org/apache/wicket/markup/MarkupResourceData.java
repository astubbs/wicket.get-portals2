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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Holds markup as a resource (the stream that the markup came from) and a list of MarkupElements
 * (the markup itself).
 * 
 * @see MarkupElement
 * @see ComponentTag
 * @see org.apache.wicket.markup.RawMarkup
 * 
 * @author Jonathan Locke
 * @author Juergen Donnerstag
 */
public class MarkupResourceData
{
	private static final Logger log = LoggerFactory.getLogger(MarkupResourceData.class);

	/** Placeholder that indicates no markup */
	public static final MarkupResourceData NO_MARKUP_RESOURCE_DATA = new MarkupResourceData();

	/** The markup's resource stream */
	private MarkupResourceStream resource;

	/** In case of the inherited markup, this is the base markup */
	private Markup baseMarkup;

	/** If found in the markup, the <?xml ...?> string */
	private String xmlDeclaration;

	/** The encoding as found in <?xml ... encoding="" ?>. Null, else */
	private String encoding;

	/** Wicket namespace: <html xmlns:wicket="http://wicket.apache.org"> */
	private String wicketNamespace;

	/** == wicket namespace name + ":id" */
	private String wicketId;

	/**
	 * Constructor
	 */
	MarkupResourceData()
	{
		setWicketNamespace(ComponentTag.DEFAULT_WICKET_NAMESPACE);
	}

	/**
	 * @return String representation of markup list
	 */
	public String toString()
	{
		if (resource != null)
		{
			return resource.toString();
		}
		else
		{
			return "(unknown resource)";
		}
	}

	/**
	 * Gets the resource that contains this markup
	 * 
	 * @return The resource where this markup came from
	 */
	public MarkupResourceStream getResource()
	{
		return resource;
	}

	/**
	 * Return the XML declaration string, in case if found in the markup.
	 * 
	 * @return Null, if not found.
	 */
	public String getXmlDeclaration()
	{
		return xmlDeclaration;
	}

	/**
	 * Gets the markup encoding. A markup encoding may be specified in a markup file with an XML
	 * encoding specifier of the form &lt;?xml ... encoding="..." ?&gt;.
	 * 
	 * @return Encoding, or null if not found.
	 */
	public String getEncoding()
	{
		return encoding;
	}

	/**
	 * Get the wicket namespace valid for this specific markup
	 * 
	 * @return wicket namespace
	 */
	public String getWicketNamespace()
	{
		return wicketNamespace;
	}

	/**
	 * 
	 * @return usually it is "wicket:id"
	 */
	final public String getWicketId()
	{
		return wicketId;
	}

	/**
	 * Sets encoding.
	 * 
	 * @param encoding
	 *            encoding
	 */
	final void setEncoding(final String encoding)
	{
		this.encoding = encoding;
	}

	/**
	 * Sets wicketNamespace.
	 * 
	 * @param wicketNamespace
	 *            wicketNamespace
	 */
	public final void setWicketNamespace(final String wicketNamespace)
	{
		this.wicketNamespace = wicketNamespace;
		wicketId = wicketNamespace + ":id";

		if (!ComponentTag.DEFAULT_WICKET_NAMESPACE.equals(wicketNamespace))
		{
			log.info("You are using a non-standard component name: " + wicketNamespace);
		}
	}

	/**
	 * Sets xmlDeclaration.
	 * 
	 * @param xmlDeclaration
	 *            xmlDeclaration
	 */
	final void setXmlDeclaration(final String xmlDeclaration)
	{
		this.xmlDeclaration = xmlDeclaration;
	}

	/**
	 * Sets the resource stream associated with the markup. It is for diagnostic purposes only.
	 * 
	 * @param resource
	 *            the markup resource stream
	 */
	final void setResource(final MarkupResourceStream resource)
	{
		this.resource = resource;
	}

	/**
	 * Get the resource stream containing the base markup (markup inheritance)
	 * 
	 * @return baseMarkupResource Null, if not base markup
	 */
	public MarkupResourceData getBaseMarkupResourceData()
	{
		if (baseMarkup == null)
			return null;
		return baseMarkup.getMarkupResourceData();
	}

	/**
	 * In case of markup inheritance, the base markup.
	 * 
	 * @param baseMarkup
	 *            The base markup
	 */
	public void setBaseMarkup(Markup baseMarkup)
	{
		this.baseMarkup = baseMarkup;
	}

	/**
	 * In case of markup inheritance, the base markup resource.
	 * 
	 * @return The base markup
	 */
	public Markup getBaseMarkup()
	{
		return baseMarkup;
	}
}