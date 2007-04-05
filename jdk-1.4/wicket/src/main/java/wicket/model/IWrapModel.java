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
 * A marker interface that represents a model that serves as a wrapper for
 * another. Typically these models are produced by the following methods:
 * {@link IComponentAssignedModel#wrapOnAssignment(wicket.Component)} and
 * {@link IInheritanceAware#wrapOnInheritance(wicket.Component)}
 * 
 * <b>Nested Models </b>- IModels can be nested and the innermost model is also
 * known as the "root" model since it is the model on which the outer models
 * rely. The getNestedModel() method on IModel gets any nested model within the
 * given model. This allows Component.sameInnermostModel() to compare two models
 * to see if they both have the same innermost model (the same most nested
 * model).
 * <p>
 * For example, a Form might have a Person model and then a TextField might have
 * a PropertyModel which is the "name" property of the Person model. In this
 * case, PropertyModel will implement getNestedModel(), returning the Person
 * model which is the nested model of the property model.
 * 
 * @author jcompagner
 * @author Igor Vaynberg (ivaynberg)
 */
public interface IWrapModel extends IModel
{
	/**
	 * Gets the wrapped model.
	 * 
	 * @return The wrapped model
	 */
	IModel getWrappedModel();
}
