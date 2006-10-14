/*
 * $Id: org.eclipse.jdt.ui.prefs 5004 2006-03-17 20:47:08 -0800 (Fri, 17 Mar 2006) eelco12 $
 * $Revision: 5004 $
 * $Date: 2006-03-17 20:47:08 -0800 (Fri, 17 Mar 2006) $
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
package wicket.markup.html.form;

import java.io.Serializable;

import wicket.Component;
import wicket.validation.IMessageSource;
import wicket.validation.IValidationError;

/**
 * This class is the parameter to {@link Component#error(Serializable)} instead
 * of the generated error string itself (when
 * {@link FormComponent#error(IValidationError)} is called). The advantage is
 * that a custom feedback panel would still have access to the underlying
 * {@link IValidationError} that generated the error message - providing much
 * more context.
 * 
 * @author Igor Vaynberg (ivaynberg)
 */
public class ValidationErrorFeedback implements Serializable
{
	private static final long serialVersionUID = 1L;

	/** error object */
	private final IValidationError error;

	/** error message */
	private final String message;

	/**
	 * Construct.
	 * 
	 * @param error
	 * @param message
	 */
	public ValidationErrorFeedback(final IValidationError error, final String message)
	{
		if (error == null)
		{
			throw new IllegalArgumentException("Argument [[error]] cannot be null");
		}
		this.error = error;
		this.message = message;
	}

	/**
	 * Gets serialVersionUID.
	 * 
	 * @return serialVersionUID
	 */
	public static long getSerialVersionUID()
	{
		return serialVersionUID;
	}

	/**
	 * Gets error.
	 * 
	 * @return error
	 */
	public IValidationError getError()
	{
		return error;
	}

	/**
	 * Gets message.
	 * 
	 * @return message
	 */
	public String getMessage()
	{
		return message;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return message;
	}


}