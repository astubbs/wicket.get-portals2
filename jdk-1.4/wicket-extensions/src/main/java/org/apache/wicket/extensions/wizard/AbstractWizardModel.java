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
package org.apache.wicket.extensions.wizard;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Abstract wizard model that provides an implementation for handling
 * {@link IWizardModelListener wizard model listeners} and provides base
 * implementations of many methods. If you want to provide a custom
 * implementation of {@link IWizardModel}, it is recommended you start by
 * overriding this class.
 * 
 * @author eelcohillenius
 */
public abstract class AbstractWizardModel implements IWizardModel
{

	/** Whether cancel functionality is available. */
	private boolean cancelVisible = true;

	/** Whether the last button should be shown at all; false by default. */
	private boolean lastVisible = false;

	/** Listeners for {@link IWizardModelListener model events}. */
	private final List wizardModelListeners = new ArrayList(1);

	/**
	 * Construct.
	 */
	public AbstractWizardModel()
	{
	}

	/**
	 * Adds a wizard model listener.
	 * 
	 * @param listener
	 *            The listener to add
	 */
	public final void addListener(IWizardModelListener listener)
	{
		this.wizardModelListeners.add(listener);
	}

	/**
	 * This implementation just fires
	 * {@link IWizardModelListener#onCancel() a cancel event}. Though this
	 * isn't a very strong contract, it gives all the power to the user of this
	 * model.
	 * 
	 * @see IWizardModel#cancel()
	 */
	public void cancel()
	{
		fireWizardCancelled();
	}

	/**
	 * This implementation just fires
	 * {@link IWizardModelListener#onFinish() a finish event}. Though this
	 * isn't a very strong contract, it gives all the power to the user of this
	 * model.
	 * 
	 * @see IWizardModel#finish()
	 */
	public void finish()
	{
		fireWizardFinished();
	}

	/**
	 * Gets whether cancel functionality is available.
	 * 
	 * @return Whether cancel functionality is available
	 */
	public boolean isCancelVisible()
	{
		return cancelVisible;
	}

	/**
	 * Checks if the last button should be displayed. This method should only
	 * return true if {@link IWizardModel#isLastAvailable} can return true at
	 * any point. Returning false will prevent the last button from appearing on
	 * the wizard at all.
	 * 
	 * @return <tt>true</tt> if the previou last should be displayed,
	 *         <tt>false</tt> otherwise.
	 */
	public boolean isLastVisible()
	{
		return lastVisible;
	}

	/**
	 * Removes a wizard model listener.
	 * 
	 * @param listener
	 *            The listener to remove
	 */
	public final void removeListener(IWizardModelListener listener)
	{
		this.wizardModelListeners.remove(listener);
	}

	/**
	 * Sets whether cancel functionality is available.
	 * 
	 * @param cancelVisible
	 *            Whether cancel functionality is available
	 */
	public void setCancelVisible(boolean cancelVisible)
	{
		this.cancelVisible = cancelVisible;
	}

	/**
	 * Configures if the last button should be displayed.
	 * 
	 * @param lastVisible
	 *            <tt>true</tt> to display the last button, <tt>false</tt>
	 *            otherwise.
	 * @see #isLastVisible
	 */
	public void setLastVisible(boolean lastVisible)
	{
		this.lastVisible = lastVisible;
	}

	/**
	 * Notify listeners that the active step has changed.
	 * 
	 * @param step
	 *            The new step
	 */
	protected final void fireActiveStepChanged(IWizardStep step)
	{
		for (Iterator i = wizardModelListeners.iterator(); i.hasNext();)
		{
			IWizardModelListener listener = (IWizardModelListener)i.next();
			listener.onActiveStepChanged(step);
		}
	}

	/**
	 * Notify listeners that the wizard is finished.
	 */
	protected final void fireWizardCancelled()
	{
		for (Iterator i = wizardModelListeners.iterator(); i.hasNext();)
		{
			IWizardModelListener listener = (IWizardModelListener)i.next();
			listener.onCancel();
		}
	}

	/**
	 * Notify listeners that the wizard is finished.
	 */
	protected final void fireWizardFinished()
	{
		for (Iterator i = wizardModelListeners.iterator(); i.hasNext();)
		{
			IWizardModelListener listener = (IWizardModelListener)i.next();
			listener.onFinish();
		}
	}
}
