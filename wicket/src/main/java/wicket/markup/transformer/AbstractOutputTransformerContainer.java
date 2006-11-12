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
package wicket.markup.transformer;

import wicket.Component;
import wicket.MarkupContainer;
import wicket.Response;
import wicket.WicketRuntimeException;
import wicket.markup.ComponentTag;
import wicket.markup.MarkupStream;
import wicket.model.IModel;
import wicket.response.StringResponse;

/**
 * This abstract container provides the means to post-process the markup
 * generated by its child components (excluding the containers tag)
 * <p>
 * Please see <code>IBehavior</code> for an alternativ based on IBehavior
 * 
 * @see wicket.markup.transformer.AbstractTransformerBehavior
 * @see wicket.markup.transformer.ITransformer
 * 
 * @author Juergen Donnerstag
 * @param <T> 
 */
public abstract class AbstractOutputTransformerContainer<T> extends MarkupContainer<T>
		implements
			ITransformer
{
	private static final long serialVersionUID = 1L;

	/** Whether the containers tag shall be transformed as well */
	private boolean transformBodyOnly = true;

	/**
	 * Construct
	 * 
	 * @see wicket.Component#Component(MarkupContainer,String)
	 */
	public AbstractOutputTransformerContainer(MarkupContainer parent, final String id)
	{
		super(parent, id);
	}

	/**
	 * Construct
	 * 
	 * @see wicket.Component#Component(MarkupContainer,String, IModel)
	 */
	public AbstractOutputTransformerContainer(MarkupContainer parent, final String id,
			final IModel<T> model)
	{
		super(parent, id, model);
	}
	
	/**
	 * 
	 * @see wicket.markup.html.WebMarkupContainer#getMarkupType()
	 */
	@Override
	public String getMarkupType()
	{
		return "html";
	}

	/**
	 * You can choose whether the body of the tag excluding the tag shall be
	 * transformed or including the tag.
	 * 
	 * @param value
	 *            If true, only the body is applied to transformation.
	 * @return this
	 */
	public MarkupContainer setTransformBodyOnly(final boolean value)
	{
		this.transformBodyOnly = value;
		return this;
	}

	/**
	 * Create a new response object which is used to store the markup generated
	 * by the child objects.
	 * 
	 * @return Response object. Must not be null
	 */
	protected Response newResponse()
	{
		return new StringResponse();
	}

	/**
	 * 
	 * @see wicket.markup.transformer.ITransformer#transform(wicket.Component,
	 *      java.lang.String)
	 */
	public abstract CharSequence transform(final Component component, final String output)
			throws Exception;

	/**
	 * @see wicket.Component#onComponentTagBody(wicket.markup.MarkupStream,
	 *      wicket.markup.ComponentTag)
	 */
	@Override
	protected final void onComponentTagBody(final MarkupStream markupStream,
			final ComponentTag openTag)
	{
		if (this.transformBodyOnly == true)
		{
			execute(new Runnable()
			{
				public void run()
				{
					// Invoke default execution
					AbstractOutputTransformerContainer.super.onComponentTagBody(markupStream,
							openTag);
				}
			});
		}
		else
		{
			super.onComponentTagBody(markupStream, openTag);
		}
	}

	/**
	 * @see wicket.Component#onRender(MarkupStream)
	 */
	@Override
	protected final void onRender(final MarkupStream markupStream)
	{
		if (this.transformBodyOnly == false)
		{
			execute(new Runnable()
			{
				public void run()
				{
					// Invoke default execution
					AbstractOutputTransformerContainer.super.onRender(markupStream);
				}
			});
		}
		else
		{
			super.onRender(markupStream);
		}
	}

	/**
	 * 
	 * @param code
	 */
	private final void execute(final Runnable code)
	{
		// Temporarily replace the web response with a String response
		final Response webResponse = this.getResponse();

		try
		{
			// Create a new response object
			final Response response = newResponse();
			if (response == null)
			{
				throw new IllegalStateException("newResponse() must not return null");
			}

			// and make it the current one
			this.getRequestCycle().setResponse(response);

			// Invoke default execution
			code.run();

			try
			{
				// Tranform the data
				// TODO post 1.2 transform also just charsequence, is this 1.2
				// or 1.1??
				CharSequence output = transform(this, response.toString());
				webResponse.write(output);
			}
			catch (Exception ex)
			{
				throw new WicketRuntimeException("Error while transforming the output: " + this, ex);
			}
		}
		finally
		{
			// Restore the original response
			this.getRequestCycle().setResponse(webResponse);
		}
	}
}
