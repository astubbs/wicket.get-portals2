/*
 * $Id$
 * $Revision$
 * $Date$
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package wicket.response;

import java.io.OutputStream;
import java.io.StringWriter;

import wicket.Response;

/**
 * Response object that writes to a StringWriter.  If the StringResponse 
 * is later converted to a String via toString(), the output which 
 * was written to the StringResponse will be returned as a String.
 * 
 * @author Jonathan Locke
 */
public final class StringResponse extends Response
{
    /** StringWriter to write to */
    private final StringWriter out;

    /**
     * Constructor
     */
    public StringResponse()
    {
        this.out = new StringWriter();
    }

    /**
     * @see wicket.Response#write(java.lang.String)
     */
    public void write(final String string)
    {
        out.write(string);
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return out.toString();
    }

	/**
	 * @see wicket.Response#getOutputStream()
	 */
	public OutputStream getOutputStream()
	{
		throw new UnsupportedOperationException("Cannot get output stream on StringResponse");
	}
}
