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
package wicket.protocol.http;

import java.lang.ref.SoftReference;
import java.util.Map;

import wicket.Application;
import wicket.Component;
import wicket.IPageMap;
import wicket.Page;
import wicket.PageMap;
import wicket.Request;
import wicket.Session;
import wicket.session.pagemap.IPageMapEntry;
import wicket.util.concurrent.ConcurrentHashMap;
import wicket.version.IPageVersionManager;
import wicket.version.undo.Change;

/**
 * FIXME document me!
 * 
 * @author jcompagner
 */
public class SecondLevelCacheSessionStore extends HttpSessionStore
{
	/**
	 * This interface is used by the SecondLevelCacheSessionStore
	 * so that pages can be stored to a persistent layer.
	 * Implemenation should store the page that it gets under the
	 * id and versionnumber. So that every page version can be 
	 * reconstructed when asked for.
	 *    
	 * @see FilePageStore as default implementation.
	 */
	public static interface IPageStore
	{

		/**
		 * Restores a page version from the persistent layer
		 * 
		 * @param sessionId
		 * @param id
		 * @param versionNumber
		 * @return The page
		 */
		Page getPage(String sessionId, int id, int versionNumber);

		/**
		 * Removes a page from the persistent layer.
		 * 
		 * @param sessionId
		 * @param page
		 */
		void removePage(String sessionId, Page page);

		/**
		 * Stores the page to a persistent layer. The page should be stored
		 * under the id and the version number.
		 * 
		 * @param sessionId
		 * @param page
		 */
		void storePage(String sessionId, Page page);

		/**
		 * The pagestore should cleanup all the pages for that sessionid.
		 *  
		 * @param sessionId
		 */
		void unbind(String sessionId);
		
		/**
		 * This method is called when the page is accessed. A IPageStore 
		 * implemenation can block until a save of that page version is 
		 * done. So that a specifiek page version is always restoreable.
		 * 
		 * @param sessionId 
		 * @param page 
		 */
		void pageAccessed(String sessionId, Page page);

		/**
		 * 
		 */
		void destroy();

	}
	
	private static final class SecondLevelCachePageVersionManager implements IPageVersionManager
	{
		private static final long serialVersionUID = 1L;

		private int currentVersionNumber;

		private Page page;
		
		
		/**
		 * Construct.
		 * @param page
		 */
		public SecondLevelCachePageVersionManager(Page page)
		{
			this.page = page;
		}

		/**
		 * @see wicket.version.IPageVersionManager#beginVersion(boolean)
		 */
		public void beginVersion(boolean mergeVersion)
		{
			if(!mergeVersion)
			{
				currentVersionNumber++;
			}
		}

		/**
		 * @see wicket.version.IPageVersionManager#componentAdded(wicket.Component)
		 */
		public void componentAdded(Component component)
		{
		}

		/**
		 * @see wicket.version.IPageVersionManager#componentModelChanging(wicket.Component)
		 */
		public void componentModelChanging(Component component)
		{
		}

		/**
		 * @see wicket.version.IPageVersionManager#componentRemoved(wicket.Component)
		 */
		public void componentRemoved(Component component)
		{
		}

		/**
		 * @see wicket.version.IPageVersionManager#componentStateChanging(wicket.version.undo.Change)
		 */
		public void componentStateChanging(Change change)
		{
		}

		/**
		 * @see wicket.version.IPageVersionManager#endVersion(boolean)
		 */
		public void endVersion(boolean mergeVersion)
		{
			String sessionId = page.getSession().getId();
			// can we really test here for merge versions? Ajax request does change the page.
			// And the last version should be stored to disk..
			if (sessionId != null /*&& !mergeVersion*/) 
			{
				IPageStore store = ((SecondLevelCacheSessionStore)Application.get().getSessionStore()).getStore();
				store.storePage(sessionId, page);
			}
		}

		/**
		 * @see wicket.version.IPageVersionManager#expireOldestVersion()
		 */
		public void expireOldestVersion()
		{
		}

		/**
		 * @see wicket.version.IPageVersionManager#getCurrentVersionNumber()
		 */
		public int getCurrentVersionNumber()
		{
			return currentVersionNumber;
		}

		/**
		 * @see wicket.version.IPageVersionManager#getVersion(int)
		 */
		public Page getVersion(int versionNumber)
		{
			if(currentVersionNumber == versionNumber)
			{
				return page;
			}
			return null;
		}

		/**
		 * @see wicket.version.IPageVersionManager#getVersions()
		 */
		public int getVersions()
		{
			return 0;
		}
		
	}

	private static final class SecondLevelCachePageMap extends PageMap
	{
		private static final long serialVersionUID = 1L;

		private Page lastPage = null;

