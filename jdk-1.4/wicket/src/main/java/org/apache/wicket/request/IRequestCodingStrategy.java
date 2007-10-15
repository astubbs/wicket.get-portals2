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
package org.apache.wicket.request;

import org.apache.wicket.IRequestTarget;
import org.apache.wicket.Request;
import org.apache.wicket.RequestCycle;

/**
 * Implementations of this interface are responsible for digesting the incoming request and creating
 * a suitable {@link org.apache.wicket.request.RequestParameters} object for it, as well as creating
 * url representations for request targets.
 * 
 * @author Eelco Hillenius
 */
public interface IRequestCodingStrategy extends IRequestTargetMounter
{
	/**
	 * Analyze the request and create a corresponding request parameters object for it.
	 * 
	 * @param request
	 *            the incoming request
	 * @return a request parameters object that corresponds to the request
	 */
	RequestParameters decode(Request request);

	/**
	 * <p>
	 * Gets the url that will point to the provided request target.
	 * </p>
	 * <p>
	 * If an implementation supports mounting, it should return the mounted path for the provided
	 * request target if any.
	 * </p>
	 * 
	 * @param requestCycle
	 *            the current request cycle (for efficient access)
	 * 
	 * @param requestTarget
	 *            the request target
	 * 
	 * @return the url to the provided target
	 */
	CharSequence encode(RequestCycle requestCycle, IRequestTarget requestTarget);
}
