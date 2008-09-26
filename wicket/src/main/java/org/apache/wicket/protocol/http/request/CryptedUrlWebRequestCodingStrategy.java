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
package org.apache.wicket.protocol.http.request;

import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.wicket.Application;
import org.apache.wicket.IRequestTarget;
import org.apache.wicket.Request;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.protocol.http.RequestUtils;
import org.apache.wicket.protocol.http.WicketURLDecoder;
import org.apache.wicket.protocol.http.WicketURLEncoder;
import org.apache.wicket.request.IRequestCodingStrategy;
import org.apache.wicket.request.RequestParameters;
import org.apache.wicket.request.target.coding.IRequestTargetUrlCodingStrategy;
import org.apache.wicket.util.crypt.ICrypt;
import org.apache.wicket.util.string.AppendingStringBuffer;
import org.apache.wicket.util.string.Strings;
import org.apache.wicket.util.string.UrlUtils;
import org.apache.wicket.util.value.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This is a request coding strategy which encrypts the URL and hence makes it impossible for users
 * to guess what is in the url and rebuild it manually. It uses the CryptFactory registered with the
 * application to encode and decode the URL. Hence, the coding algorithm must be a two-way one
 * (reversible). Because the algorithm is reversible, URLs which were bookmarkable before will
 * remain bookmarkable.
 * <p>
 * To register the request coding strategy to need to do the following:
 * 
 * <pre>
 * protected IRequestCycleProcessor newRequestCycleProcessor()
 * {
 * 	return new WebRequestCycleProcessor()
 * 	{
 * 		protected IRequestCodingStrategy newRequestCodingStrategy()
 * 		{
 * 			return new CryptedUrlWebRequestCodingStrategy(new WebRequestCodingStrategy());
 * 		}
 * 	};
 * }
 * </pre>
 * 
 * <b>Note:</b> When trying to hack urls in the browser an exception might be caught while decoding
 * the URL. By default, for safety reasons a very simple WicketRuntimeException is thrown. The
 * original stack trace is only logged.
 * 
 * @author Juergen Donnerstag
 */
public class CryptedUrlWebRequestCodingStrategy implements IRequestCodingStrategy
{
	/** log. */
	private static final Logger log = LoggerFactory.getLogger(CryptedUrlWebRequestCodingStrategy.class);

	/** The default request coding strategy most of the methods are delegated to */
	private final IRequestCodingStrategy defaultStrategy;

	/**
	 * Construct.
	 * 
	 * @param defaultStrategy
	 *            The default strategy most requests are forwarded to
	 */
	public CryptedUrlWebRequestCodingStrategy(final IRequestCodingStrategy defaultStrategy)
	{
		this.defaultStrategy = defaultStrategy;
	}

	/**
	 * Decode the querystring of the URL
	 * 
	 * @see org.apache.wicket.request.IRequestCodingStrategy#decode(org.apache.wicket.Request)
	 */
	public RequestParameters decode(final Request request)
	{
		String url = request.decodeURL(request.getURL());
		String decodedQueryParams = decodeURL(url);
		if (decodedQueryParams != null)
		{
			// The difficulty now is that this.defaultStrategy.decode(request)
			// doesn't know the just decoded url which is why must create
			// a fake Request for.
			Request fakeRequest = new DecodedUrlRequest(request, url, decodedQueryParams);
			return defaultStrategy.decode(fakeRequest);
		}

		return defaultStrategy.decode(request);
	}

	/**
	 * Encode the querystring of the URL
	 * 
	 * @see org.apache.wicket.request.IRequestCodingStrategy#encode(org.apache.wicket.RequestCycle,
	 *      org.apache.wicket.IRequestTarget)
	 */
	public CharSequence encode(final RequestCycle requestCycle, final IRequestTarget requestTarget)
	{
		CharSequence url = defaultStrategy.encode(requestCycle, requestTarget);
		url = encodeURL(url);
		return url;
	}

	/**
	 * @see org.apache.wicket.request.IRequestTargetMounter#mount(org.apache.wicket.request.target.coding.IRequestTargetUrlCodingStrategy)
	 */
	public void mount(IRequestTargetUrlCodingStrategy urlCodingStrategy)
	{
		defaultStrategy.mount(urlCodingStrategy);
	}

