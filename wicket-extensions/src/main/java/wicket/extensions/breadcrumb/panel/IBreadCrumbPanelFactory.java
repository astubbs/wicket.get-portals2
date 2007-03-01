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
package wicket.extensions.breadcrumb.panel;

import wicket.IClusterable;
import wicket.MarkupContainer;
import wicket.extensions.breadcrumb.IBreadCrumbModel;

/**
 * Factory interface to enabled defered creation of a bread crumb panel while
 * getting the proper id for creation. Mainly meant for supporting
 * {@link BreadCrumbPanel#activate(IBreadCrumbPanelFactory)}.
 * 
 * @author Eelco Hillenius
 */
public interface IBreadCrumbPanelFactory extends IClusterable
{
	/**
	 * Creates a new {@link BreadCrumbPanel bread crumb panel} instance. The
	 * provided component id must be used when creating the panel.
	 * 
	 * @param parent
	 *            The parent component
	 * 
	 * @param componentId
	 *            The component id for the new panel.
	 * @param breadCrumbModel
	 *            The bread crumb model
	 * @return A new bread crumb panel instance
	 */
	BreadCrumbPanel create(MarkupContainer parent, String componentId,
			IBreadCrumbModel breadCrumbModel);
}