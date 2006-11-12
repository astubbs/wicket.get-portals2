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
package wicket.markup.html.form;

import wicket.IRequestListener;
import wicket.RequestListenerInterface;

/**
 * Listener interface that is called when a form is submitted.
 * 
 * @author Jonathan Locke
 */
public interface IFormSubmitListener extends IRequestListener
{
	/** Listener interface */
	public static final RequestListenerInterface INTERFACE = new RequestListenerInterface(
			IFormSubmitListener.class);

	/**
	 * Called when a form is submitted
	 */
	void onFormSubmitted();
}
