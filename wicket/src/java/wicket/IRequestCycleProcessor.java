/*
 * $Id$ $Revision$ $Date$
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
 * The request cycle processor is responsible for handling the steps of a
 * request cycle. It's methods are called in a pre-defined order: TODO docme
 * further.
 * 
 * @author hillenius
 */
public interface IRequestCycleProcessor
{
	/**
	 * <p>
	 * Resolves the request and returns the request target.
	 * </p>
	 * <p>
	 * Implementors of this method should be careful not to mix this code with
	 * event handling code; method
	 * {@link #processEvents(RequestCycle)} is meant for that
	 * purpose.
	 * </p>
	 * 
	 * @param requestCycle
	 *            the current request cycle
	 * @return the request target
	 */
	IRequestTarget resolve(RequestCycle requestCycle);

	/**
	 * After a page is restored, this method is responsible for calling any
	 * event handling code based on the request. For example, when a link is
	 * clicked, {@link #resolve(RequestCycle)} should return the page that that
	 * link resides on, and this method should call the
	 * {@link wicket.markup.html.link.ILinkListener} interface on that
	 * component.
	 * 
	 * @param requestCycle
	 *            the current request cycle
	 */
	void processEvents(RequestCycle requestCycle);

	/**
	 * After the target is resolved and the request events are handled, it is
	 * time to respond to the request. This method is responsible for executing
	 * the proper response sequence given the current request target and
	 * response.
	 * 
	 * @param requestCycle
	 *            the current request cycle
	 */
	void respond(RequestCycle requestCycle);

	/**
	 * Whenever a unhandled exception is encountered during the processing of a
	 * request cycle, this method is called to respond to the request in a
	 * proper way.
	 * 
	 * @param e
	 *            any unhandled exception
	 * @param requestCycle
	 *            the current request cycle
	 */
	void respond(Exception e, RequestCycle requestCycle);
}
