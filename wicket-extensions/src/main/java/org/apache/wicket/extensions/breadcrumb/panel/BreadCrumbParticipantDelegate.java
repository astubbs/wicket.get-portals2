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
/**
 * 
 */
package org.apache.wicket.extensions.breadcrumb.panel;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.Component.IVisitor;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbParticipant;
import org.apache.wicket.markup.html.panel.Panel;

/**
 * Base implementation for {@link Panel}/ {@link Component} based {@link IBreadCrumbParticipant}
 * that decouples the implementation from the actual panel class.
 * 
 * @author eelcohillenius
 */
public abstract class BreadCrumbParticipantDelegate implements IBreadCrumbParticipant
{
	private static final long serialVersionUID = 1L;

	private final Component component;

	/**
	 * Construct.
	 * 
	 * @param component
	 */
	public BreadCrumbParticipantDelegate(Component component)
	{
		if (component == null)
		{
			throw new IllegalArgumentException("component must be not null");
		}
		this.component = component;
	}

	/**
	 * @see org.apache.wicket.extensions.breadcrumb.IBreadCrumbParticipant#getComponent()
	 */
	public Component getComponent()
	{
		return component;
	}

	/**
	 * If the previous participant is not null (and a component, which it should be), replace that
	 * component on it's parent with this one.
	 * 
	 * @see org.apache.wicket.extensions.breadcrumb.IBreadCrumbParticipant#onActivate(org.apache.wicket.extensions.breadcrumb.IBreadCrumbParticipant)
	 */
	public void onActivate(IBreadCrumbParticipant previous)
	{
		if (previous != null)
		{
			MarkupContainer parent = previous.getComponent().getParent();
			if (parent != null)
			{
				final String thisId = component.getId();
				if (parent.get(thisId) != null)
				{
					parent.replace(component);
				}
				else
				{
					// try to search downwards to match the id
					// NOTE unfortunately, we can't rely on the path pre 2.0
					Component c = (Component)parent.visitChildren(new IVisitor<Component>()
					{
						public Object component(Component component)
						{
							if (component.getId().equals(thisId))
							{
								return component;
							}
							return IVisitor.CONTINUE_TRAVERSAL;
						}
					});
					if (c == null)
					{
						// not found... do a reverse search (upwards)
						c = (Component)parent.visitParents(Component.class,
							new IVisitor<Component>()
							{
								public Object component(Component component)
								{
									if (component.getId().equals(thisId))
									{
										return component;
									}
									return IVisitor.CONTINUE_TRAVERSAL;
								}
							});
					}

					// replace if found
					if (c != null)
					{
						c.replaceWith(component);
					}
				}
			}
		}
		else if (component.getParent() != null)
		{
			component.getParent().replace(component);
		}
	}
}