	/**
	 * @see org.apache.wicket.request.IRequestTargetMounter#unmount(java.lang.String)
	 */
	public void unmount(String path)
	{
		defaultStrategy.unmount(path);
	}

	public void addIgnoreMountPath(String path)
	{
		defaultStrategy.addIgnoreMountPath(path);
	}

	/**
	 * @see org.apache.wicket.request.IRequestTargetMounter#urlCodingStrategyForPath(java.lang.String)
	 */
	public IRequestTargetUrlCodingStrategy urlCodingStrategyForPath(String path)
	{
		return defaultStrategy.urlCodingStrategyForPath(path);
	}

	/**
	 * @see org.apache.wicket.request.IRequestTargetMounter#pathForTarget(org.apache.wicket.IRequestTarget)
	 */
	public CharSequence pathForTarget(IRequestTarget requestTarget)
	{
		return defaultStrategy.pathForTarget(requestTarget);
	}

	/**
	 * @see org.apache.wicket.request.IRequestTargetMounter#targetForRequest(org.apache.wicket.request.RequestParameters)
	 */
	public IRequestTarget targetForRequest(RequestParameters requestParameters)
	{
		return defaultStrategy.targetForRequest(requestParameters);
	}

	/**
	 * Returns the given url encoded.
	 * 
	 * @param url
	 *            The URL to encode
	 * @return The encoded url
	 */
	protected CharSequence encodeURL(final CharSequence url)
	{
		// Get the crypt implementation from the application
		ICrypt urlCrypt = Application.get().getSecuritySettings().getCryptFactory().newCrypt();
		if (urlCrypt != null)
		{
			// The url must have a query string, otherwise keep the url
			// unchanged
			final int pos = url.toString().indexOf('?');
			if (pos > -1)
			{
				// The url's path
				CharSequence urlPrefix = url.subSequence(0, pos);

				// Extract the querystring
				String queryString = url.subSequence(pos + 1, url.length()).toString();

				// if the querystring starts with a parameter like
				// "x=", than don't change the querystring as it
				// has been encoded already
				if (!queryString.startsWith("x="))
				{
					// The length of the encrypted string depends on the
					// length of the original querystring. Let's try to
					// make the querystring shorter first without loosing
					// information.
					queryString = shortenUrl(queryString).toString();

					// encrypt the query string
					String encryptedQueryString = urlCrypt.encryptUrlSafe(queryString);

					encryptedQueryString = WicketURLEncoder.QUERY_INSTANCE.encode(encryptedQueryString);

					// build the new complete url
					return new AppendingStringBuffer(urlPrefix).append("?x=").append(
						encryptedQueryString);
				}
			}
		}

		// we didn't change anything
		return url;
	}

	/**
	 * Decode the "x" parameter of the querystring
	 * 
	 * @param url
	 *            The encoded URL
	 * @return The decoded 'x' parameter of the querystring
	 */
	protected String decodeURL(final String url)
	{
		int startIndex = url.indexOf("?x=");
		if (startIndex != -1)
		{
			try
			{
				startIndex = startIndex + 3;
				final int endIndex = url.indexOf("&", startIndex);
				String secureParam;
				if (endIndex == -1)
				{
					secureParam = url.substring(startIndex);
				}
				else
				{
					secureParam = url.substring(startIndex, endIndex);
				}

				secureParam = WicketURLDecoder.QUERY_INSTANCE.decode(secureParam);

				// Get the crypt implementation from the application
				final ICrypt urlCrypt = Application.get()
					.getSecuritySettings()
					.getCryptFactory()
					.newCrypt();

				// Decrypt the query string
				String queryString = urlCrypt.decryptUrlSafe(secureParam);

				// The querystring might have been shortened (length reduced).
				// In that case, lengthen the query string again.
				queryString = rebuildUrl(queryString);
				return queryString;
			}
			catch (Exception ex)
			{
				return onError(ex);
			}
		}
		return null;
	}

	/**
	 * @param ex
	 * 
	 * @return decoded URL
	 */
	protected String onError(final Exception ex)
	{
		log.error("Invalid URL", ex);

		throw new HackAttackException("Invalid URL");
	}

