/*
 * $Id: IntegerValidator.java 5791 2006-05-20 00:32:57 +0000 (Sat, 20 May 2006)
 * joco01 $ $Revision$ $Date: 2006-05-20 00:32:57 +0000 (Sat, 20 May
 * 2006) $
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
package wicket.markup.html.form.validation;

import java.io.Serializable;
import java.util.Map;

import wicket.markup.html.form.FormComponent;
import wicket.util.string.Strings;

/**
 * Ensures that the form component has a numeric value in a given range. The
 * range static factory method constructs a IntegerValidator with minimum and
 * maximum values specified as Java longs. Convenience fields exist for INT,
 * POSITIVE_INT, LONG and POSITIVE_LONG which match the appropriate ranges of
 * numbers.
 * 
 * @author Jonathan Locke
 * @deprecated
 * @see {@link NumberValidator}
 */
@Deprecated
public class IntegerValidator extends StringValidator
{
	private static final long serialVersionUID = 1L;

	/**
	 * Validator that ensures int value.
	 */
	public static final IntegerValidator INT = new IntegerValidator(Integer.MIN_VALUE,
			Integer.MAX_VALUE);

	/**
	 * Validator that ensures long value.
	 */
	public static final IntegerValidator LONG = new IntegerValidator(Long.MIN_VALUE, Long.MAX_VALUE);

	/**
	 * Validator that ensures positive int value.
	 */
	public static final IntegerValidator POSITIVE_INT = new IntegerValidator(0, Integer.MAX_VALUE);

	/**
	 * Validator that ensures positive long value.
	 */
	public static final IntegerValidator POSITIVE_LONG = new IntegerValidator(0, Long.MAX_VALUE);

	/** Upper bound on valid decimal number. */
	private final long max;

	/** Lower bound on valid decimal number. */
	private final long min;

	/**
	 * Gets a decimal validator with a given range.
	 * 
	 * @param min
	 *            Lower bound on valid decimal number
	 * @param max
	 *            Upper bound on valid decimal number
	 * @return Validator object
	 */
	public final static IntegerValidator range(final long min, final long max)
	{
		return new IntegerValidator(min, max);
	}

	/**
	 * Protected constructor forces use of static factory method and static
	 * instances. Or override it to implement resourceKey(Component)
	 * 
	 * @param min
	 *            Lower bound on valid decimal number
	 * @param max
	 *            Upper bound on valid decimal number
	 */
	protected IntegerValidator(final long min, final long max)
	{
		this.min = min;
		this.max = max;
	}

	/**
	 * Gets the upper bound on valid length.
	 * 
	 * @return the upper bound on valid length
	 */
	public final long getMax()
	{
		return max;
	}

	/**
	 * Gets the lower bound on valid length.
	 * 
	 * @return the lower bound on valid length
	 */
	public final long getMin()
	{
		return min;
	}


	/**
	 * Validates the given form component. Ensures that the form component has a
	 * numeric value. If min and max arguments are given, this validator also
	 * ensures the value is in bounds.
	 * 
	 * @see wicket.markup.html.form.validation.StringValidator#onValidate(wicket.markup.html.form.FormComponent,
	 *      java.lang.String)
	 */
	@Override
	public final void onValidate(FormComponent formComponent, String value)
	{
		// If value is non-empty
		if (!Strings.isEmpty(value))
		{
			try
			{
				// Get long value
				final long longValue = Long.parseLong(value);

				// Check range
				if (longValue < min || longValue > max)
				{
					error(formComponent);
				}
			}
			catch (NumberFormatException e)
			{
				error(formComponent);
			}
		}
	}

	/**
	 * @see wicket.markup.html.form.validation.AbstractValidator#messageModel(wicket.markup.html.form.FormComponent)
	 */
	@Override
	protected Map<String, Serializable> messageModel(FormComponent formComponent)
	{
		final Map<String, Serializable> map = super.messageModel(formComponent);
		map.put("min", new Long(min));
		map.put("max", new Long(max));
		return map;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "[IntegerValidator min = " + min + ", max = " + max + "]";
	}
}