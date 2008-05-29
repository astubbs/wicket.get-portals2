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
package org.apache.wicket.protocol.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.apache.wicket.Response;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.util.io.StringBufferWriter;
import org.apache.wicket.util.string.AppendingStringBuffer;


/**
 * Implementation of {@link HttpServletResponse} that saves the output in a string buffer. This is
 * used in REDIRECT_TO_BUFFER render strategy to create the buffer of the output that can be held on
 * to until the redirect part of the render strategy.
 * 
 * @author jcompagner
 */
class BufferedHttpServletResponse implements HttpServletResponse
{
	/** the print writer for the response */
	private StringBufferWriter sbw = new StringBufferWriter();
	private PrintWriter pw = new PrintWriter(sbw);

	/** cookies list */
	private List<Cookie> cookies;

	/** status code */
	private int status = -1;

	/** headers map */
	private Map<String, Object> headers;

	/** the real response for encoding the url */
	private HttpServletResponse realResponse;

	private String redirect;
	private String contentType;
	private byte[] byteBuffer;
	private Locale locale;
	private String encoding;

	/**
	 * Constructor.
	 * 
	 * @param realResponse
	 *            The real response for encoding the url
	 */
	public BufferedHttpServletResponse(HttpServletResponse realResponse)
	{
		this.realResponse = realResponse;
	}

	/**
	 * @see javax.servlet.http.HttpServletResponse#addCookie(javax.servlet.http.Cookie)
	 */
	public void addCookie(Cookie cookie)
	{
		isOpen();
		if (cookies == null)
		{
			cookies = new ArrayList<Cookie>(2);
		}
		cookies.add(cookie);
	}

	/**
	 * @see javax.servlet.http.HttpServletResponse#containsHeader(java.lang.String)
	 */
	public boolean containsHeader(String name)
	{
		isOpen();
		if (headers == null)
		{
			return false;
		}
		return headers.containsKey(name);
	}

	/**
	 * @see javax.servlet.http.HttpServletResponse#encodeURL(java.lang.String)
	 */
	public String encodeURL(String url)
	{
		isOpen();
		return realResponse.encodeURL(url);
	}

	/**
	 * @see javax.servlet.http.HttpServletResponse#encodeRedirectURL(java.lang.String)
	 */
	public String encodeRedirectURL(String url)
	{
		isOpen();
		return realResponse.encodeRedirectURL(url);
	}

	/**
	 * @see javax.servlet.http.HttpServletResponse#encodeUrl(java.lang.String)
	 * @deprecated
	 */
	@Deprecated
	public String encodeUrl(String url)
	{
		isOpen();
		return realResponse.encodeURL(url);
	}

	/**
	 * @see javax.servlet.http.HttpServletResponse#encodeRedirectUrl(java.lang.String)
	 * @deprecated
	 */
	@Deprecated
	public String encodeRedirectUrl(String url)
	{
		isOpen();
		return realResponse.encodeRedirectURL(url);
	}

	/**
	 * @see javax.servlet.http.HttpServletResponse#sendError(int, java.lang.String)
	 */
	public void sendError(int sc, String msg) throws IOException
	{
		isOpen();
		realResponse.sendError(sc, msg);
	}

	/**
	 * @see javax.servlet.http.HttpServletResponse#sendError(int)
	 */
	public void sendError(int sc) throws IOException
	{
		isOpen();
		realResponse.sendError(sc);
	}

	/**
	 * @see javax.servlet.http.HttpServletResponse#sendRedirect(java.lang.String)
	 */
	public void sendRedirect(String location) throws IOException
	{
		isOpen();
		redirect = location;
	}

	/**
	 * @return The redirect url
	 */
	public String getRedirectUrl()
	{
		isOpen();
		return redirect;
	}


	private void testAndCreateHeaders()
	{
		isOpen();
		if (headers == null)
		{
			headers = new HashMap<String, Object>();
		}
	}

	private void isOpen()
	{
		if (realResponse == null)
		{
			throw new WicketRuntimeException("the buffered servlet response already closed.");
		}
	}

	@SuppressWarnings("unchecked")
	private void addHeaderObject(String name, Object object)
	{
		Object previousObject = headers.get(name);
		if (previousObject == null)
		{
			headers.put(name, object);
		}
		else if (previousObject instanceof List)
		{
			((List)previousObject).add(object);
		}
		else
		{
			ArrayList<Object> list = new ArrayList<Object>();
			list.add(previousObject);
			list.add(object);
			headers.put(name, list);
		}
	}

