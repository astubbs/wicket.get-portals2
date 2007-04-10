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
package org.apache.wicket.markup.transformer;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 * A container which output markup will be processes by a XSLT processor prior
 * to writing the output into the web response. The *.xsl resource must be
 * located in the same path as the nearest parent with an associated markup and
 * must have a filename equal to the component's id.
 * <p>
 * The containers tag will be the root element of the xml data applied for
 * transformation to ensure the xml data are well formed (single root element).
 * In addition the attribute
 * <code>xmlns:wicket="http://wicket.apache.org"</code> is added to the
 * root element to allow the XSL processor to handle the org.apache.wicket namespace.
 * <p>
 * Similar to this container, a <code>IBehavior</code> is available which does
 * the same, but does not require an additional Container.
 * 
 * @see org.apache.wicket.markup.transformer.XsltTransfomerBehavior
 * 
 * @author Juergen Donnerstag
 */
public class XsltOutputTransformerContainer extends AbstractOutputTransformerContainer
{
	private static final long serialVersionUID = 1L;

	/** An optional xsl file path */
	private final String xslFile;

	/**
	 * Instead of using the default mechanism to determine the associated XSL
	 * file, it is given by the user.
	 * 
	 * @see org.apache.wicket.Component#Component(String, IModel)
	 * 
	 * @param xslFilePath
	 *            XSL input file path
	 */
	public XsltOutputTransformerContainer(final String id, final IModel model,
			final String xslFilePath)
	{
		super(id);

		this.xslFile = xslFilePath;

		// The containers tag will be transformed as well. Thus we make sure
		// that
		// the xml provided to the xsl processor is well formed (has a single
		// root element)
		setTransformBodyOnly(false);

		// Make the XSLT processor happy and allow him to handle the org.apache.wicket
		// tags and attributes which are in the org.apache.wicket namespace
		add(new AttributeModifier("xmlns:wicket", true, new Model("http://wicket.apache.org")));
	}

	/**
	 * Construct
	 * 
	 * @see org.apache.wicket.Component#Component(String, IModel)
	 */
	public XsltOutputTransformerContainer(final String id, final IModel model)
	{
		this(id, model, null);
	}

	/**
	 * Construct
	 * 
	 * @see org.apache.wicket.Component#Component(String)
	 */
	public XsltOutputTransformerContainer(final String id)
	{
		this(id, null, null);
	}

	/**
	 * 
	 * @see org.apache.wicket.MarkupContainer#getMarkupType()
	 */
	public String getMarkupType()
	{
		return "xsl";
	}

	/**
	 * 
	 * @see org.apache.wicket.markup.transformer.ITransformer#transform(org.apache.wicket.Component,
	 *      java.lang.String)
	 */
	public CharSequence transform(final Component component, final String output) throws Exception
	{
		return new XsltTransformer(this.xslFile).transform(component, output);
	}
}
