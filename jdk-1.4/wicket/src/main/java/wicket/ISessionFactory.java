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
package wicket;

/**
 * A factory interface used by Applications to create Sessions.
 * 
 * @author Jonathan Locke
 */
public interface ISessionFactory
{

	/**
	 * Creates a new session
	 * 
	 * @param request
	 *            The request that will create this session.
	 * @param response
	 *            The response to initialize, for example with cookies. This is
	 *            important to use cases involving unit testing because those
	 *            use cases might want to be able to sign a user in
	 *            automatically when the session is created.
	 * 
	 * @return The session
	 * 
	 * @since 1.3
	 */
	Session newSession(Request request, Response response);
}
