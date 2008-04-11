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
package org.apache.wicket.util.convert.converters;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import junit.framework.TestCase;

import org.apache.wicket.IConverterLocator;
import org.apache.wicket.util.convert.ConversionException;
import org.apache.wicket.util.convert.ConverterLocator;

/**
 * Tests for the base converters.
 * 
 * @author Eelco Hillenius
 */
public final class ConvertersTest extends TestCase
{
	/** Dutch locale for localized testing. */
	private static final Locale DUTCH_LOCALE = new Locale("nl", "NL");

	/**
	 * Construct.
	 */
	public ConvertersTest()
	{
		super();
	}

	/**
	 * Construct.
	 * 
	 * @param name
	 */
	public ConvertersTest(String name)
	{
		super(name);
	}

	/**
	 * Test generalized conversion
	 */
	public void testConversion()
	{
		final IConverterLocator converter = new ConverterLocator();
		assertEquals("7", converter.getConverter(Integer.class).convertToString(new Integer(7),
			Locale.US));
		assertEquals("7.1", converter.getConverter(Double.class).convertToString(new Double(7.1),
			Locale.US));
		assertEquals("7,1", converter.getConverter(Double.class).convertToString(new Double(7.1),
			DUTCH_LOCALE));

		Calendar cal = Calendar.getInstance(DUTCH_LOCALE);
		cal.clear();
		cal.set(2002, Calendar.OCTOBER, 24);
		Date date = cal.getTime();

		assertEquals(date, converter.getConverter(Date.class).convertToObject("24-10-02",
			DUTCH_LOCALE));
		assertEquals("24-10-02", converter.getConverter(Date.class).convertToString(date,
			DUTCH_LOCALE));

		// empty strings should return null, NOT throw NPEs
		assertNull(converter.getConverter(Integer.class).convertToObject("", Locale.US));
		assertNull(converter.getConverter(Byte.class).convertToObject("", Locale.US));
		assertNull(converter.getConverter(Character.class).convertToObject("", Locale.US));
		assertNull(converter.getConverter(Float.class).convertToObject("", Locale.US));
		assertNull(converter.getConverter(Long.class).convertToObject("", Locale.US));
		assertNull(converter.getConverter(Short.class).convertToObject("", Locale.US));
		assertNull(converter.getConverter(Date.class).convertToObject("", Locale.US));
		assertNull(converter.getConverter(Double.class).convertToObject("", Locale.US));
		assertEquals(Boolean.FALSE, converter.getConverter(Boolean.class).convertToObject("",
			Locale.US));
		assertNotNull(converter.getConverter(String.class).convertToObject("", Locale.US));
	}

	/**
	 * @throws Exception
	 */
	public void testThousandSeperator() throws Exception
	{
		BigDecimalConverter bdc = new BigDecimalConverter();
		assertEquals(new BigDecimal(3000), bdc.convertToObject("3 000", Locale.FRENCH));

		DoubleConverter dc = new DoubleConverter();
		assertEquals(new Double(3000), dc.convertToObject("3 000", Locale.FRENCH));

	}

	/**
	 * @throws Exception
	 *             WICKET-1344 public void testBigDecimalRounding() throws Exception {
	 *             BigDecimalConverter bdc = new BigDecimalConverter(); assertEquals("123.45",
	 *             bdc.convertToObject("123.45", Locale.ENGLISH).toString());
	 *  }
	 */

	/**
	 * Test boolean conversions.
	 */
	public void testBooleanConversions()
	{
		BooleanConverter booleanConverter = new BooleanConverter();
		assertEquals("true", booleanConverter.convertToString(Boolean.TRUE, Locale.getDefault()));
		assertEquals("false", booleanConverter.convertToString(Boolean.FALSE, Locale.getDefault()));
		assertEquals(Boolean.TRUE, booleanConverter.convertToObject("true", Locale.getDefault()));
		assertEquals(Boolean.FALSE, booleanConverter.convertToObject("false", Locale.getDefault()));
		try
		{
			booleanConverter.convertToObject("whatever", Locale.getDefault());
			fail("Conversion should have thrown an exception");
		}
		catch (ConversionException e)
		{
			// this is correct
		}
	}

	/**
	 * Test byte conversions.
	 */
	public void testByteConversions()
	{
		ByteConverter converter = new ByteConverter();
		assertEquals(new Byte((byte)10), converter.convertToObject("10", Locale.US));
		assertEquals("10", converter.convertToString(new Byte((byte)10), Locale.US));
		try
		{
			converter.convertToObject("whatever", Locale.US);
			fail("Conversion should have thrown an exception");
		}
		catch (ConversionException e)
		{
			// This is correct
		}
		try
		{
			converter.convertToObject("10whatever", Locale.US);
			fail("Conversion should have thrown an exception");
		}
		catch (ConversionException e)
		{
			// This is correct
		}
		try
		{
			converter.convertToObject("256", Locale.US);
			fail("Conversion should have thrown an exception");
		}
		catch (ConversionException e)
		{
			// This is correct
		}
	}

