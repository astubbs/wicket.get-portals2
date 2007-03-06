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
package wicket.markup.repeater;

import java.util.Iterator;

import wicket.IClusterable;
import wicket.MarkupContainer;
import wicket.model.IModel;

/**
 * Interface for item reuse strategies.
 * <p>
 * <u>Note:</u> Child items will be rendered in the order they are provided by
 * the returned iterator, so it is important that the returned iterator
 * preserves the order of the <code>models</code> iterator.
 * </p>
 * 
 * @author Igor Vaynberg (ivaynberg)
 * 
 * @param <T>
 *            Type of model object this component holds
 */
public interface IItemReuseStrategy<T> extends IClusterable
{

	/**
	 * Returns an iterator over items that will be added to the view. The
	 * iterator needs to return all the items because the old ones are removed
	 * prior to the new ones added.
	 * <p>
	 * <u>Note:</u> Child items will be rendered in the order they are provided
	 * by the returned iterator, so it is important that the returned iterator
	 * preserves the order of the <code>models</code> iterator.
	 * </p>
	 * 
	 * @param parent
	 *            parent for returned item components
	 * 
	 * @param factory
	 *            implementation of IItemFactory
	 * @param models
	 *            iterator over models for items
	 * @param existingItems
	 *            iterator over child items from the previous request
	 * @return iterator over items that will be added after all the old items
	 *         are removed.
	 */
	Iterator<Item<T>> getItems(IItemFactory<T> factory,
			Iterator<IModel<T>> models, Iterator<Item<T>> existingItems);
}
