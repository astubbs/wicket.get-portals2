/*
 * $Id$ $Revision:
 * 1.1 $ $Date$
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
package wicket.request;

import wicket.IRequestTarget;
import wicket.Request;
import wicket.RequestCycle;

/**
 * Implementations of this interface are responsible for digesting the incomming
 * request and create a suitable {@link wicket.request.RequestParameters} object
 * for it, and creating url representations for request targets.
 * 
 * @author Eelco Hillenius
 */
// TODO just returning a string with encode is probably not enough in the long
// term if we ever want to support things like client state saving.
public interface IRequestEncoder
{
	/**
	 * Analyze the request and create a corresponding request parameters object
	 * for it.
	 * 
	 * @param request
	 *            the incomming request
	 * @return a request parameters object that corresponds to the request
	 */
	RequestParameters decode(Request request);

	/**
	 * <p>
	 * Gets the url that will point to the provided request target.
	 * </p>
	 * <p>
	 * If an implementation supports mounting, it should return the mounted path
	 * for the provided request target if any.
	 * </p>
	 * 
	 * @param requestCycle
	 *            the current request cycle (for efficient access)
	 * 
	 * @param requestTarget
	 *            the request target
	 * @return the url to the provided target
	 */
	String encode(RequestCycle requestCycle, IRequestTarget requestTarget);

	/**
	 * Mounts a request target with the given path.
	 * 
	 * @param path
	 *            the path to mount the request target with
	 * @param requestTarget
	 *            the request target
	 */
	void mountPath(String path, IRequestTarget requestTarget);

	/**
	 * Unmounts a request target.
	 * 
	 * @param path
	 *            the path to unmount
	 */
	void unmountPath(String path);

	/**
	 * Gets the request target that was registered with the given path.
	 * 
	 * @param path
	 *            the path
	 * @return the request target or null if nothing was mounted with the given
	 *         path
	 */
	IRequestTarget targetForPath(String path);

	/**
	 * Gets the path that the provided request target was registered with.
	 * 
	 * @param requestTarget
	 *            the request target
	 * @return the path that the provided request target was registered with
	 */
	String pathForTarget(IRequestTarget requestTarget);
}
