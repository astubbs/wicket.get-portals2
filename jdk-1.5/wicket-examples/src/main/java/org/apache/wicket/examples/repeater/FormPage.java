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
package org.apache.wicket.examples.repeater;

import java.util.Iterator;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.OddEvenItem;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.markup.repeater.ReuseIfModelsEqualStrategy;
import org.apache.wicket.markup.repeater.util.ModelIteratorAdapter;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;


/**
 * Page that demonstrates using RefreshingView in a form. The component reuses its items, to allow
 * adding or removing rows without necessarily validating the form, and preserving component state
 * which preserves error messages, etc.
 */
public class FormPage extends BasePage
{
	final Form form;

	/**
	 * constructor
	 */
	public FormPage()
	{
		form = new Form("form");
		add(form);

		// create a repeater that will display the list of contacts.
		RefreshingView refreshingView = new RefreshingView("simple")
		{
			protected Iterator getItemModels()
			{
				// for simplicity we only show the first 10 contacts
				Iterator contacts = DatabaseLocator.getDatabase().find(0, 10, "firstName", true)
						.iterator();

				// the iterator returns contact objects, but we need it to
				// return models, we use this handy adapter class to perform
				// on-the-fly conversion.
				return new ModelIteratorAdapter(contacts)
				{

					protected IModel model(Object object)
					{
						return new DetachableContactModel((Contact)object);
					}

				};

			}

			protected void populateItem(final Item item)
			{
				// populate the row of the repeater
				IModel contact = item.getModel();
				item.add(new ActionPanel("actions", contact));
				// FIXME use CompoundPropertyModel!
				item.add(new TextField("id", new PropertyModel(contact, "id")));
				item.add(new TextField("firstName", new PropertyModel(contact, "firstName")));
				item.add(new TextField("lastName", new PropertyModel(contact, "lastName")));
				item.add(new TextField("homePhone", new PropertyModel(contact, "homePhone")));
				item.add(new TextField("cellPhone", new PropertyModel(contact, "cellPhone")));
			}

			protected Item newItem(String id, int index, IModel model)
			{
				// this item sets markup class attribute to either 'odd' or
				// 'even' for decoration
				return new OddEvenItem(id, index, model);
			}
		};

		// because we are in a form we need to preserve state of the component
		// hierarchy (because it might contain things like form errors that
		// would be lost if the hierarchy for each item was recreated every
		// request by default), so we use an item reuse strategy.
		refreshingView.setItemReuseStrategy(ReuseIfModelsEqualStrategy.getInstance());


		form.add(refreshingView);
	}

	/**
	 * Panel that houses row-actions
	 */
	private class ActionPanel extends Panel
	{
		/**
		 * @param id
		 *            component id
		 * @param model
		 *            model for contact
		 */
		public ActionPanel(String id, IModel model)
		{
			super(id, model);
			add(new Link("select")
			{
				public void onClick()
				{
					FormPage.this.setSelected((Contact)getParent().getModelObject());
				}
			});

			SubmitLink removeLink = new SubmitLink("remove", form)
			{
				public void onSubmit()
				{
					Contact contact = (Contact)getParent().getModelObject();
					info("Removed contact " + contact);
					DatabaseLocator.getDatabase().delete(contact);
				}
			};
			removeLink.setDefaultFormProcessing(false);
			add(removeLink);
		}
	}
}
