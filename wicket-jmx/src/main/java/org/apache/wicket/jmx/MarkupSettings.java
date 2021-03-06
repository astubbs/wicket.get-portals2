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
package org.apache.wicket.jmx;


/**
 * Exposes Application related functionality for JMX.
 * 
 * @author eelcohillenius
 */
public class MarkupSettings implements MarkupSettingsMBean
{
	private final org.apache.wicket.Application application;

	/**
	 * Create.
	 * 
	 * @param application
	 */
	public MarkupSettings(org.apache.wicket.Application application)
	{
		this.application = application;
	}

	/**
	 * @see org.apache.wicket.jmx.MarkupSettingsMBean#getAutomaticLinking()
	 */
	public boolean getAutomaticLinking()
	{
		return application.getMarkupSettings().getAutomaticLinking();
	}

	/**
	 * @see org.apache.wicket.jmx.MarkupSettingsMBean#getCompressWhitespace()
	 */
	public boolean getCompressWhitespace()
	{
		return application.getMarkupSettings().getCompressWhitespace();
	}

	/**
	 * @see org.apache.wicket.jmx.MarkupSettingsMBean#getDefaultAfterDisabledLink()
	 */
	public String getDefaultAfterDisabledLink()
	{
		return application.getMarkupSettings().getDefaultAfterDisabledLink();
	}

	/**
	 * @see org.apache.wicket.jmx.MarkupSettingsMBean#getDefaultBeforeDisabledLink()
	 */
	public String getDefaultBeforeDisabledLink()
	{
		return application.getMarkupSettings().getDefaultBeforeDisabledLink();
	}

	/**
	 * @see org.apache.wicket.jmx.MarkupSettingsMBean#getDefaultMarkupEncoding()
	 */
	public String getDefaultMarkupEncoding()
	{
		return application.getMarkupSettings().getDefaultMarkupEncoding();
	}

	/**
	 * @see org.apache.wicket.jmx.MarkupSettingsMBean#getMarkupParserFactory()
	 */
	public String getMarkupParserFactory()
	{
		return Stringz.className(application.getMarkupSettings().getMarkupParserFactory());
	}

	/**
	 * @see org.apache.wicket.jmx.MarkupSettingsMBean#getStripComments()
	 */
	public boolean getStripComments()
	{
		return application.getMarkupSettings().getStripComments();
	}

	/**
	 * @see org.apache.wicket.jmx.MarkupSettingsMBean#getStripWicketTags()
	 */
	public boolean getStripWicketTags()
	{
		return application.getMarkupSettings().getStripWicketTags();
	}

	/**
	 * @see org.apache.wicket.jmx.MarkupSettingsMBean#getStripXmlDeclarationFromOutput()
	 */
	public boolean getStripXmlDeclarationFromOutput()
	{
		return application.getMarkupSettings().getStripXmlDeclarationFromOutput();
	}

	/**
	 * @see org.apache.wicket.jmx.MarkupSettingsMBean#setAutomaticLinking(boolean)
	 */
	public void setAutomaticLinking(boolean automaticLinking)
	{
		application.getMarkupSettings().setAutomaticLinking(automaticLinking);
	}

	/**
	 * @see org.apache.wicket.jmx.MarkupSettingsMBean#setCompressWhitespace(boolean)
	 */
	public void setCompressWhitespace(boolean compressWhitespace)
	{
		application.getMarkupSettings().setCompressWhitespace(compressWhitespace);
	}

	/**
	 * @see org.apache.wicket.jmx.MarkupSettingsMBean#setDefaultAfterDisabledLink(java.lang.String)
	 */
	public void setDefaultAfterDisabledLink(String defaultAfterDisabledLink)
	{
		application.getMarkupSettings().setDefaultAfterDisabledLink(defaultAfterDisabledLink);
	}

	/**
	 * @see org.apache.wicket.jmx.MarkupSettingsMBean#setDefaultBeforeDisabledLink(java.lang.String)
	 */
	public void setDefaultBeforeDisabledLink(String defaultBeforeDisabledLink)
	{
		application.getMarkupSettings().setDefaultBeforeDisabledLink(defaultBeforeDisabledLink);
	}

	/**
	 * @see org.apache.wicket.jmx.MarkupSettingsMBean#setDefaultMarkupEncoding(java.lang.String)
	 */
	public void setDefaultMarkupEncoding(String encoding)
	{
		application.getMarkupSettings().setDefaultMarkupEncoding(encoding);
	}

	/**
	 * @see org.apache.wicket.jmx.MarkupSettingsMBean#setStripComments(boolean)
	 */
	public void setStripComments(boolean stripComments)
	{
		application.getMarkupSettings().setStripComments(stripComments);
	}

	/**
	 * @see org.apache.wicket.jmx.MarkupSettingsMBean#setStripWicketTags(boolean)
	 */
	public void setStripWicketTags(boolean stripWicketTags)
	{
		application.getMarkupSettings().setStripWicketTags(stripWicketTags);
	}

	/**
	 * @see org.apache.wicket.jmx.MarkupSettingsMBean#setStripXmlDeclarationFromOutput(boolean)
	 */
	public void setStripXmlDeclarationFromOutput(boolean strip)
	{
		application.getMarkupSettings().setStripXmlDeclarationFromOutput(strip);
	}
}
