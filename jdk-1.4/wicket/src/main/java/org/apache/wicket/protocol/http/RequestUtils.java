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

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.util.string.Strings;
import org.apache.wicket.util.value.ValueMap;

/**
 * Wicket Http specific utilities class.
 */
public final class RequestUtils
{
	/**
	 * Decode the provided queryString as a series of key/ value pairs and set them in the provided
	 * value map.
	 * 
	 * @param queryString
	 *            string to decode, uses '&' to separate parameters and '=' to separate key from
	 *            value
	 * @param params
	 *            parameters map to write the found key/ value pairs to
	 */
	public static void decodeParameters(String queryString, ValueMap params)
	{
		final String[] paramTuples = queryString.split("&");
		for (int t = 0; t < paramTuples.length; t++)
		{
			final String[] bits = paramTuples[t].split("=");
			try
			{
				if (bits.length == 2)
				{
					params.add(URLDecoder.decode(bits[0], "UTF-8"), URLDecoder.decode(bits[1],
						"UTF-8"));
				}
				else
				{
					params.add(URLDecoder.decode(bits[0], "UTF-8"), "");
				}
			}
			catch (UnsupportedEncodingException e)
			{
				// Should never happen
			}
		}
	}

	/**
	 * Remove occurrences of ".." from the path
	 * 
	 * @param path
	 * @return
	 */
	static String removeDoubleDots(String path)
	{
		List newcomponents = new ArrayList(Arrays.asList(path.split("/")));

		for (int i = 0; i < newcomponents.size(); i++)
		{
			if (i < newcomponents.size() - 1)
			{
				// Verify for a ".." component at next iteration
				if (((String)newcomponents.get(i)).length() > 0 &&
					newcomponents.get(i + 1).equals(".."))
				{
					newcomponents.remove(i);
					newcomponents.remove(i);
					i = i - 2;
					if (i < -1)
						i = -1;
				}
			}
		}
		String newpath = Strings.join("/", (String[])newcomponents.toArray(new String[0]));
		if (path.endsWith("/"))
			return newpath + "/";
		return newpath;
	}

	/**
	 * Hidden utility class constructor.
	 */
	private RequestUtils()
	{
	}

	/**
	 * Does a URLDecoder.decode() in UTF-8
	 * 
	 * @param path
	 * @return
	 */
	public static String decode(String path)
	{
		try
		{
			return URLDecoder.decode(path, "UTF-8");
		}
		catch (UnsupportedEncodingException e)
		{
			throw new WicketRuntimeException(e);
		}
	}
}
