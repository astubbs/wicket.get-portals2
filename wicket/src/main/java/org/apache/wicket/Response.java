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
package org.apache.wicket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;
import java.net.URLConnection;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;

import javax.servlet.ServletContext;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.util.io.Streams;
import org.apache.wicket.util.string.AppendingStringBuffer;
import org.apache.wicket.util.string.Strings;
import org.apache.wicket.util.time.Time;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Abstract base class for different implementations of response writing. A subclass must implement
 * write(String) to write a String to the response destination (whether it be a browser, a file, a
 * test harness or some other place). A subclass may optionally implement close(),
 * encodeURL(String), redirect(String), isRedirect() or setContentType(String) as appropriate.
 * 
 * @author Jonathan Locke
 */
public abstract class Response
{
	private static final Logger log = LoggerFactory.getLogger(Response.class);

	/** Default encoding of output stream */
	private String defaultEncoding;

	/**
	 * Closes the response output stream
	 */
	public void close()
	{
	}

	/**
	 * Called when the Response needs to reset itself. Subclasses can empty there buffer or build up
	 * state.
	 */
	public void reset()
	{
	}

	/**
	 * An implementation of this method is only required if a subclass wishes to support sessions
	 * via URL rewriting. This default implementation simply returns the URL String it is passed.
	 * 
	 * @param url
	 *            The URL to encode
	 * @return The encoded url
	 */
	public CharSequence encodeURL(final CharSequence url)
	{
		return url;
	}

	/**
	 * THIS METHOD IS NOT PART OF THE WICKET PUBLIC API. DO NOT USE IT.
	 * 
	 * Loops over all the response filters that were set (if any) with the give response returns the
	 * response buffer itself if there where now filters or the response buffer that was
	 * created/returned by the filter(s)
	 * 
	 * @param responseBuffer
	 *            The response buffer to be filtered
	 * @return Returns the filtered string buffer.
	 */
	public final AppendingStringBuffer filter(AppendingStringBuffer responseBuffer)
	{
		List<IResponseFilter> responseFilters = Application.get()
			.getRequestCycleSettings()
			.getResponseFilters();

		if (responseFilters == null)
		{
			return responseBuffer;
		}

		for (int i = 0; i < responseFilters.size(); i++)
		{
			IResponseFilter filter = responseFilters.get(i);
			responseBuffer = filter.filter(responseBuffer);
		}
		return responseBuffer;
	}

	/**
	 * Get the default encoding
	 * 
	 * @return default encoding
	 */
	public String getCharacterEncoding()
	{
		if (defaultEncoding == null)
		{
			return Application.get().getRequestCycleSettings().getResponseRequestEncoding();
		}
		else
		{
			return defaultEncoding;
		}
	}

	/**
	 * @return The output stream for this response
	 */
	public abstract OutputStream getOutputStream();

	/**
	 * Returns true if a redirection has occurred. The default implementation always returns false
	 * since redirect is not implemented by default.
	 * 
	 * @return True if the redirect method has been called, making this response a redirect.
	 */
	public boolean isRedirect()
	{
		return false;
	}

	/**
	 * CLIENTS SHOULD NEVER CALL THIS METHOD FOR DAY TO DAY USE!
	 * <p>
	 * A subclass may override this method to implement redirection. Subclasses which have no need
	 * to do redirection may choose not to override this default implementation, which does nothing.
	 * For example, if a subclass wishes to write output to a file or is part of a testing harness,
	 * there may be no meaning to redirection.
	 * </p>
	 * <p>
	 * Framework users who want to redirect should use a construction like <code>
	 * RequestCycle.get().setRequestTarget(new RedirectRequestTarget(...));
	 * </code>
	 * or <code>
	 * setResponsePage(new RedirectPage(...));
	 * </code>
	 * </p>
	 * 
	 * @param url
	 *            The URL to redirect to
	 */
	public void redirect(final String url)
	{
	}

	/**
	 * Set the default encoding for the output. Note: It is up to the derived class to make use of
	 * the information. Class Response simply stores the value, but does not apply it anywhere
	 * automatically.
	 * 
	 * @param encoding
	 */
	public void setCharacterEncoding(final String encoding)
	{
		defaultEncoding = encoding;
	}

	/**
	 * Set the content length on the response, if appropriate in the subclass. This default
	 * implementation does nothing.
	 * 
	 * @param length
	 *            The length of the content
	 */
	public void setContentLength(final long length)
	{
	}

	/**
	 * Set the content type on the response, if appropriate in the subclass. This default
	 * implementation does nothing.
	 * 
	 * @param mimeType
	 *            The mime type
	 */
	public void setContentType(final String mimeType)
	{
	}