	/**
	 * Try to shorten the querystring without loosing information. Note: WebRequestWithCryptedUrl
	 * must implement exactly the opposite logic.
	 * 
	 * @param queryString
	 *            The original query string
	 * @return The shortened querystring
	 */
	protected CharSequence shortenUrl(CharSequence queryString)
	{
		queryString = Strings.replaceAll(queryString,
			WebRequestCodingStrategy.BEHAVIOR_ID_PARAMETER_NAME + "=", "1*");
		queryString = Strings.replaceAll(queryString,
			WebRequestCodingStrategy.INTERFACE_PARAMETER_NAME + "=IRedirectListener", "2*");
		queryString = Strings.replaceAll(queryString,
			WebRequestCodingStrategy.INTERFACE_PARAMETER_NAME + "=IFormSubmitListener", "3*");
		queryString = Strings.replaceAll(queryString,
			WebRequestCodingStrategy.INTERFACE_PARAMETER_NAME + "=IOnChangeListener", "4*");
		queryString = Strings.replaceAll(queryString,
			WebRequestCodingStrategy.INTERFACE_PARAMETER_NAME + "=ILinkListener", "5*");
		queryString = Strings.replaceAll(queryString,
			WebRequestCodingStrategy.INTERFACE_PARAMETER_NAME + "=", "6*");
		queryString = Strings.replaceAll(queryString,
			WebRequestCodingStrategy.BOOKMARKABLE_PAGE_PARAMETER_NAME + "=", "7*");

		// For debugging only: determine possibilities to further shorten
		// the query string
		if (log.isDebugEnabled())
		{
			// Every word with at least 3 letters
			Pattern words = Pattern.compile("\\w\\w\\w+");
			Matcher matcher = words.matcher(queryString);
			while (matcher.find())
			{
				CharSequence word = queryString.subSequence(matcher.start(), matcher.end());
				log.debug("URL pattern NOT shortened: '" + word + "' - '" + queryString + "'");
			}
		}

		return queryString;
	}

	/**
	 * In case the query string has been shortened prior to encryption, than rebuild (lengthen) the
	 * query string now. Note: This implementation must exactly match the reverse one implemented in
	 * WebResponseWithCryptedUrl.
	 * 
	 * @param queryString
	 *            The URL's query string
	 * @return The lengthened query string
	 */
	protected String rebuildUrl(CharSequence queryString)
	{
		queryString = Strings.replaceAll(queryString, "1*",
			WebRequestCodingStrategy.BEHAVIOR_ID_PARAMETER_NAME + "=");
		queryString = Strings.replaceAll(queryString, "2*",
			WebRequestCodingStrategy.INTERFACE_PARAMETER_NAME + "=IRedirectListener");
		queryString = Strings.replaceAll(queryString, "3*",
			WebRequestCodingStrategy.INTERFACE_PARAMETER_NAME + "=IFormSubmitListener");
		queryString = Strings.replaceAll(queryString, "4*",
			WebRequestCodingStrategy.INTERFACE_PARAMETER_NAME + "=IOnChangeListener");
		queryString = Strings.replaceAll(queryString, "5*",
			WebRequestCodingStrategy.INTERFACE_PARAMETER_NAME + "=ILinkListener");
		queryString = Strings.replaceAll(queryString, "6*",
			WebRequestCodingStrategy.INTERFACE_PARAMETER_NAME + "=");
		queryString = Strings.replaceAll(queryString, "7*",
			WebRequestCodingStrategy.BOOKMARKABLE_PAGE_PARAMETER_NAME + "=");

		return queryString.toString();
	}

	/**
	 * IRequestCodingStrategy.decode(Request) requires a Request parameter and not a URL. Hence,
	 * based on the original URL and the decoded 'x' parameter a new Request object must be created
	 * to serve the default coding strategy as input for analyzing the URL.
	 */
	private static class DecodedUrlRequest extends Request
	{
		/** The original request */
		private final Request request;

		/** The new URL with the 'x' param decoded */
		private final String url;

		/**
		 * The new parameter map with the 'x' param removed and the 'new' one included
		 */
		private final Map parameterMap;