		/**
		 * Construct.
		 * 
		 * @param name
		 * @param session
		 */
		private SecondLevelCachePageMap(String name, Session session)
		{
			super(name, session);
		}

		public Page get(int id, int versionNumber)
		{
			String sessionId = getSession().getId();
			if (lastPage != null && lastPage.getNumericId() == id)
			{
				Page page = lastPage.getVersion(versionNumber);
				if (page != null)
				{
					// ask the page store if it is ready saving the page.
					getStore().pageAccessed(sessionId, page);
					return page;
				}
			}
			if (sessionId != null)
			{
				return getStore().getPage(sessionId, id, versionNumber);
			}
			return null;
		}

		public void put(Page page)
		{
			if (!page.isPageStateless())
			{
				String sessionId = getSession().getId();
				if (sessionId != null)
				{
					if(lastPage != page && page.getCurrentVersionNumber() == 0)
					{
						// we have to save a new page directly to the file store
						// so that this version is also recoverable.
						getStore().storePage(sessionId, page);
					}
					lastPage = page;
					dirty();
				}
			}
		}

		public void removeEntry(IPageMapEntry entry)
		{
			String sessionId = getSession().getId();
			if (sessionId != null)
			{
				getStore().removePage(sessionId, entry.getPage());
			}
		}

		private IPageStore getStore()
		{
			return ((SecondLevelCacheSessionStore)Application.get().getSessionStore()).getStore();
		}
	}

	private final IPageStore cachingStore;

	/**
	 * Construct.
	 * 
	 * @param pageStore
	 */
	public SecondLevelCacheSessionStore(final IPageStore pageStore)
	{
		this.cachingStore = new IPageStore()
		{
			private Map sessionMap = new ConcurrentHashMap();

			public Page getPage(String sessionId, int id, int versionNumber)
			{
				SoftReference sr = (SoftReference)sessionMap.get(sessionId);
				if (sr != null)
				{
					Map map = (Map)sr.get();
					if (map != null)
					{
						SoftReference sr2 = (SoftReference)map.get(Integer.toString(id));
						if (sr2 != null)
						{
							Page page = (Page)sr2.get();
							if (page != null)
							{
								page = page.getVersion(versionNumber);
							}
							if (page != null)
							{
								return page;
							}
						}
					}
				}
				return pageStore.getPage(sessionId, id, versionNumber);
			}

			public void removePage(String sessionId, Page page)
			{
				SoftReference sr = (SoftReference)sessionMap.get(sessionId);
				if (sr != null)
				{
					Map map = (Map)sr.get();
					if (map != null)
					{
						map.remove(page.getId());
					}
				}
				pageStore.removePage(sessionId, page);
			}

			public void storePage(String sessionId, Page page)
			{
				Map pageMap = null;
				SoftReference sr = (SoftReference)sessionMap.get(sessionId);
				if (sr == null || (pageMap = (Map)sr.get()) == null)
				{
					pageMap = new ConcurrentHashMap();
					sessionMap.put(sessionId, new SoftReference(pageMap));
				}
				pageMap.put(page.getId(), new SoftReference(page));
				pageStore.storePage(sessionId, page);
			}

			public void unbind(String sessionId)
			{
				sessionMap.remove(sessionId);
				pageStore.unbind(sessionId);
			}

			public void pageAccessed(String sessionId, Page page)
			{
				pageStore.pageAccessed(sessionId, page);
			}

			public void destroy()
			{
				pageStore.destroy();
			}
		};
	}

	/**
	 * @see wicket.protocol.http.HttpSessionStore#createPageMap(java.lang.String,
	 *      wicket.Session)
	 */
	public IPageMap createPageMap(String name, Session session)
	{
		return new SecondLevelCachePageMap(name, session);
	}
	
	/**
	 * @see wicket.protocol.http.HttpSessionStore#newVersionManager(wicket.Page)
	 */
	public IPageVersionManager newVersionManager(Page page)
	{
		return new SecondLevelCachePageVersionManager(page);
	}

	/**
	 * @return The store to use
	 */
	public IPageStore getStore()
	{
		return cachingStore;
	}

	/**
	 * @see wicket.session.ISessionStore#setAttribute(wicket.Request,
	 *      java.lang.String, java.lang.Object)
	 */
	public final void setAttribute(Request request, String name, Object value)
	{
		// ignore all pages, they are stored through the pagemap
		if (!(value instanceof Page))
		{
			super.setAttribute(request, name, value);
		}
	}

	/**
	 * @see wicket.protocol.http.AbstractHttpSessionStore#onUnbind(java.lang.String)
	 */
	protected void onUnbind(String sessionId)
	{
		getStore().unbind(sessionId);
	}
	
	/**
	 * @see wicket.protocol.http.AbstractHttpSessionStore#destroy()
	 */
	public void destroy()
	{
		super.destroy();
		getStore().destroy();
	}
}