	/**
	 * Test double conversions.
	 */
	public void testDoubleConversions()
	{
		DoubleConverter converter = new DoubleConverter();
		assertEquals(new Double(1.1), converter.convertToObject("1.1", Locale.US));
		assertEquals("1.1", converter.convertToString(new Double(1.1), Locale.US));
		try
		{
			converter.convertToObject("whatever", Locale.US);
			fail("Conversion should have thrown an exception");
		}
		catch (ConversionException e)
		{
			// this is correct
		}
		try
		{
			converter.convertToObject("1.1whatever", Locale.US);
			fail("Conversion should have thrown an exception");
		}
		catch (ConversionException e)
		{
			// this is correct
		}
	}

	/**
	 * Test float conversions.
	 */
	public void testFloatConversions()
	{
		FloatConverter converter = new FloatConverter();
		assertEquals(new Float(1.1), converter.convertToObject("1.1", Locale.US));
		assertEquals("1.1", converter.convertToString(new Float(1.1), Locale.US));
		try
		{
			converter.convertToObject("whatever", Locale.US);
			fail("Conversion should have thrown an exception");
		}
		catch (ConversionException e)
		{
			// this is correct
		}
		try
		{
			converter.convertToObject("1.1whatever", Locale.US);
			fail("Conversion should have thrown an exception");
		}
		catch (ConversionException e)
		{
			// this is correct
		}
	}

	/**
	 * Test integer conversions.
	 */
	public void testIntegerConversions()
	{
		IntegerConverter converter = new IntegerConverter();
		assertEquals(new Integer(10), converter.convertToObject("10", Locale.US));
		assertEquals("10", converter.convertToString(new Integer(10), Locale.US));
		try
		{
			converter.convertToObject("whatever", Locale.US);
			fail("Conversion should have thrown an exception");
		}
		catch (ConversionException e)
		{
			// This is correct
		}
		try
		{
			converter.convertToObject("10whatever", Locale.US);
			fail("Conversion should have thrown an exception");
		}
		catch (ConversionException e)
		{
			// This is correct
		}
		try
		{
			converter.convertToObject("" + ((long)Integer.MAX_VALUE + 1), Locale.US);
			fail("Conversion should have thrown an exception");
		}
		catch (ConversionException e)
		{
			// This is correct
		}
	}

	/**
	 * Test long conversions.
	 */
	public void testLongConversions()
	{
		LongConverter converter = new LongConverter();
		assertEquals(new Long(10), converter.convertToObject("10", Locale.US));
		assertEquals("10", converter.convertToString(new Long(10), Locale.US));
		try
		{
			converter.convertToObject("whatever", Locale.US);
			fail("Conversion should have thrown an exception");
		}
		catch (ConversionException e)
		{
			// This is correct
		}
		try
		{
			converter.convertToObject("10whatever", Locale.US);
			fail("Conversion should have thrown an exception");
		}
		catch (ConversionException e)
		{
			// This is correct
		}
		try
		{
			converter.convertToObject("" + Long.MAX_VALUE + "0", Locale.US);
			fail("Conversion should have thrown an exception");
		}
		catch (ConversionException e)
		{
			// This is correct
		}
	}

	/**
	 * Test short conversions
	 */
	public void testShortConversions()
	{
		ShortConverter converter = new ShortConverter();
		assertEquals(new Short((short)10), converter.convertToObject("10", Locale.US));
		assertEquals("10", converter.convertToString(new Short((short)10), Locale.US));
		try
		{
			converter.convertToObject("whatever", Locale.US);
			fail("Conversion should have thrown an exception");
		}
		catch (ConversionException e)
		{
			// This is correct
		}
		try
		{
			converter.convertToObject("10whatever", Locale.US);
			fail("Conversion should have thrown an exception");
		}
		catch (ConversionException e)
		{
			// This is correct
		}
		try
		{
			converter.convertToObject("" + (Short.MAX_VALUE + 1), Locale.US);
			fail("Conversion should have thrown an exception");
		}
		catch (ConversionException e)
		{
			// This is correct
		}
	}

	/**
	 * Test date locale conversions.
	 */
	public void testDateConverter()
	{
		DateConverter converter = new DateConverter();

		Calendar cal = Calendar.getInstance(DUTCH_LOCALE);
		cal.clear();
		cal.set(2002, Calendar.OCTOBER, 24);
		Date date = cal.getTime();

		assertEquals("24-10-02", converter.convertToString(date, DUTCH_LOCALE));
		assertEquals(date, converter.convertToObject("24-10-02", DUTCH_LOCALE));

		assertEquals("10/24/02", converter.convertToString(date, Locale.US));
		assertEquals(date, converter.convertToObject("10/24/02", Locale.US));

		try
		{
			converter.convertToObject("whatever", Locale.US);
			fail("Conversion should have thrown an exception");
		}
		catch (ConversionException e)
		{
			// this is correct
		}
		try
		{
			converter.convertToObject("10/24/02whatever", Locale.US);
			fail("Conversion should have thrown an exception");
		}
		catch (ConversionException e)
		{
			// this is correct
		}
	}
}