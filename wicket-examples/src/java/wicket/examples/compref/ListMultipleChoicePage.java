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
package wicket.examples.compref;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import wicket.examples.WicketExamplePage;
import wicket.markup.html.form.Form;
import wicket.markup.html.form.ListMultipleChoice;
import wicket.markup.html.panel.FeedbackPanel;
import wicket.model.CompoundPropertyModel;

/**
 * Page with examples on {@link wicket.markup.html.form.ListMultipleChoice}.
 * 
 * @author Eelco Hillenius
 */
public class ListMultipleChoicePage extends WicketExamplePage
{
	/** available sites for selection. */
	private static final List SITES = Arrays.asList(new String[] { 
	        "The Server Side", "Java Lobby", "Java.Net" });

	/** available choices for large selection box. */
	private static final List MANY_CHOICES = Arrays.asList(new String[] {
			"Choice1", "Choice2", "Choice3", "Choice4", "Choice5", "Choice6", "Choice7", "Choice8", "Choice9", });

	/**
	 * Constructor
	 */
	public ListMultipleChoicePage()
	{
		final Input input = new Input();
		setModel(new CompoundPropertyModel(input));

		// Add a FeedbackPanel for displaying our messages
		FeedbackPanel feedbackPanel = new FeedbackPanel("feedback");
		add(feedbackPanel);

		// Add a form with an onSumbit implementation that sets a message
		Form form = new Form("form")
		{
			protected void onSubmit()
			{
				info("input: " + input);
			}
		};
		add(form);

		// Add a multiple list choice component that uses Input's 'site'
		// property to designate the
		// current selection, and that uses the SITES list for the available
		// options.
		// Note that our model here holds a Collection, as we need to store
		// multiple values too
		ListMultipleChoice listChoice = new ListMultipleChoice("sites", SITES);
		form.add(listChoice);
		
		listChoice = new ListMultipleChoice("choices", MANY_CHOICES).setMaxRows(5);
		form.add(listChoice);
	}

	/** Simple data class that acts as a model for the input fields. */
	private static class Input implements Serializable
	{
		/** the selected sites. */
		public List sites = new ArrayList();

		/** the selected choices. */
		public List choices = new ArrayList();
		
		/** adds pre-selected items to the choices list */
		public Input() {
			choices.add("Choice3");
			choices.add("Choice4");
			choices.add("Choice5");
		}

		/**
		 * @see java.lang.Object#toString()
		 */
		public String toString()
		{
			return "sites = '" + listAsString(sites) + "', choices='" + listAsString(choices) + "'";
		}

		private String listAsString(List list)
		{
			StringBuffer b = new StringBuffer();
			for(Iterator i = list.iterator(); i.hasNext();)
			{
				b.append(i.next());
				if (i.hasNext()) b.append(", ");
			}
			return b.toString();
		}
	}
}