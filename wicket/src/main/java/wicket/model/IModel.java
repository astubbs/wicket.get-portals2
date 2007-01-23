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
package wicket.model;


/**
 * A IModel wraps the actual model Object used by a Component. IModel
 * implementations are used as a facade for the real model so that users have
 * control over the actual persistence strategy. Note that objects implementing
 * this interface will be stored in the Session. Hence, you should use
 * (non-transient) instance variables sparingly.
 * <ul>
 * <li><b>Basic Models </b>- To implement a basic (non-detachable) model which
 * holds its entire state in the Session, you can either subclass
 * {@link AbstractModel}, or use the simple model wrapper {@link Model}
 * directly.
 * 
 * <li><b>Detachable Models </b>- IModel inherits a hook,
 * {@link IDetachable#detach()}, so that interface implementers can detach
 * transient information when a model is no longer being actively used by the
 * framework. This reduces memory use and reduces the expense of replicating the
 * model in a clustered server environment. To implement a detachable model, you
 * should generally extend {@link wicket.model.AbstractDetachableModel}instead
 * of implementing IModel directly.
 * 
 * <li><b>Property Models </b>- The AbstractPropertyModel class provides
 * default functionality for property models. A property model provides access
 * to a particular property of its wrapped model.
 * 
 * <li><b>Compound Property Models </b>- The IModel interface is parameterized
 * by Component, allowing a model to be shared among several Components. When
 * the {@link IModel#getObject()}method is called, the value returned will
 * depend on the Component which is asking for the value. Likewise, the
 * {@link IModel#setObject(Object)}method sets a different property depending
 * on which Component is doing the setting. For more information on
 * CompoundPropertyModels and model inheritance, see
 * {@link wicket.model.CompoundPropertyModel}and {@link wicket.Page}.
 * </ul>
 * 
 * @see wicket.Component#sameRootModel(wicket.Component)
 * @see wicket.Component#sameRootModel(IModel)
 * 
 * @param <T>
 *            Type of model object this model holds
 * 
 * @author Chris Turner
 * @author Eelco Hillenius
 * @author Jonathan Locke
 * 
 * TODO 3.0: Vote on renaming get/setObject to get/setValue
 */
public interface IModel<T> extends IDetachable
{
	/**
	 * Gets the model object.
	 * 
	 * @return The model object
	 */
	T getObject();

	/**
	 * Sets the model object.
	 * 
	 * @param object
	 *            The model object
	 */
	void setObject(final T object);
}