	/**
	 * @see javax.servlet.http.HttpServletResponse#setDateHeader(java.lang.String, long)
	 */
	public void setDateHeader(String name, long date)
	{
		testAndCreateHeaders();
		headers.put(name, new Long(date));
	}

	/**
	 * @see javax.servlet.http.HttpServletResponse#addDateHeader(java.lang.String, long)
	 */
	public void addDateHeader(String name, long date)
	{
		testAndCreateHeaders();
		addHeaderObject(name, new Long(date));
	}

	/**
	 * @see javax.servlet.http.HttpServletResponse#setHeader(java.lang.String, java.lang.String)
	 */
	public void setHeader(String name, String value)
	{
		testAndCreateHeaders();
		headers.put(name, value);
	}

	/**
	 * @see javax.servlet.http.HttpServletResponse#addHeader(java.lang.String, java.lang.String)
	 */
	public void addHeader(String name, String value)
	{
		testAndCreateHeaders();
		addHeaderObject(name, value);
	}

	/**
	 * @see javax.servlet.http.HttpServletResponse#setIntHeader(java.lang.String, int)
	 */
	public void setIntHeader(String name, int value)
	{
		testAndCreateHeaders();
		headers.put(name, new Integer(value));
	}

	/**
	 * @see javax.servlet.http.HttpServletResponse#addIntHeader(java.lang.String, int)
	 */
	public void addIntHeader(String name, int value)
	{
		testAndCreateHeaders();
		addHeaderObject(name, new Integer(value));
	}

	/**
	 * @see javax.servlet.http.HttpServletResponse#setStatus(int)
	 */
	public void setStatus(int statusCode)
	{
		status = statusCode;
	}

	/**
	 * @see javax.servlet.http.HttpServletResponse#setStatus(int, java.lang.String)
	 * @deprecated use setStatus(int) instead
	 */
	@Deprecated
	public void setStatus(int sc, String sm)
	{
		throw new UnsupportedOperationException(
			"not supported in the buffered http response, use setStatus(int)");
	}

	/**
	 * @see javax.servlet.ServletResponse#getCharacterEncoding()
	 */
	public String getCharacterEncoding()
	{
		isOpen();
		return encoding;
	}

	/**
	 * Set the character encoding to use for the output.
	 * 
	 * @param encoding
	 */
	public void setCharacterEncoding(String encoding)
	{
		this.encoding = encoding;
	}

	/**
	 * @see javax.servlet.ServletResponse#getOutputStream()
	 */
	public ServletOutputStream getOutputStream() throws IOException
	{
		throw new UnsupportedOperationException("Cannot get output stream on BufferedResponse");
	}

	/**
	 * @see javax.servlet.ServletResponse#getWriter()
	 */
	public PrintWriter getWriter() throws IOException
	{
		isOpen();
		return pw;
	}

	/**
	 * @see javax.servlet.ServletResponse#setContentLength(int)
	 */
	public void setContentLength(int len)
	{
		isOpen();
		// ignored will be calculated when the buffer is really streamed.
	}

	/**
	 * @see javax.servlet.ServletResponse#setContentType(java.lang.String)
	 */
	public void setContentType(String type)
	{
		isOpen();
		contentType = type;
	}

	public String getContentType()
	{
		return contentType;
	}

	/**
	 * @see javax.servlet.ServletResponse#setBufferSize(int)
	 */
	public void setBufferSize(int size)
	{
		isOpen();
		// ignored every thing will be buffered
	}

	/**
	 * @see javax.servlet.ServletResponse#getBufferSize()
	 */
	public int getBufferSize()
	{
		isOpen();
		return Integer.MAX_VALUE;
	}

	/**
	 * @see javax.servlet.ServletResponse#flushBuffer()
	 */
	public void flushBuffer() throws IOException
	{
		isOpen();
	}

	/**
	 * @see javax.servlet.ServletResponse#resetBuffer()
	 */
	public void resetBuffer()
	{
		isOpen();
		sbw.reset();
	}

	/**
	 * @see javax.servlet.ServletResponse#isCommitted()
	 */
	public boolean isCommitted()
	{
		return pw == null;
	}

	/**
	 * @see javax.servlet.ServletResponse#reset()
	 */
	public void reset()
	{
		resetBuffer();
		headers = null;
		cookies = null;
	}

	/**
	 * @see javax.servlet.ServletResponse#setLocale(java.util.Locale)
	 */
	public void setLocale(Locale loc)
	{
		isOpen();
		locale = loc;
	}

