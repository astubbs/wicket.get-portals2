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
package wicket.examples.compref;

import wicket.AttributeModifier;
import wicket.Component;
import wicket.IClusterable;
import wicket.examples.WicketExamplePage;
import wicket.markup.html.basic.Label;
import wicket.markup.html.link.Link;
import wicket.model.Model;

/**
 * Page with examples on {@link wicket.markup.html.link.Link}.
 * 
 * @author Eelco Hillenius
 */
public class LinkPage extends WicketExamplePage
{
	/**
	 * Constructor
	 */
	public LinkPage()
	{
		// power to the annonymous classes!

		// first create a simple value holder object
		final ClickCount count1 = new ClickCount();

		// add a link which, when clicked, increases our counter
		// when a link is clicked, its onClick method is called
		Link link1 = new Link("link1")
		{
			public void onClick()
			{
				count1.clicks++;
			}
		};
		add(link1);

		// add a counter label to the link so that we can display it in the body
		// of the link
		link1.add(new Label("label1", new Model()
		{
			public Object getObject(Component component)
			{
				return Integer.toString(count1.clicks);
			}
		}));

		// we can attach Link components to any HTML tag we want. If it is an
		// anchor (<a href...),
		// the url to this component is put in the href attribute. For other
		// components, a
		// onclick javascript event handler is created that triggers the round
		// trip

		// it is ofcourse possible to - instead of the above approach - hide as
		// much of the
		// component as possible within a class.
		class CustomLink extends Link
		{
			final ClickCount count2;

			/**
			 * Construct.
			 * 
			 * @param id
			 */
			public CustomLink(String id)
			{
				super(id);
				count2 = new ClickCount();
				add(new ClickCountLabel("label2", count2));
			}

			public void onClick()
			{
				count2.clicks++;
			}
		}
		add(new CustomLink("link2"));

		// and if we know we are going to attach it to a <input type="button>
		// tag, we shouldn't
		// use a label, but an AttributeModifier instead.
		class ButtonLink extends Link
		{
			final ClickCount count3;

			/**
			 * Construct.
			 * 
			 * @param id
			 */
			public ButtonLink(String id)
			{
				super(id);
				count3 = new ClickCount();
				add(new AttributeModifier("value", new Model()
				{
					public Object getObject(Component component)
					{
						// we just replace the whole string. You could use
						// custom
						// AttributeModifiers to e.g. just replace one part of
						// the
						// string if you want
						return "this button is clicked " + count3.clicks + " times";
					}
				}));
			}

			public void onClick()
			{
				count3.clicks++;
			}
		}
		add(new ButtonLink("link3"));
	}

	/**
	 * Simple class to holds the number of clicks.
	 */
	private static class ClickCount implements IClusterable
	{
		/** number of clicks. */
		private int clicks = 0;
	}

	/**
	 * Simple custom label that displays the link click count.
	 */
	private static class ClickCountLabel extends Label
	{
		/**
		 * Construct.
		 * 
		 * @param id
		 *            component id
		 * @param clickCount
		 *            the count object
		 */
		public ClickCountLabel(String id, final ClickCount clickCount)
		{
			// call super with a simple annonymous class model that displays the
			// current number of clicks
			super(id, new Model()
			{
				public Object getObject(Component component)
				{
					return Integer.toString(clickCount.clicks);
				}
			});
		}
	}

	// ----------

	final ClickCount count1 = new ClickCount(); // simple counter object
	Link link1 = new Link("link1")
	{
		public void onClick()
		{
			count1.clicks++;
		}
	};

	/**
	 * Override base method to provide an explanation
	 */
	protected void explain()
	{
		String html = "<a href=\"#\" wicket:id=\"link1\">this link is clicked <span wicket:id=\"label1\">n</span> times</a>";
		String code = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;final ClickCount count1 = new ClickCount(); // simple counter object\n"
				+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Link link1 = new Link(\"link1\") {\n"
				+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;public void onClick() {\n"
				+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;count1.clicks++;\n"
				+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}\n"
				+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;};\n"
				+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;link1.add(new Label(\"label1\", new Model() {\n"
				+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;public Object getObject(Component component) {\n"
				+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;return Integer.toString(count1.clicks);\n"
				+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}\n"
				+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}));\n"
				+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;add(link1);";
		add(new ExplainPanel(html, code));

	}

}