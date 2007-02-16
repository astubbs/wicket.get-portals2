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
package wicket.ajax;

import wicket.Application;
import wicket.RequestCycle;
import wicket.ResourceReference;
import wicket.behavior.AbstractAjaxBehavior;
import wicket.markup.html.IHeaderResponse;
import wicket.markup.html.WicketEventReference;
import wicket.markup.html.resources.JavascriptResourceReference;
import wicket.settings.IDebugSettings;
import wicket.util.string.AppendingStringBuffer;
import wicket.util.string.Strings;
import wicket.util.time.Duration;

/**
 * The base class for Wicket's default AJAX implementation.
 * 
 * @since 1.2
 * 
 * @author Igor Vaynberg (ivaynberg)
 * 
 */
public abstract class AbstractDefaultAjaxBehavior extends AbstractAjaxBehavior
{
	private static final long serialVersionUID = 1L;

	/** reference to the default indicator gif file. */
	public static final ResourceReference INDICATOR = new ResourceReference(
			AbstractDefaultAjaxBehavior.class, "indicator.gif");

	/** reference to the default ajax support javascript file. */
	private static final ResourceReference JAVASCRIPT = new JavascriptResourceReference(
			AbstractDefaultAjaxBehavior.class, "wicket-ajax.js");

	/** reference to the default ajax debug support javascript file. */
	private static final ResourceReference JAVASCRIPT_DEBUG_DRAG = new JavascriptResourceReference(
			AbstractDefaultAjaxBehavior.class, "wicket-ajax-debug-drag.js");

	/** reference to the default ajax debug support javascript file. */
	private static final ResourceReference JAVASCRIPT_DEBUG = new JavascriptResourceReference(
			AbstractDefaultAjaxBehavior.class, "wicket-ajax-debug.js");


	/**
	 * Subclasses should call super.onBind()
	 * 
	 * @see wicket.behavior.AbstractAjaxBehavior#onBind()
	 */
	@Override
	protected void onBind()
	{
		getComponent().setOutputMarkupId(true);
	}

	/**
	 * @see wicket.behavior.AbstractAjaxBehavior#renderHead(wicket.markup.html.IHeaderResponse)
	 */
	@Override
	public void renderHead(IHeaderResponse response)
	{
		super.renderHead(response);

		final IDebugSettings settings = Application.get().getDebugSettings();


		response.renderJavascriptReference(JAVASCRIPT);
		response.renderJavascriptReference(WicketEventReference.INSTANCE);
		if (settings.isAjaxDebugModeEnabled())
		{
			response.renderJavascript("wicketAjaxDebugEnable=true;", "wicket-ajax-debug-enable");
			response.renderJavascriptReference(JAVASCRIPT_DEBUG_DRAG);
			response.renderJavascriptReference(JAVASCRIPT_DEBUG);
		}
	}

	/**
	 * @return ajax call decorator used to decorate the call generated by this
	 *         behavior
	 */
	protected IAjaxCallDecorator getAjaxCallDecorator()
	{
		return null;
	}

	/**
	 * @return javascript that will generate an ajax GET request to this
	 *         behavior
	 */
	protected CharSequence getCallbackScript()
	{
		return getCallbackScript(true);
	}

	/**
	 * @return javascript that will generate an ajax GET request to this
	 *         behavior *
	 * @param recordPageVersion
	 *            if true the url will be encoded to execute on the current page
	 *            version, otherwise url will be encoded to execute on the
	 *            latest page version
	 */
	protected CharSequence getCallbackScript(boolean recordPageVersion)
	{
		return getCallbackScript("wicketAjaxGet('" + getCallbackUrl(recordPageVersion) + "'", null,
				null);
	}

