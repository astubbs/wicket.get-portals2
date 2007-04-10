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

import org.apache.wicket.markup.html.panel.Panel;

/**
 * The default bar of button components for wizards. This should be good for 90%
 * of the cases. If not, override {@link Wizard#newButtonBar(String)} and
 * provide your own.
 * <p>
 * The button bar holds the {@link PreviousButton previous}, [@link NextButton
 * next}, {@link LastButton last}, [@link CancelButton cancel} and
 * {@link FinishButton finish} buttons. The {@link LastButton last button} is
 * off by default. You can turn it on by having the wizard model return true for
 * {@link IWizardModel#isLastVisible() the is last visble method}.
 * </p>
 * 
 * @author Eelco Hillenius
 */
public class WizardButtonBar extends Panel
{
	private static final long serialVersionUID = 1L;

	/**
	 * Construct.
	 * 
	 * @param id
	 *            The component id
	 * @param wizard
	 *            The containing wizard
	 */
	public WizardButtonBar(String id, Wizard wizard)
	{
		super(id);
		add(new PreviousButton("previous", wizard));
		add(new NextButton("next", wizard));
		add(new LastButton("last", wizard));
		add(new CancelButton("cancel", wizard));
		add(new FinishButton("finish", wizard));
	}
}
