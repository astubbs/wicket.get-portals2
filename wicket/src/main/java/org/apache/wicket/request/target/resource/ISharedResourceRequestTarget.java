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
package org.apache.wicket.request.target.resource;

import org.apache.wicket.IRequestTarget;
import org.apache.wicket.request.RequestParameters;

/**
 * Target that denotes a shared {@link org.apache.wicket.Resource}.
 * 
 * @author Eelco Hillenius
 */
public interface ISharedResourceRequestTarget extends IRequestTarget
{
	/**
	 * Gets the request parameters.
	 * 
	 * @return the request parameters
	 */
	RequestParameters getRequestParameters();

	/**
	 * @return The resource key for this SharedResource
	 */
	String getResourceKey();
}