	/**
	 * Returns javascript that performs an ajax callback to this behavior. The
	 * script is decorated by the ajax callback decorator from
	 * {@link AbstractDefaultAjaxBehavior#getAjaxCallDecorator()}.
	 * 
	 * @param partialCall
	 *            Javascript of a partial call to the function performing the
	 *            actual ajax callback. Must be in format
	 *            <code>function(params,</code> with signature
	 *            <code>function(params, onSuccessHandler, onFailureHandler</code>.
	 *            Example: <code>wicketAjaxGet('callbackurl'</code>
	 * @param onSuccessScript
	 *            javascript that will run when the ajax call finishes
	 *            successfully
	 * @param onFailureScript
	 *            javascript that will run when the ajax call finishes with an
	 *            error status
	 * 
	 * @return script that peforms ajax callback to this behavior
	 */
	protected CharSequence getCallbackScript(final CharSequence partialCall,
			final CharSequence onSuccessScript, final CharSequence onFailureScript)
	{
		final IAjaxCallDecorator decorator = getAjaxCallDecorator();

		String indicatorId = findIndicatorId();

		CharSequence success = (onSuccessScript == null) ? "" : onSuccessScript;
		CharSequence failure = (onFailureScript == null) ? "" : onFailureScript;

		if (decorator != null)
		{
			success = decorator.decorateOnSuccessScript(success);
		}

		if (!Strings.isEmpty(indicatorId))
		{
			String hide = ";wicketHide('" + indicatorId + "');";
			success = success + hide;
			failure = failure + hide;
		}

		if (decorator != null)
		{
			failure = decorator.decorateOnFailureScript(failure);
		}

		AppendingStringBuffer buff = new AppendingStringBuffer(256);
		buff.append("var ").append(IAjaxCallDecorator.WICKET_CALL_RESULT_VAR).append("=");
		buff.append(partialCall).append(", function() { ").append(success);
		buff.append("}, function() { ").append(failure).append("});");

		CharSequence call = buff;

		if (!Strings.isEmpty(indicatorId))
		{
			call = new AppendingStringBuffer("wicketShow('").append(indicatorId).append("');")
					.append(call);
		}

		if (decorator != null)
		{
			call = decorator.decorateScript(call);
		}

		return call;
	}

	/**
	 * 
	 * @return String
	 */
	private String findIndicatorId()
	{
		if (getComponent() instanceof IAjaxIndicatorAware)
		{
			return ((IAjaxIndicatorAware)getComponent()).getAjaxIndicatorMarkupId();
		}

		if (this instanceof IAjaxIndicatorAware)
		{
			return ((IAjaxIndicatorAware)this).getAjaxIndicatorMarkupId();
		}

		return null;
	}

	/**
	 * @see wicket.behavior.IBehaviorListener#onRequest()
	 */
	public final void onRequest()
	{
		AjaxRequestTarget target = new AjaxRequestTarget();
		RequestCycle.get().setRequestTarget(target);
		respond(target);
	}

	/**
	 * @param target
	 *            The AJAX target
	 */
	// TODO rename this to onEvent or something? respond is mostly the same as
	// onRender
	// this is not the case this is still the event handling period. respond is
	// called
	// in the RequestCycle on the AjaxRequestTarget..
	protected abstract void respond(AjaxRequestTarget target);

	/**
	 * Wraps the provided javascript with a throttled block. Throttled behaviors
	 * only execute once within the given delay even though they are triggered
	 * multiple times.
	 * <p>
	 * For example, this is useful when attaching an event behavior to the
	 * onkeypress event. It is not desirable to have an ajax call made every
	 * time the user types so we throttle that call to a desirable delay, such
	 * as once per second. This gives us a near real time ability to provide
	 * feedback without overloading the server with ajax calls.
	 * 
	 * @param script
	 *            javascript to be throttled
	 * @param throttleId
	 *            the id of the throttle to be used. Usually this should remain
	 *            constant for the same javascript block.
	 * @param throttleDelay
	 *            time span within which the javascript block will only execute
	 *            once
	 * @return wrapped javascript
	 */
	public static final CharSequence throttleScript(CharSequence script, String throttleId,
			Duration throttleDelay)
	{
		if (Strings.isEmpty(script))
		{
			throw new IllegalArgumentException("script cannot be empty");
		}

		if (Strings.isEmpty(throttleId))
		{
			throw new IllegalArgumentException("throttleId cannot be empty");
		}

		if (throttleDelay == null)
		{
			throw new IllegalArgumentException("throttleDelay cannot be null");
		}

		return new AppendingStringBuffer("wicketThrottler.throttle( '").append(throttleId).append(
				"', ").append(throttleDelay.getMilliseconds()).append(", function() { ").append(
				script).append("});");
	}
}
