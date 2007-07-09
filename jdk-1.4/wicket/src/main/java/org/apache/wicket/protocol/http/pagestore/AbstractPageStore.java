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
package org.apache.wicket.protocol.http.pagestore;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.wicket.Page;
import org.apache.wicket.Session;
import org.apache.wicket.protocol.http.SecondLevelCacheSessionStore;
import org.apache.wicket.protocol.http.SecondLevelCacheSessionStore.IPageStore;
import org.apache.wicket.session.pagemap.IPageMapEntry;
import org.apache.wicket.util.collections.IntHashMap;
import org.apache.wicket.util.lang.Objects;

/**
 * Abstract page store that implements the serialization logic so that
 * the subclasses can concentrate on actual storing of serialized 
 * page instances.
 * 
 * @author Matej Knopp
 */
public abstract class AbstractPageStore implements IPageStore
{

	protected static class SerializedPage
	{

		private final int pageId;
		private final String pageMapName;
		private final int versionNumber;
		private final int ajaxVersionNumber;
		private byte[] data;

		/**
		 * Construct.
		 * 
		 * @param pageId
		 * @param pageMapName
		 * @param versionNumber
		 * @param ajaxVersionNumber
		 * @param data
		 */
		public SerializedPage(int pageId, String pageMapName, int versionNumber,
				int ajaxVersionNumber, byte[] data)
		{
			this.pageId = pageId;
			this.pageMapName = pageMapName;
			this.versionNumber = versionNumber;
			this.ajaxVersionNumber = ajaxVersionNumber;
			this.data = data;
		}
		
		/**
		 * Construct.
		 * @param page
		 */
		public SerializedPage(Page page)
		{
			this.pageId = page.getNumericId();
			this.pageMapName = page.getPageMapName();
			this.versionNumber = page.getCurrentVersionNumber();
			this.ajaxVersionNumber = page.getAjaxVersionNumber();
		}

		/**
		 * @return
		 */
		public int getPageId()
		{
			return pageId;
		}

		/**
		 * @return
		 */
		public String getPageMapName()
		{
			return pageMapName;
		}

		/**
		 * @return
		 */
		public int getVersionNumber()
		{
			return versionNumber;
		}

		/**
		 * @return
		 */
		public int getAjaxVersionNumber()
		{
			return ajaxVersionNumber;
		}

		/**
		 * @return
		 */
		public byte[] getData()
		{
			return data;
		}

		/**
		 * @param data
		 */
		public void setData(byte[] data)
		{
			this.data = data;
		}
		
		public int hashCode()
		{
			return pageId * 1931 + versionNumber * 13 + ajaxVersionNumber * 301 +
					(pageMapName != null ? pageMapName.hashCode() : 0);
		}

		public boolean equals(Object obj)
		{
			if (this == obj)
				return true;
			
			if (obj instanceof SerializedPage == false)
				return false;
			
			SerializedPage rhs = (SerializedPage) obj;
			
			return pageId == rhs.pageId &&
			       (pageMapName == rhs.pageMapName || (pageMapName != null && pageMapName.equals(rhs.pageMapName))) &&
			       versionNumber == rhs.versionNumber &&
			       ajaxVersionNumber == rhs.ajaxVersionNumber;
		}
	};
	
	protected List/* SerializedPage */ serializePage(Page page) {
		final List result = new ArrayList();
		
		SerializedPage initialPage = new SerializedPage(page);
		result.add(initialPage);
		
		PageSerializer serializer = new PageSerializer(initialPage) {
			protected void onPageSerialized(SerializedPage page)
			{
				result.add(page);
			}
		};
		
		Page.serializer.set(serializer);
		
		try
		{
			initialPage.setData(Objects.objectToByteArray(page.getPageMapEntry()));
		} finally {
			Page.serializer.set(null);
		}
		
		return result;
	}
	
	protected Page deserializePage(byte[] data, int versionNumber) {
		boolean set = Page.serializer.get() == null;
		Page page = null;
		try
		{
			if (set)
			{
				Page.serializer.set(new PageSerializer(null));
			}
			IPageMapEntry entry = (IPageMapEntry)Objects.byteArrayToObject(data);
			if (entry != null)
			{
				page = entry.getPage();
				page = page.getVersion(versionNumber);
			}
		}
		finally
		{
			if (set)
			{
				Page.serializer.set(null);
			}
		}
		return page;
	}
	
	private static class PageSerializer implements Page.IPageSerializer
	{
		private SerializedPage current;

		private List previous = new ArrayList();
		private List completed = new ArrayList();


		protected void onPageSerialized(SerializedPage page) {
			
		}
		
		/**
		 * Construct.
		 * 
		 * @param page
		 */
		public PageSerializer(SerializedPage page)
		{
			this.current = page;
		}

		/**
		 * @throws IOException
		 * @see org.apache.wicket.Page.IPageSerializer#serializePage(org.apache.wicket.Page)
		 */
		public void serializePage(Page page, ObjectOutputStream stream) throws IOException
		{
			if (current.getPageId() == page.getNumericId())
			{
				stream.writeBoolean(false);
				stream.defaultWriteObject();
				return;
			}
			SerializedPage spk = new SerializedPage(page);
			if (!completed.contains(spk) && !previous.contains(spk))
			{
				previous.add(current);
				current = spk;
				byte[] bytes = Objects.objectToByteArray(page.getPageMapEntry());
				current.setData(bytes);
				onPageSerialized(current);
				completed.add(current);
				current = (SerializedPage)previous.remove(previous.size() - 1);
			}
			stream.writeBoolean(true);
			stream.writeObject(new PageHolder(page));
		}

		public Page deserializePage(int id, String pageMapName, Page page, ObjectInputStream stream)
				throws IOException, ClassNotFoundException
		{
			HashMap pageMaps = (HashMap)SecondLevelCacheSessionStore.getUsedPages().get();
			if (pageMaps == null)
			{
				pageMaps = new HashMap();
				SecondLevelCacheSessionStore.getUsedPages().set(pageMaps);
			}
			IntHashMap pages = (IntHashMap)pageMaps.get(pageMapName);
			if (pages == null)
			{
				pages = new IntHashMap();
				pageMaps.put(pageMapName, pages);
			}
			pages.put(id, page);
			boolean b = stream.readBoolean();
			if (b == false)
			{
				stream.defaultReadObject();
				return page;
			}
			else
			{
				// the object will resolve to a Page (probably PageHolder)
				return (Page)stream.readObject();
			}
		}
	}

	
	/**
	 * Class that resolves to page instance
	 */
	private static class PageHolder implements Serializable
	{
		private static final long serialVersionUID = 1L;

		private final int pageid;
		private final String pagemap;

		PageHolder(Page page)
		{
			this.pageid = page.getNumericId();
			this.pagemap = page.getPageMapName();
		}

		protected Object readResolve() throws ObjectStreamException
		{
			return Session.get().getPage(pagemap, Integer.toString(pageid), -1);
		}
	}

}