		/**
		 * Construct.
		 * 
		 * @param request
		 * @param url
		 * @param encodedParamReplacement
		 */
		public DecodedUrlRequest(final Request request, final String url,
			final String encodedParamReplacement)
		{
			this.request = request;

			// Create a copy of the original parameter map
			parameterMap = this.request.getParameterMap();

			// Remove the 'x' parameter which contains ALL the encoded params
			parameterMap.remove("x");
			String decodedParamReplacement = encodedParamReplacement;
			decodedParamReplacement = WicketURLDecoder.QUERY_INSTANCE.decode(encodedParamReplacement);

			// Add ALL of the params from the decoded 'x' param
			ValueMap params = new ValueMap();
			RequestUtils.decodeParameters(decodedParamReplacement, params);
			parameterMap.putAll(params);

			// Rebuild the URL with the 'x' param removed
			int pos1 = url.indexOf("?x=");
			if (pos1 == -1)
			{
				throw new WicketRuntimeException("Programming error: we should come here");
			}
			int pos2 = url.indexOf("&");

			AppendingStringBuffer urlBuf = new AppendingStringBuffer(url.length() +
				encodedParamReplacement.length());
			urlBuf.append(url.subSequence(0, pos1 + 1));
			urlBuf.append(encodedParamReplacement);
			if (pos2 != -1)
			{
				urlBuf.append(url.substring(pos2));
			}
			this.url = urlBuf.toString();
		}

		/**
		 * Delegate to the original request
		 * 
		 * @see org.apache.wicket.Request#getLocale()
		 */
		@Override
		public Locale getLocale()
		{
			return request.getLocale();
		}

		/**
		 * @see org.apache.wicket.Request#getParameter(java.lang.String)
		 */
		@Override
		public String getParameter(final String key)
		{
			if (key == null)
			{
				return null;
			}

			Object val = parameterMap.get(key);
			if (val == null)
			{
				return null;
			}
			else if (val instanceof String[])
			{
				String[] arrayVal = (String[])val;
				return arrayVal.length > 0 ? arrayVal[0] : null;
			}
			else if (val instanceof String)
			{
				return (String)val;
			}
			else
			{
				// never happens, just being defensive
				return val.toString();
			}
		}

		/**
		 * @see org.apache.wicket.Request#getParameterMap()
		 */
		@Override
		public Map getParameterMap()
		{
			return parameterMap;
		}

		/**
		 * @see org.apache.wicket.Request#getParameters(java.lang.String)
		 */
		@Override
		public String[] getParameters(final String key)
		{
			if (key == null)
			{
				return null;
			}

			Object val = parameterMap.get(key);
			if (val == null)
			{
				return null;
			}
			else if (val instanceof String[])
			{
				return (String[])val;
			}
			else if (val instanceof String)
			{
				return new String[] { (String)val };
			}
			else
			{
				// never happens, just being defensive
				return new String[] { val.toString() };
			}
		}

		/**
		 * @see org.apache.wicket.Request#getPath()
		 */
		@Override
		public String getPath()
		{
			// Hasn't changed. We only encoded the querystring
			return request.getPath();
		}

		@Override
		public String getRelativePathPrefixToContextRoot()
		{
			return request.getRelativePathPrefixToContextRoot();
		}

		@Override
		public String getRelativePathPrefixToWicketHandler()
		{
			return request.getRelativePathPrefixToWicketHandler();
		}

		/**
		 * @see org.apache.wicket.Request#getURL()
		 */
		@Override
		public String getURL()
		{
			return url;
		}

		@Override
		public String getQueryString()
		{
			return request.getQueryString();
		}
	}

	/**
	 * 
	 */
	public class HackAttackException extends WicketRuntimeException
	{
		private static final long serialVersionUID = 1L;

		/**
		 * Construct.
		 * 
		 * @param msg
		 */
		public HackAttackException(final String msg)
		{
			super(msg);
		}

		/**
		 * No stack trace. We won't tell the hackers about the internals of wicket
		 * 
		 * @see java.lang.Throwable#getStackTrace()
		 */
		@Override
		public StackTraceElement[] getStackTrace()
		{
			return new StackTraceElement[0];
		}

		/**
		 * No additional information. We won't tell the hackers about the internals of wicket
		 * 
		 * @see java.lang.Throwable#toString()
		 */
		@Override
		public String toString()
		{
			return getMessage();
		}
	}

	/** {@inheritDoc} */
	public String rewriteStaticRelativeUrl(String string)
	{
		return UrlUtils.rewriteToContextRelative(string, RequestCycle.get().getRequest());
	}
}
