/*
 * $Id$ $Revision:
 * 1.5 $ $Date$
 * 
 * ==============================================================================
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package wicket.util.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;

/**
 * Utilities methods for working with input and output streams.
 * 
 * @author Jonathan Locke
 */
public final class Streams
{
	/**
	 * Private to prevent instantiation.
	 */
	private Streams()
	{
	}

	/**
	 * Reads a stream as a string.
	 * 
	 * @param in
	 *            The input stream
	 * @return The string
	 * @throws IOException
	 */
	public static String readString(final InputStream in) throws IOException
	{
		return readString(new BufferedReader(new InputStreamReader(in)));
	}

	/**
	 * Reads a string using a character encoding.
	 * 
	 * @param in
	 *            The input
	 * @param encoding
	 *            The character encoding of the input data
	 * @return The string
	 * @throws IOException
	 */
	public static String readString(final InputStream in, final CharSequence encoding) throws IOException
	{
		return readString(new BufferedReader(new InputStreamReader(in, encoding.toString())));
	}

	/**
	 * Reads all input from a reader into a string.
	 * 
	 * @param in
	 *            The input
	 * @return The string
	 * @throws IOException
	 */
	public static String readString(final Reader in) throws IOException
	{
		final StringBuffer buffer = new StringBuffer(2048);
		int value;

		while ((value = in.read()) != -1)
		{
			buffer.append((char)value);
		}

		return buffer.toString();
	}

	/**
	 * Writes the input stream to the output stream. Input is done without a
	 * Reader object, meaning that the input is copied in its raw form.
	 * 
	 * @param in
	 *            The input stream
	 * @param out
	 *            The output stream
	 * @throws IOException
	 */
	public static void writeStream(final InputStream in, final OutputStream out) throws IOException
	{
		final byte[] buffer = new byte[1024];
		while (true)
		{
			final int available = in.available(); 
			if (available > 0)
			{
				final int length = Math.min(available, buffer.length);
				in.read(buffer, 0, length);
				out.write(buffer, 0, length);
			}
			else
			{
				final int c = in.read();
				if (c == -1)
				{
					break;
				}
				out.write(c);
			}
		}
	}
}
