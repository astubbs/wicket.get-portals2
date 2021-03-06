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
package org.apache.wicket.markup.html.link;

import org.apache.wicket.Component;
import org.apache.wicket.IClusterable;
import org.apache.wicket.IPageMap;
import org.apache.wicket.Page;
import org.apache.wicket.PageMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A popup specification can be used as a property of the {@link Link}classes to specify that the
 * link should be rendered with an onClick javascript event handler that opens a new window with the
 * links' URL.
 * <p>
 * You can 'or' display flags together like this:
 * 
 * <pre>
 * new PopupSettings(PopupSettings.RESIZABLE | PopupSettings.SCROLLBARS);
 * </pre>
 * 
 * </p>
 * 
 * @author Jonathan Locke
 * @author Eelco Hillenius
 */
public class PopupSettings implements IClusterable
{
	/** The log. */
	private static final Logger log = LoggerFactory.getLogger(PopupSettings.class);

	private static final long serialVersionUID = 1L;

	/** Flag to include location bar */
	public static final int LOCATION_BAR = 1;

	/** Flag to include menu bar */
	public static final int MENU_BAR = 2;

	/** Flag to make popup resizable */
	public static final int RESIZABLE = 4;

	/** Flag to include scrollbars */
	public static final int SCROLLBARS = 8;

	/** Flag to include status bar */
	public static final int STATUS_BAR = 16;

	/** Flag to include location bar */
	public static final int TOOL_BAR = 32;

	/** Display flags */
	private int displayFlags;

	/** Height of popup window. */
	private int height = -1;

	/** Left position of popup window. */
	private int left = -1;

	/**
	 * The target to put in JavaScript. This implementation simply refers to the href element, but
	 * clients may want to override this (e.g. when the HTML element is not an anchor).
	 */
	private String target = "href";

	/** Top position of popup window. */
	private int top = -1;

	/** Width of popup window. */
	private int width = -1;

	/**
	 * The logical name of the window. This can be anything you want, although you should use
	 * alphanumeric characters only (no spaces or punctuation). If you have a window already open
	 * and call window.open a second time using the same windowName, the first window will be reused
	 * rather than opening a second window.
	 */
	private String windowName = null;

	/**
	 * The pagemap name where the page that will be created by this popuplink will be created in.
	 */
	private String pageMapName;

	/**
	 * Construct. If you are not using these popup settings with an external link - in which case we
	 * don't need to know about a page map - you should use one of the constructors with a {@link
	 * PageMap} argument. Typically, you should put any popup in a separate page map as Wicket holds
	 * references to a limited number of pages/ versions only. If you don't put your popup in a
	 * separate page map, the user might get page expired exceptions when getting back to the main
	 * window again.
	 */
	public PopupSettings()
	{
	}

	/**
	 * Construct.
	 * 
	 * @param displayFlags
	 * 		Display flags
	 */
	public PopupSettings(final int displayFlags)
	{
		this(null, displayFlags);
	}

	/**
	 * Construct.
	 * 
	 * @param pagemap
	 * 		The pagemap where this popup must be in. Typically, you should put any popup in a
	 * 		separate page map as Wicket holds references to a limited number of pages/ versions
	 * 		only. If you don't put your popup in a separate page map, the user might get page
	 * 		expired exceptions when getting back to the main window again.
	 */
	public PopupSettings(IPageMap pagemap)
	{
		this(pagemap, 0);
	}

	/**
	 * Construct.
	 * 
	 * @param pagemap
	 * 		The pagemap where this popup must be in. Typically, you should put any popup in a
	 * 		separate page map as Wicket holds references to a limited number of pages/ versions
	 * 		only. If you don't put your popup in a separate page map, the user might get page
	 * 		expired exceptions when getting back to the main window again.
	 * @param displayFlags
	 * 		Display flags
	 */
	public PopupSettings(IPageMap pagemap, final int displayFlags)
	{
		this.displayFlags = displayFlags;
		if (pagemap != null)
		{
			pageMapName = pagemap.getName();
			windowName = pageMapName;
		}
	}

