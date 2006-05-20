/*
 * $Id$
 * $Revision$ $Date$
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
package wicket.model;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import wicket.Component;
import wicket.RequestCycle;

/**
 * This provide a base class to work with detachable {@link wicket.model.IModel}s.
 * It encapsulates the logic for attaching and detaching models. The
 * onAttach() abstract method will be called at the first access to the model
 * within a request and - if the model was attached earlier, onDetach() will be
 * called at the end of the request. In effect, attachment and detachment is
 * only done when it is actually needed.
 * 
 * @author Chris Turner
 * @author Eelco Hillenius
 * @author Jonathan Locke
 */
public abstract class AbstractDetachableModel<V> implements IModel<V>
{
	/** Logger. */
	private static final Log log = LogFactory.getLog(AbstractDetachableModel.class);

	/**
	 * Transient flag to prevent multiple detach/attach scenario. We need to
	 * maintain this flag as we allow 'null' model values.
	 */
	private transient boolean attached = false;

	/**
	 * Attaches the model.
	 */
	public final void attach()
	{
		if (!attached)
		{
			if (log.isDebugEnabled())
			{
				log.debug("attaching " + this + " for requestCycle " + RequestCycle.get());
			}
			attached = true;
			onAttach();
		}
	}

	/**
	 * @see IModel#detach()
	 */
	public final void detach()
	{
		if (attached)
		{
			if (log.isDebugEnabled())
			{
				log.debug("detaching " + this + " for requestCycle " + RequestCycle.get());
			}
			attached = false;
			onDetach();
		}

		IModel nestedModel = getNestedModel();
		if (nestedModel != null)
		{
			// do detach the nested model because this one could be attached 
			// if the model is used not through this compound model
			nestedModel.detach();
		}
	}
	
	/**
	 * @see wicket.model.IModel#getNestedModel()
	 */
	public abstract IModel getNestedModel();

	/**
	 * @see wicket.model.IModel#getObject(Component)
	 */
	public final V getObject(final Component component)
	{
		attach();
		return onGetObject(component);
	}

	/**
	 * Gets whether this model has been attached to the current session.
	 * 
	 * @return whether this model has been attached to the current session
	 */
	public boolean isAttached()
	{
		return attached;
	}

	/**
	 * @see wicket.model.IModel#setObject(Component, Object)
	 */
	public final void setObject(final Component component, final V object)
	{
		attach();
		onSetObject(component, object);
	}

	/**
	 * @see Object#toString() 
	 */
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer("Model:classname=[");
		sb.append(getClass().getName()).append("]");
		sb.append(":attached=").append(isAttached());
		return sb.toString();
	}

	/**
	 * Attaches to the current request. Implement this method with custom
	 * behavior, such as loading the model object.
	 */
	protected abstract void onAttach();

	/**
	 * Detaches from the current request. Implement this method with custom
	 * behavior, such as setting the model object to null.
	 */
	protected abstract void onDetach();

	/**
	 * Called when getObject is called in order to retrieve the detachable
	 * object. Before this method is called, getObject() always calls attach()
	 * to ensure that the object is attached.
	 * 
	 * @param component
	 *            The component asking for the object
	 * @return The object
	 */
	protected abstract V onGetObject(final Component component);

	/**
	 * Called when setObject is called in order to change the detachable
	 * object. Before this method is called, setObject() always calls attach()
	 * to ensure that the object is attached.
	 * 
	 * @param component
	 *			The component asking for replacement of the model object
	 * @param object
	 * 			The new model object
	 */
	protected abstract void onSetObject(final Component component, final V object);
}
