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
package org.apache.wicket.extensions.ajax.markup.html.form.upload;

import javax.servlet.http.HttpServletRequest;

import org.apache.wicket.RequestCycle;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.protocol.http.WebRequest;
import org.apache.wicket.protocol.http.servlet.MultipartServletWebRequest;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.util.lang.Bytes;
import org.apache.wicket.util.upload.FileUploadException;


/**
 * A request object that stores information about the current upload in session so it is accessible
 * to the {@link UploadProgressBar}.
 * <p>
 * This request object is necessary for the UploadProgressBar to function properly. It is installed
 * like so:
 * 
 * <code>
 * class MyApplication extends WebApplication {
 * ...
 *     @Override
 *     protected WebRequest newWebRequest(HttpServletRequest servletRequest) {
 *         return new UploadWebRequest(servletRequest);
 *     }
 * ...
 * }
 * </code>
 * 
 * @author Igor Vaynberg (ivaynberg)
 */
public class UploadWebRequest extends ServletWebRequest
{


	final HttpServletRequest req;

	/**
	 * Constructor
	 * 
	 * @param req
	 */
	public UploadWebRequest(final HttpServletRequest req)
	{
		super(req);
		this.req = req;
	}

	/**
	 * @see org.apache.wicket.protocol.http.WebRequest#newMultipartWebRequest(org.apache.wicket.util.lang.Bytes)
	 */
	public WebRequest newMultipartWebRequest(Bytes maxsize)
	{
		try
		{
			return new MultipartRequest(req, maxsize);
		}
		catch (FileUploadException e)
		{
			throw new WicketRuntimeException(e);
		}
	}

	/**
	 * Multipart request object that feeds the upload info into session
	 * 
	 * @author Igor Vaynberg (ivaynberg)
	 * 
	 */
	private static class MultipartRequest extends MultipartServletWebRequest
	{
		/**
		 * @param req
		 * @param maxSize
		 * @throws FileUploadException
		 */
		public MultipartRequest(HttpServletRequest req, Bytes maxSize) throws FileUploadException
		{
			super(req, maxSize);
			if (req == null)
			{
				throw new IllegalStateException("req cannot be null");
			}
		}

		/**
		 * @see org.apache.wicket.protocol.http.servlet.MultipartServletWebRequest#wantUploadProgressUpdates()
		 */
		protected boolean wantUploadProgressUpdates()
		{
			return true;
		}

		/**
		 * @see org.apache.wicket.protocol.http.servlet.MultipartServletWebRequest#onUploadStarted(int)
		 */
		protected void onUploadStarted(int totalBytes)
		{
			UploadInfo info = new UploadInfo(totalBytes);

			HttpServletRequest request = ((WebRequest)RequestCycle.get().getRequest()).getHttpServletRequest();
			setUploadInfo(request, info);
		}

		/**
		 * @see org.apache.wicket.protocol.http.servlet.MultipartServletWebRequest#onUploadUpdate(int,
		 *      int)
		 */
		protected void onUploadUpdate(int bytesUploaded, int total)
		{
			HttpServletRequest request = ((WebRequest)RequestCycle.get().getRequest()).getHttpServletRequest();
			UploadInfo info = getUploadInfo(request);
			if (info == null)
			{
				throw new IllegalStateException(
					"could not find UploadInfo object in session which should have been set when uploaded started");
			}
			info.setBytesUploaded(bytesUploaded);

			setUploadInfo(request, info);
		}

		/**
		 * @see org.apache.wicket.protocol.http.servlet.MultipartServletWebRequest#onUploadCompleted()
		 */
		protected void onUploadCompleted()
		{
			HttpServletRequest request = ((WebRequest)RequestCycle.get().getRequest()).getHttpServletRequest();
			clearUploadInfo(request);
		}
	}

	private static final String SESSION_KEY = UploadWebRequest.class.getName();


	/**
	 * Retrieves {@link UploadInfo} from session, null if not found
	 * 
	 * @param req
	 * @return {@link UploadInfo} object from session, or null if not found
	 */
	static UploadInfo getUploadInfo(HttpServletRequest req)
	{
		if (req == null)
		{
			throw new IllegalArgumentException("req cannot be null");
		}
		return (UploadInfo)req.getSession().getAttribute(SESSION_KEY);
	}

	/**
	 * Sets the {@link UploadInfo} object into session
	 * 
	 * @param req
	 * @param uploadInfo
	 */
	static void setUploadInfo(HttpServletRequest req, UploadInfo uploadInfo)
	{
		if (req == null)
		{
			throw new IllegalArgumentException("req cannot be null");
		}
		if (uploadInfo == null)
		{
			throw new IllegalArgumentException("uploadInfo cannot be null");
		}
		req.getSession().setAttribute(SESSION_KEY, uploadInfo);
	}

	/**
	 * Clears the {@link UploadInfo} object from session if one exists
	 * 
	 * @param req
	 */
	static void clearUploadInfo(HttpServletRequest req)
	{
		if (req == null)
		{
			throw new IllegalArgumentException("req cannot be null");
		}
		req.getSession().removeAttribute(SESSION_KEY);
	}

}