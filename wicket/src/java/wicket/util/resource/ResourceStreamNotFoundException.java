/*
 * $Id: ResourceStreamNotFoundException.java,v 1.4 2005/01/15 19:23:55 jonathanlocke
 * Exp $ $Revision$ $Date$
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
package wicket.util.resource;

/**
 * Thrown if a required resource cannot be found.
 * 
 * @author Jonathan Locke
 */
public final class ResourceStreamNotFoundException extends Exception
{
	/**
	 * Constructor
	 */
	public ResourceStreamNotFoundException()
	{
		super();
	}

	/**
	 * Constructor
	 * 
	 * @param message
	 *            Description of the problem
	 */
	public ResourceStreamNotFoundException(final String message)
	{
		super(message);
	}

	/**
	 * Constructor
	 * 
	 * @param cause
	 *            Nested stack trace
	 */
	public ResourceStreamNotFoundException(final Throwable cause)
	{
		super(cause);
	}

	/**
	 * Constructor
	 * 
	 * @param message
	 *            Description of the problem
	 * @param cause
	 *            Nested stack trace
	 */
	public ResourceStreamNotFoundException(final String message, final Throwable cause)
	{
		super(message, cause);
	}
}
