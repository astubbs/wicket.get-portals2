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
package org.apache.wicket.examples.ajax.builtin;

import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.calldecorator.AjaxCallDecorator;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.PropertyModel;


/**
 * Ajax links demo.
 * 
 * @author ivaynberg
 */
public class LinksPage extends BasePage
{
	private int counter1 = 0;
	private int counter2 = 0;
	private int counter3 = 0;

	/**
	 * @return Value of counter1
	 */
	public int getCounter1()
	{
		return counter1;
	}

	/**
	 * @return Value for counter2
	 */
	public int getCounter2()
	{
		return counter2;
	}

	/**
	 * @return Value of counter3
	 */
	public int getCounter3()
	{
		return counter3;
	}

	/**
	 * Constructor
	 */
	public LinksPage()
	{
		final Label c1 = new Label("c1", new PropertyModel<Integer>(this, "counter1"));
		c1.setOutputMarkupId(true);
		add(c1);

		final Label c2 = new Label("c2", new PropertyModel<Integer>(this, "counter2"));
		c2.setOutputMarkupId(true);
		add(c2);

		final Label c3 = new Label("c3", new PropertyModel<Integer>(this, "counter3"));
		c3.setOutputMarkupId(true);
		add(c3);

		add(new AjaxLink("c1-link")
		{
			@Override
			public void onClick(AjaxRequestTarget target)
			{
				counter1++;
				target.addComponent(c1);
			}
		});

		add(new AjaxFallbackLink("c2-link")
		{
			@Override
			public void onClick(AjaxRequestTarget target)
			{
				counter2++;
				// notice that for a fallback link we need to makesure the
				// target is not null. if the target is null ajax failed and the
				// fallback was used, so there is no need to do any ajax-related
				// processing.
				if (target != null)
				{
					target.addComponent(c2);
				}
			}
		});

		add(new IndicatingAjaxLink("c3-link")
		{
			@Override
			public void onClick(AjaxRequestTarget target)
			{
				counter3++;
				target.addComponent(c3);
				// sleep for 5 seconds to show off the busy indicator
				try
				{
					Thread.sleep(5000);
				}
				catch (InterruptedException e)
				{
					// noop
				}
			}
		});

		add(new AjaxLink("success-link")
		{
			@Override
			public void onClick(AjaxRequestTarget target)
			{
			}

			@Override
			protected org.apache.wicket.ajax.IAjaxCallDecorator getAjaxCallDecorator()
			{
				return new AjaxCallDecorator()
				{
					@Override
					public CharSequence decorateOnSuccessScript(CharSequence script)
					{
						return "alert('Success');";
					}

					@Override
					public CharSequence decorateOnFailureScript(CharSequence script)
					{
						return "alert('Failure');";
					}

					@Override
					public CharSequence decorateScript(CharSequence script)
					{
						return "alert('Before ajax call');" + script;
					}
				};
			};
		});

		add(new AjaxLink("failure-link")
		{
			@Override
			public void onClick(AjaxRequestTarget target)
			{
				throw new WicketRuntimeException("Failure link clicked");
			}

			@Override
			protected org.apache.wicket.ajax.IAjaxCallDecorator getAjaxCallDecorator()
			{
				return new AjaxCallDecorator()
				{
					@Override
					public CharSequence decorateOnSuccessScript(CharSequence script)
					{
						return "alert('Success');";
					}

					@Override
					public CharSequence decorateOnFailureScript(CharSequence script)
					{
						return "alert('Failure');";
					}

					@Override
					public CharSequence decorateScript(CharSequence script)
					{
						return "alert('Before ajax call');" + script;
					}
				};
			};
		});

		add(new AjaxLink("set-response-page")
		{
			@Override
			public void onClick(AjaxRequestTarget target)
			{
				setResponsePage(new LinksPage());
			}
		});

		add(new AjaxLink("exception")
		{
			@Override
			public void onClick(AjaxRequestTarget target)
			{
				throw new RuntimeException("test whether the exception handling works");
			}
		});
	}
}