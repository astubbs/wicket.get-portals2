/*
 * $Id$
 * $Revision$
 * $Date$
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package wicket.extensions.markup.html.repeater.refreshing;

import java.util.Iterator;

import wicket.model.IModel;

/**
 * Iterator adapter that wraps adaptee's elements with a model. Makes it easy to
 * implement {@link RefreshingView#getItemModels() }
 * 
 * @author Igor Vaynberg (ivaynberg)
 * 
 */
public abstract class ModelIteratorAdapter implements Iterator
{
	private Iterator delegate;

	/**
	 * Constructor
	 * 
	 * @param delegate
	 *            iterator that will be wrapped
	 */
	public ModelIteratorAdapter(Iterator delegate)
	{
		this.delegate = delegate;
	}

	/**
	 * @see java.util.Iterator#hasNext()
	 */
	public boolean hasNext()
	{
		return delegate.hasNext();
	}

	/**
	 * @see java.util.Iterator#next()
	 */
	public Object next()
	{
		return model(delegate.next());
	}

	/**
	 * @see java.util.Iterator#remove()
	 */
	public void remove()
	{
		delegate.remove();
	}

	/**
	 * This method is used to wrap the provided object with an implementation of
	 * IModel. The provided object is guaranteed to be returned from the
	 * delegate iterator.
	 * 
	 * @param object
	 *            object to be wrapped
	 * @return IModel wrapper for the object
	 */
	abstract protected IModel model(Object object);
}
