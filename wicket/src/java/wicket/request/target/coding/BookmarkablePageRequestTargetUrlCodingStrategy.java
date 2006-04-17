/*
 * $Id: BookmarkablePageRequestTargetUrlCodingStrategy.java,v 1.1 2005/12/10 21:28:56 eelco12
 * Exp $ $Revision$ $Date$
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
package wicket.request.target.coding;

import wicket.IRequestTarget;
import wicket.PageParameters;
import wicket.protocol.http.request.WebRequestCodingStrategy;
import wicket.request.RequestParameters;
import wicket.request.target.component.BookmarkablePageRequestTarget;
import wicket.request.target.component.IBookmarkablePageRequestTarget;
import wicket.util.string.AppendingStringBuffer;

/**
 * Encodes and decodes mounts for a single bookmarkable page class.
 * 
 * @author Eelco Hillenius
 */
public class BookmarkablePageRequestTargetUrlCodingStrategy extends AbstractRequestTargetUrlCodingStrategy
{
	/** bookmarkable page class. */
	private final Class bookmarkablePageClass;

	/** page map name. */
	private final String pageMapName;

	/**
	 * Construct.
	 * 
	 * @param mountPath
	 *            the mount path
	 * @param bookmarkablePageClass
	 *            the class of the bookmarkable page
	 * @param pageMapName
	 *            the page map name if any
	 */
	public BookmarkablePageRequestTargetUrlCodingStrategy(final String mountPath,
			final Class bookmarkablePageClass, String pageMapName)
	{
		super(mountPath);

		if (bookmarkablePageClass == null)
		{
			throw new IllegalArgumentException("Argument bookmarkablePageClass must be not null");
		}

		this.bookmarkablePageClass = bookmarkablePageClass;
		this.pageMapName = pageMapName;
	}

	/**
	 * @see wicket.request.target.coding.IRequestTargetUrlCodingStrategy#encode(wicket.IRequestTarget)
	 */
	public final CharSequence encode(final IRequestTarget requestTarget)
	{
		if (!(requestTarget instanceof IBookmarkablePageRequestTarget))
		{
			throw new IllegalArgumentException("This encoder can only be used with " +
        "instances of "	+ IBookmarkablePageRequestTarget.class.getName());
		}
		final AppendingStringBuffer url = new AppendingStringBuffer(40);
		url.append(getMountPath());
		final IBookmarkablePageRequestTarget target = (IBookmarkablePageRequestTarget)requestTarget;

		PageParameters pageParameters = target.getPageParameters();
		if(pageMapName != null)
		{
			if(pageParameters == null)
			{
				pageParameters = new PageParameters();
			}
			pageParameters.put(WebRequestCodingStrategy.PAGEMAP, pageMapName);
		}
		else if(target.getPageMapName() != null)
		{
			pageParameters.put(WebRequestCodingStrategy.PAGEMAP, target.getPageMapName());
		}
		appendParameters(url, pageParameters);
		return url;
	}

	/**
	 * @see wicket.request.target.coding.IRequestTargetUrlCodingStrategy#decode(wicket.request.RequestParameters)
	 */
	public IRequestTarget decode(RequestParameters requestParameters)
	{
		final String parametersFragment = requestParameters.getPath().substring(getMountPath().length());
		final PageParameters parameters = new PageParameters(decodeParameters(parametersFragment, requestParameters.getParameters()));
		final String pageMapName = (String)parameters.remove(WebRequestCodingStrategy.PAGEMAP);
		requestParameters.setPageMapName(pageMapName);

		final BookmarkablePageRequestTarget target = new BookmarkablePageRequestTarget(pageMapName,
				bookmarkablePageClass, parameters);
		return target;
	}

	/**
	 * @see wicket.request.target.coding.IRequestTargetUrlCodingStrategy#matches(wicket.IRequestTarget)
	 */
	public boolean matches(IRequestTarget requestTarget)
	{
		if (requestTarget instanceof IBookmarkablePageRequestTarget)
		{
			IBookmarkablePageRequestTarget target = (IBookmarkablePageRequestTarget)requestTarget;
			if (bookmarkablePageClass.equals(target.getPageClass()))
			{
				if (this.pageMapName == null)
				{
					return true;
				}
				else
				{
					return this.pageMapName.equals(target.getPageMapName());
				}
			}
		}
		return false;
	}
}