	/**
	 * Set the contents last modified time, if appropriate in the subclass. This default
	 * implementation does nothing.
	 * 
	 * @param time
	 *            The time object
	 */
	public void setLastModifiedTime(Time time)
	{
	}

	/**
	 * @param locale
	 *            Locale to use for this response
	 */
	public void setLocale(final Locale locale)
	{
	}

	/**
	 * Writes the given tag to via the write(String) abstract method.
	 * 
	 * @param tag
	 *            The tag to write
	 */
	public final void write(final ComponentTag tag)
	{
		write(tag.toString());
	}

	/**
	 * Writes the given string to the Response subclass output destination.
	 * 
	 * @param string
	 *            The string to write
	 */
	public abstract void write(final CharSequence string);

	/**
	 * Either throws the exception wrapped as {@link WicketRuntimeException} or silently ignores it.
	 * This method should ignore IO related exceptions like connection reset by peer or broken pipe.
	 * 
	 * @param e
	 */
	private void handleException(Exception e)
	{
		// FIXME this doesn't catch all. For instance, Jetty (6/ NIO) on
		// Unix like platforms will not be recognized as exceptions
		// that should be ignored
		Throwable throwable = e;
		boolean ignoreException = false;
		while (throwable != null)
		{
			if (throwable instanceof SQLException)
			{
				break; // leave false and quit loop
			}
			else if (throwable instanceof SocketException)
			{
				String message = throwable.getMessage();
				ignoreException = message != null &&
					(message.indexOf("Connection reset") != -1 ||
						message.indexOf("Broken pipe") != -1 ||
						message.indexOf("Socket closed") != -1 || message.indexOf("connection abort") != -1);
			}
			else
			{
				ignoreException = throwable.getClass().getName().indexOf("ClientAbortException") >= 0 ||
					throwable.getClass().getName().indexOf("EofException") >= 0;
			}
			if (ignoreException)
			{
				if (log.isDebugEnabled())
				{
					log.debug("Socket exception ignored for sending Resource "
						+ "response to client (ClientAbort)", e);
				}
				break;
			}
			throwable = throwable.getCause();
		}
		if (!ignoreException)
		{
			throw new WicketRuntimeException("Unable to write the response", e);
		}
	}

	/**
	 * Copies the given input stream to the servlet response
	 * <p>
	 * NOTE Content-Length is not set because it would require to buffer the whole input stream
	 * </p>
	 * 
	 * @param in
	 *            input stream to copy, will be closed after copy
	 */
	public void write(InputStream in)
	{
		OutputStream out = getOutputStream();

		try
		{
			// Copy resource input stream to servlet output stream
			Streams.copy(in, out);
		}
		catch (Exception e)
		{
			handleException(e);
		}
		finally
		{
			// NOTE: We only close the InputStream. The servlet
			// container should close the output stream.
			try
			{
				in.close();
				out.flush();
			}
			catch (IOException e)
			{
				// jetty 6 throws broken pipe exception here too
				handleException(e);
			}
		}
	}

	/**
	 * Writes the given string to the Response subclass output destination and appends a cr/nl
	 * depending on the OS
	 * 
	 * @param string
	 */
	public final void println(final CharSequence string)
	{
		write(string);
		write(Strings.LINE_SEPARATOR);
	}

	/**
	 * Sets the Content-Type header with servlet-context-defined content-types (application's
	 * web.xml or servlet container's configuration), and fall back to system or JVM-defined
	 * (FileNameMap) content types.
	 * 
	 * @param requestCycle
	 * @param uri
	 *            Resource name to be analyzed to detect MIME type
	 * 
	 * @see ServletContext#getMimeType(String)
	 * @see URLConnection#getFileNameMap()
	 */
	public void detectContentType(RequestCycle requestCycle, String uri)
	{
		// Configure response with content type of resource
		final ServletContext context = ((WebApplication)requestCycle.getApplication()).getServletContext();
		// First look for user defined content-type in web.xml
		String contentType = context.getMimeType(uri);

		// If not found, fall back to
		// FileResourceStream.getContentType() that looks into
		// system or JVM content types
		if (contentType == null)
		{
			contentType = URLConnection.getFileNameMap().getContentTypeFor(uri);
		}

		if (contentType != null)
		{
			// only set the charset when the contentType is the text type
			if (contentType.toLowerCase().indexOf("text") != -1)
			{
				setContentType(contentType + "; charset=" + getCharacterEncoding());
			}
			else
			{
				setContentType(contentType);
			}
		}
	}
}
