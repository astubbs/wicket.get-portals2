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
package wicket.markup.transformer;

import wicket.Component;
import wicket.markup.ComponentTag;

/**
 * An IBehavior which can be added to any component except ListView. It allows
 * to post-process (XSLT) the markup generated by the component. The *.xsl
 * resource must be located in the same path as the nearest parent with an
 * associated markup and must have a filename equal to the component's id.
 * <p>
 * The containers tag will be the root element of the xml data applied for
 * transformation to ensure the xml data are well formed (single root element).
 * In addition the attribute
 * <code>xmlns:wicket="http://wicket.sourceforge.net"</code> is added to the
 * root element to allow the XSL processor to handle the wicket namespace.
 * <p>
 * The reason why the transformer can not be used to XSLT the ListViews output
 * is because of the ListViews markup being reused for each ListItem. Please use
 * a XsltOutputTransformerContainer instead. Note: if the ListView is used to
 * print a list of &lt;tr&gt; tags, than the transformer container must enclose
 * the &lt;table&gt; tag as well to be HTML compliant.
 * 
 * @see wicket.markup.transformer.AbstractOutputTransformerContainer
 * @see wicket.markup.transformer.XsltOutputTransformerContainer
 * 
 * @author Juergen Donnerstag
 */
public class XsltTransfomerBehavior extends AbstractTransformerBehavior
{
	private static final long serialVersionUID = 1L;

	/** An optional xsl file path */
	private final String xslFile;

	/**
	 * Construct.
	 */
	public XsltTransfomerBehavior()
	{
		this.xslFile = null;
	}

	/**
	 * Instead of using the default mechanism to determine the associated XSL
	 * file, it is given by the user.
	 * 
	 * @param xslFilePath
	 *            XSL file path
	 */
	public XsltTransfomerBehavior(final String xslFilePath)
	{
		this.xslFile = xslFilePath;
	}

	/**
	 * @see wicket.behavior.IBehavior#onComponentTag(wicket.Component,
	 *      wicket.markup.ComponentTag)
	 */
	@Override
	public void onComponentTag(final Component component, final ComponentTag tag)
	{
		tag.put("xmlns:wicket", "http://wicket.sourceforge.net");

		// Make the XSLT processor happy and allow him to handle the wicket
		// tags and attributes which are in the wicket namespace
		super.onComponentTag(component, tag);
	}

	/**
	 * 
	 * @see wicket.markup.transformer.ITransformer#transform(wicket.Component,
	 *      java.lang.String)
	 */
	@Override
	public CharSequence transform(final Component component, final String output) throws Exception
	{
		return new XsltTransformer(this.xslFile).transform(component, output);
	}
}