	/**
	 * @see javax.servlet.ServletResponse#getLocale()
	 */
	public Locale getLocale()
	{
		isOpen();
		if (locale == null)
		{
			return realResponse.getLocale();
		}
		return locale;
	}

	/**
	 * @return The length of the complete string buffer
	 */
	public int getContentLength()
	{
		isOpen();
		return sbw.getStringBuffer().length();
	}

	/**
	 * THIS METHOD IS NOT PART OF THE WICKET PUBLIC API.
	 * 
	 * @param response
	 */
	public final void filter(Response response)
	{
		isOpen();
		AppendingStringBuffer buffer = sbw.getStringBuffer();
		if (redirect == null && buffer.length() != 0)
		{
			buffer = response.filter(buffer);
			sbw.setStringBuffer(buffer);
		}
	}

	/**
	 * 
	 */
	public void close()
	{
		isOpen();
		pw.close();
		byteBuffer = convertToCharset(sbw.getStringBuffer(), getCharacterEncoding());

		pw = null;
		sbw = null;
		realResponse = null;
	}

	/**
	 * Convert the string into the output encoding required
	 * 
	 * @param output
	 * 
	 * @param encoding
	 *            The output encoding
	 * @return byte[] The encoded characters converted into bytes
	 */
	private static byte[] convertToCharset(final AppendingStringBuffer output, final String encoding)
	{
		if (encoding == null)
		{
			throw new WicketRuntimeException("Internal error: encoding must not be null");
		}

		final ByteArrayOutputStream baos = new ByteArrayOutputStream((int)(output.length() * 1.2));

		final OutputStreamWriter osw;
		final byte[] bytes;
		try
		{
			osw = new OutputStreamWriter(baos, encoding);
			osw.write(output.getValue(), 0, output.length());
			osw.close();

			bytes = baos.toByteArray();
		}
		catch (Exception ex)
		{
			throw new WicketRuntimeException("Can't convert response to charset: " + encoding, ex);
		}

		return bytes;
	}

	/**
	 * @param servletResponse
	 * @throws IOException
	 */
	public void writeTo(HttpServletResponse servletResponse) throws IOException
	{
		if (status != -1)
		{
			servletResponse.setStatus(status);
		}
		if (headers != null)
		{
			Iterator<Entry<String, Object>> it = headers.entrySet().iterator();
			while (it.hasNext())
			{
				Entry<String, Object> entry = it.next();
				String name = entry.getKey();
				Object value = entry.getValue();
				if (value instanceof List)
				{
					List<?> lst = (List<?>)value;
					for (int i = 0; i < lst.size(); i++)
					{
						addHeader(name, lst.get(i), servletResponse);
					}
				}
				else
				{
					setHeader(name, value, servletResponse);
				}
			}
		}

		if (cookies != null)
		{
			for (int i = 0; i < cookies.size(); i++)
			{
				Cookie cookie = cookies.get(i);
				servletResponse.addCookie(cookie);
			}
		}
		if (locale != null)
		{
			servletResponse.setLocale(locale);
		}
		// got a buffered response; now write it
		servletResponse.setContentLength(byteBuffer.length);
		servletResponse.setContentType(contentType);

		final OutputStream out = servletResponse.getOutputStream();
		out.write(byteBuffer);
		out.close();

	}

	/**
	 * @param name
	 *            Name of the header to set
	 * @param value
	 *            The value can be String/Long/Int
	 * @param servletResponse
	 *            The response to set it to.
	 */
	private static void setHeader(String name, Object value, HttpServletResponse servletResponse)
	{
		if (value instanceof String)
		{
			servletResponse.setHeader(name, (String)value);
		}
		else if (value instanceof Long)
		{
			servletResponse.setDateHeader(name, ((Long)value).longValue());
		}
		else if (value instanceof Integer)
		{
			servletResponse.setIntHeader(name, ((Integer)value).intValue());
		}
	}

	/**
	 * @param name
	 *            Name of the header to set
	 * @param value
	 *            The value can be String/Long/Int
	 * @param servletResponse
	 *            The response to set it to.
	 */
	private static void addHeader(String name, Object value, HttpServletResponse servletResponse)
	{
		if (value instanceof String)
		{
			servletResponse.addHeader(name, (String)value);
		}
		else if (value instanceof Long)
		{
			servletResponse.addDateHeader(name, ((Long)value).longValue());
		}
		else if (value instanceof Integer)
		{
			servletResponse.addIntHeader(name, ((Integer)value).intValue());
		}
	}
}