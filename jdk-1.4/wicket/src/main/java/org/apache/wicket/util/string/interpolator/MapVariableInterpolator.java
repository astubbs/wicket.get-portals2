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
package org.apache.wicket.util.string.interpolator;

import java.util.Map;

import org.apache.wicket.util.string.Strings;


/**
 * Interpolates variables into a string from a Map.
 * 
 * @author Jonathan Locke
 */
public class MapVariableInterpolator extends VariableInterpolator
{
	/** Map of variables */
	private Map variables;

	/**
	 * Constructor
	 * 
	 * @param string
	 *            The string to interpolate into
	 * @param variables
	 *            The variables to substitute
	 */
	public MapVariableInterpolator(final String string, final Map variables)
	{
		super(string);
		this.variables = variables;
	}

	/**
	 * Constructor
	 * 
	 * @param string
	 *            The string to interpolate into
	 * @param variables
	 *            The variables to substitute
	 * @param exceptionOnNullVarValue
	 *            if true an {@link IllegalStateException} will be thrown if a
	 *            {@link #getValue(String)} returns null, otherwise the
	 *            ${varname} string will be left in the <code>string</code> so
	 *            multiple interpolators can be chained
	 */
	public MapVariableInterpolator(String string, final Map variables,
			boolean exceptionOnNullVarValue)
	{
		super(string, exceptionOnNullVarValue);
		this.variables = variables;
	}

	/**
	 * Accessor to set variables
	 * 
	 * @param variables
	 *            New value
	 */
	public final void setVariables(final Map variables)
	{
		this.variables = variables;
	}

	/**
	 * Gets a value for a variable name during interpolation
	 * 
	 * @param variableName
	 *            The variable
	 * @return The value
	 */
	protected final String getValue(final String variableName)
	{
		return Strings.toString(variables.get(variableName));
	}

	/**
	 * Interpolate the string with the arguments defined in a map
	 * 
	 * @param string
	 * @param variables
	 * @return interpolated string
	 */
	public static String interpolate(String string, Map variables)
	{
		return new MapVariableInterpolator(string, variables).toString();
	}

}
