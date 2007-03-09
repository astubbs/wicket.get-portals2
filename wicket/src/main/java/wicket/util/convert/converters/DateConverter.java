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
package wicket.util.convert.converters;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import wicket.util.convert.IConverter;

/**
 * Converts from Object to Date.
 * 
 * @author Eelco Hillenius
 */
public class DateConverter extends AbstractConverter
{
	private static final long serialVersionUID = 1L;

	/**
	 * The singleton instance for a character converter
	 */
	public static final IConverter INSTANCE = new DateConverter();

	/**
	 * @see wicket.util.convert.IConverter#convertToObject(java.lang.String,Locale)
	 */
	public Object convertToObject(final String value, Locale locale)
	{
		return parse(getDateFormat(locale), value, locale);
	}

	/**
	 * @see wicket.util.convert.IConverter#convertToString(java.lang.String,
	 *      Locale)
	 */
	public String convertToString(final Object value, Locale locale)
	{
		final DateFormat dateFormat = getDateFormat(locale);
		if (dateFormat != null)
		{
			return dateFormat.format(value);
		}
		return value.toString();
	}


	/**
	 * @param locale
	 * @return Returns the date format.
	 */
	public DateFormat getDateFormat(Locale locale)
	{
		if (locale == null)
		{
			locale = Locale.getDefault();
		}

		return DateFormat.getDateInstance(DateFormat.SHORT, locale);
	}

	/**
	 * @see wicket.util.convert.converters.AbstractConverter#getTargetType()
	 */
	protected Class/*<Date>*/ getTargetType()
	{
		return Date.class;
	}
}