	/**
	 * Get the onClick javascript event handler.
	 * 
	 * @return the onClick javascript event handler
	 */
	public String getPopupJavaScript()
	{
		String windowTitle = windowName;

		if (windowTitle == null)
		{
			windowTitle = "";
		}
		else
		{
			// Fix for IE bug.
			windowTitle = windowTitle.replaceAll("\\W", "_");
		}

		StringBuffer script = new StringBuffer("var w = window.open(" + target + ", '").append(
			windowTitle).append("', '");

		script.append("scrollbars=").append(flagToString(SCROLLBARS));
		script.append(",location=").append(flagToString(LOCATION_BAR));
		script.append(",menuBar=").append(flagToString(MENU_BAR));
		script.append(",resizable=").append(flagToString(RESIZABLE));
		script.append(",status=").append(flagToString(STATUS_BAR));
		script.append(",toolbar=").append(flagToString(TOOL_BAR));

		if (width != -1)
		{
			script.append(",width=").append(width);
		}

		if (height != -1)
		{
			script.append(",height=").append(height);
		}

		if (left != -1)
		{
			script.append(",left=").append(left);
		}

		if (top != -1)
		{
			script.append(",top=").append(top);
		}

		script.append("'); if(w.blur) w.focus();").append(" return false;");

		return script.toString();
	}

	/**
	 * Sets the popup window height.
	 * 
	 * @param popupHeight
	 * 		the popup window height.
	 * @return This
	 */
	public PopupSettings setHeight(int popupHeight)
	{
		height = popupHeight;
		return this;
	}

	/**
	 * Sets the left position of the popup window.
	 * 
	 * @param popupPositionLeft
	 * 		the left position of the popup window.
	 * @return This
	 */
	public PopupSettings setLeft(int popupPositionLeft)
	{
		left = popupPositionLeft;
		return this;
	}

	/**
	 * Sets the target of the link. The default implementation simply refers to the href element,
	 * but clients may want to override this (e.g. when the HTML element is not an anchor) by
	 * setting the target explicitly.
	 * 
	 * @param target
	 * 		the target of the link
	 */
	public void setTarget(String target)
	{
		this.target = target;
	}

	/**
	 * Sets the top position of the popup window.
	 * 
	 * @param popupPositionTop
	 * 		the top position of the popup window.
	 * @return This
	 */
	public PopupSettings setTop(int popupPositionTop)
	{
		top = popupPositionTop;
		return this;
	}

	/**
	 * Sets the popup window width.
	 * 
	 * @param popupWidth
	 * 		the popup window width.
	 * @return This
	 */
	public PopupSettings setWidth(int popupWidth)
	{
		width = popupWidth;
		return this;
	}

	/**
	 * Sets the window name. The logical name of the window. This can be anything you want, although
	 * you should use alphanumeric characters only (no spaces or punctuation). If you have a window
	 * already open and call window.open a second time using the same windowName, the first window
	 * will be reused rather than opening a second window
	 * 
	 * @param popupWindowName
	 * 		window name.
	 * @return This
	 */
	public PopupSettings setWindowName(String popupWindowName)
	{
		if (popupWindowName != null)
		{
			windowName = popupWindowName;
			if (pageMapName != null && (!pageMapName.equals(popupWindowName)))
			{
				log.warn("the page map and window name should be the same. The page map was " +
					pageMapName + ", and the requested window name is " + popupWindowName +
					"; changing the page map to " + popupWindowName);
			}
			pageMapName = popupWindowName;
		}
		return this;
	}

	/**
	 * @param flag
	 * 		The flag to test
	 * @return Yes or no depending on whether the flag is set
	 */
	private String flagToString(final int flag)
	{
		return (displayFlags & flag) != 0 ? "yes" : "no";
	}

	/**
	 * Gets the pagemap where the popup page must be created in.
	 * 
	 * @return The pagemap where the popup page must be created in
	 * @deprecated will be removed in Wicket 2.0; use {@link #getPageMap(Component)} instead
	 */
	@Deprecated
	public IPageMap getPageMap()
	{
		if (pageMapName != null)
		{
			return PageMap.forName(pageMapName);
		}
		else
		{
			throw new UnsupportedOperationException(
				"this method can only work when a page map is set. Either call"
					+ " getPageMap(Component) or set the page map");
		}
	}

	/**
	 * Gets the pagemap where the popup page must be created in.
	 * 
	 * @param callee
	 * 		Calling component
	 * @return The pagemap where the popup page must be created in
	 */
	public IPageMap getPageMap(Component callee)
	{
		if (pageMapName != null)
		{
			return PageMap.forName(pageMapName);
		}
		else
		{
			if (callee == null)
			{
				throw new IllegalArgumentException(
					"when the page map is not set, argument callee may not be null");
			}
			Page page = callee.getPage();
			if (page == null)
			{
				throw new IllegalStateException(callee +
					" is not yet set on a page; if you want to use this method " +
					"without a page map being set, argument callee must be not null " +
					"and added to a page");
			}
			return page.getPageMap();
		}
	}
}
