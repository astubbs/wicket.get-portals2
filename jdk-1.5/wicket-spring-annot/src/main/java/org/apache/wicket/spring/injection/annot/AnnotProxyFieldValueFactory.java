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
package org.apache.wicket.spring.injection.annot;

import java.lang.reflect.Field;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.wicket.injection.IFieldValueFactory;
import org.apache.wicket.proxy.LazyInitProxyFactory;
import org.apache.wicket.spring.ISpringContextLocator;
import org.apache.wicket.spring.SpringBeanLocator;

/**
 * {@link IFieldValueFactory} that uses {@link LazyInitProxyFactory} to create
 * proxies for Spring dependencies based on the {@link SpringBean} annotation
 * applied to a field. This class is usually used by the
 * {@link AnnotSpringInjector} to inject objects with lazy init proxies.
 * However, this class can be used on its own to create proxies for any field
 * decorated with a {@link SpringBean} annotation.
 * <p>
 * Example:
 *
 * <pre>
 * IFieldValueFactory factory = new AnnotProxyFieldValueFactory(contextLocator);
 * field = obj.getClass().getDeclaredField(&quot;dependency&quot;);
 * IDependency dependency = (IDependency) factory.getFieldValue(field, obj);
 * </pre>
 *
 * In the example above the <code>dependency</code> object returned is a lazy
 * init proxy that will look up the actual IDependency bean from spring context
 * upon first access to one of the methods.
 * <p>
 * This class will also cache any produced proxies so that the same proxy is
 * always returned for the same spring dependency. This helps cut down on
 * session size beacause proxies for the same dependency will not be serialized
 * twice.
 *
 * @see LazyInitProxyFactory
 * @see SpringBean
 * @see SpringBeanLocator
 *
 * @author Igor Vaynberg (ivaynberg)
 * @author Istvan Devai
 *
 */
public class AnnotProxyFieldValueFactory implements IFieldValueFactory {
	private ISpringContextLocator contextLocator;
	private boolean failFast = true;

	private final ConcurrentHashMap<SpringBeanLocator, Object> cache = new ConcurrentHashMap<SpringBeanLocator, Object>();

	/**
	 * @param contextLocator
	 *            spring context locator
	 */
	public AnnotProxyFieldValueFactory(ISpringContextLocator contextLocator) {
		if (contextLocator == null) {
			throw new IllegalArgumentException(
					"[contextLocator] argument cannot be null");
		}
		this.contextLocator = contextLocator;
	}

	/**
	 * @see org.apache.wicket.injection.IFieldValueFactory#getFieldValue(java.lang.reflect.Field,
	 *      java.lang.Object)
	 */
	public Object getFieldValue(Field field, Object fieldOwner) {

		if (field.isAnnotationPresent(SpringBean.class)) {
			SpringBean annot = field.getAnnotation(SpringBean.class);
			SpringBeanLocator locator = new SpringBeanLocator(annot.name(),
					field.getType(), contextLocator);

			// only check the cache if the bean is a singleton
			if (locator.isSingletonBean() && cache.containsKey(locator)) {
				return cache.get(locator);
			}

			if (failFast) {
				testLocator(locator, fieldOwner, field);
			}

			Object proxy = LazyInitProxyFactory.createProxy(field.getType(),
					locator);
			// only put the proxy into the cache if the bean is a singleton
			if (locator.isSingletonBean()) {
				cache.put(locator, proxy);
			}
			return proxy;
		} else {
			return null;
		}
	}

	/**
	 * Tests if the locator can retrieve the bean it is responsible for.
	 *
	 * @param locator
	 * @param fieldOwner
	 * @param field
	 */
	private void testLocator(SpringBeanLocator locator, Object fieldOwner, Field field)
	{
		try
		{
			locator.locateProxyTarget();
		}
		catch (Throwable e)
		{
			String errorMessage = "Could not locate spring bean of class [["
					+ locator.getBeanType().getName() + "]] ";
			if (locator.getBeanName() != null && locator.getBeanName().length() > 0)
			{
				errorMessage += "and id [[" + locator.getBeanName() + "]] ";
			}
			errorMessage += "needed in class [["
					+ fieldOwner.getClass().getName() + "]] field [[" + field.getName()
					+ "]]";
			throw new RuntimeException(errorMessage, e);
		}
	}

	/**
	 * @param failFast true if the locator fails if a bean can't be located
	 */
	public void setFailFast(boolean failFast)
	{
		this.failFast = failFast;
	}

	/**
	 * @see org.apache.wicket.injection.IFieldValueFactory#supportsField(java.lang.reflect.Field)
	 */
	public boolean supportsField(Field field) {
		return field.isAnnotationPresent(SpringBean.class);
	}
}
