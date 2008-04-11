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
package org.apache.wicket.examples.frames;

import org.apache.wicket.IClusterable;

/**
 * Simple struct for holding the class of the right frame.
 * 
 * @author Eelco Hillenius
 */
public final class FrameTarget implements IClusterable
{
	private static final long serialVersionUID = 1L;

	/** the class of the bookmarkable page. */
	private Class frameClass;

	/**
	 * Construct.
	 */
	public FrameTarget()
	{
	}

	/**
	 * Construct.
	 * 
	 * @param frameClass
	 */
	public FrameTarget(Class frameClass)
	{
		this.frameClass = frameClass;
	}

	/**
	 * Gets frame class.
	 * 
	 * @return lefFrameClass
	 */
	public Class getFrameClass()
	{
		return frameClass;
	}

	/**
	 * Sets frame class.
	 * 
	 * @param frameClass
	 *            lefFrameClass
	 */
	public void setFrameClass(Class frameClass)
	{
		this.frameClass = frameClass;
	}
}