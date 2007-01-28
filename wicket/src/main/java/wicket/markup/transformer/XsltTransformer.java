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

import java.io.FileNotFoundException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import wicket.Application;
import wicket.Component;
import wicket.util.resource.IResourceStream;

/**
 * A processor to XSLT transform the output generated by a Component.
 * 
 * @see wicket.markup.transformer.XsltOutputTransformerContainer
 * @see wicket.markup.transformer.XsltTransfomerBehavior
 * 
 * @author Juergen Donnerstag
 */
public class XsltTransformer implements ITransformer
{
	private final static String extension = "xsl";

	/** an optional XSL file */
	private final String xslFile;

	/**
	 * Construct.
	 */
	public XsltTransformer()
	{
		this.xslFile = null;
	}

	/**
	 * Instead of using the default mechanism to determine the associated XSL
	 * file, it is given by the user.
	 * 
	 * @param xslFile
	 *            XSL input file path
	 */
	public XsltTransformer(final String xslFile)
	{
		if ((xslFile != null) && xslFile.endsWith(extension))
		{
			this.xslFile = xslFile.substring(0, xslFile.length() - extension.length() - 1);
		}
		else
		{
			this.xslFile = xslFile;
		}
	}

	/**
	 * Apply a XSL transformation to the markup generated by a component. The
	 * *.xsl resource must be located in the same path as the nearest parent
	 * with an associated markup and must have a filename equal to the
	 * component's id.
	 * 
	 * @see wicket.markup.transformer.ITransformer#transform(wicket.Component,
	 *      java.lang.String)
	 */
	public CharSequence transform(final Component component, final String output) throws Exception
	{
		IResourceStream resourceStream = getResourceStream(component);

		if (resourceStream == null)
		{
			throw new FileNotFoundException("Unable to find XSLT resource for "
					+ component.toString());
		}

		try
		{
			// 1. Instantiate a TransformerFactory.
			TransformerFactory tFactory = TransformerFactory.newInstance();

			// 2. Use the TransformerFactory to process the stylesheet Source
			// and
			// generate a Transformer.
			Transformer transformer = tFactory.newTransformer(new StreamSource(resourceStream
					.getInputStream()));

			// 3. Use the Transformer to transform an XML Source and send the
			// output to a Result object.
			StringWriter writer = new StringWriter();
			transformer.transform(new StreamSource(new StringReader(output)), new StreamResult(
					writer));

			return writer.getBuffer();
		}
		finally
		{
			resourceStream.close();
		}
	}

	/**
	 * Get the XSL resource stream
	 * 
	 * @param component
	 * 
	 * @return The XSLT file resource stream
	 */
	private IResourceStream getResourceStream(final Component component)
	{
		final IResourceStream resourceStream;

		String filePath = this.xslFile;
		if (filePath == null)
		{
			filePath = component.findParentWithAssociatedMarkup().getClass().getPackage().getName()
					.replace('.', '/')
					+ "/" + component.getId();
		}

		resourceStream = Application.get().getResourceSettings().getResourceStreamFactory().newResourceStream(
				getClass(), filePath, component.getStyle(), component.getLocale(),
				XsltTransformer.extension);

		return resourceStream;
	}
}
