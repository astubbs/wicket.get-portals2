/*
 * $Id$
 * $Revision$ $Date$
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
package wicket;

/**
 * Generic runtime exception subclass thrown by Wicket.
 * 
 * @author Jonathan Locke
 */
public class WicketRuntimeException extends RuntimeException
{
	private static final long serialVersionUID = 1L;

	/**
	 * @see RuntimeException#RuntimeException()
	 */
	public WicketRuntimeException()
	{
		super();
	}

	/**
	 * @see RuntimeException#RuntimeException(java.lang.String)
	 */
	public WicketRuntimeException(final String message)
	{
		super(message);
	}

	/**
	 * @see RuntimeException#RuntimeException(java.lang.String,
	 *      java.lang.Throwable)
	 */
	public WicketRuntimeException(final String message, final Throwable cause)
	{
		super(message, cause);
	}

	/**
	 * @see RuntimeException#RuntimeException(java.lang.Throwable)
	 */
	public WicketRuntimeException(final Throwable cause)
	{
		super(cause);
	}
}
