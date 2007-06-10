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
package org.apache.wicket.markup.html.tree;

import javax.swing.tree.TreeNode;

import org.apache.wicket.Component;
import org.apache.wicket.ResourceReference;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

/**
 * Simple panel that contains an icon next to a label.
 * 
 * @author Matej Knopp
 */
public class LabelIconPanel extends Panel
{
	private static final long serialVersionUID = 1L;

	/**
	 * Constructs the panel.
	 * 
	 * @param id
	 *            component id
	 * @param model
	 *            model that is used to access the TreeNode
	 * @param tree
	 */
	public LabelIconPanel(String id, IModel model, BaseTree tree)
	{
		super(id, model);

		addComponents(model, tree);
	}

	/**
	 * Adds the icon and content components to the panel. You can override this
	 * method if you want custom components to be added
	 * 
	 * @param model
	 *            model that can be used to retrieve the TreeNode
	 * 
	 * @param tree
	 */
	protected void addComponents(IModel model, BaseTree tree)
	{
		add(newImageComponent("icon", tree, model));
		add(newContentComponent("content", tree, model));
	}

	/**
	 * Creates the icon component for the node
	 * 
	 * @param componentId
	 * @param tree
	 * @param model
	 * @return icon image component
	 */
	protected Component newImageComponent(String componentId, final BaseTree tree,
			final IModel model)
	{
		return new Image(componentId)
		{
			private static final long serialVersionUID = 1L;

			protected ResourceReference getImageResourceReference()
			{
				return LabelIconPanel.this.getImageResourceReference(tree, (TreeNode)model
						.getObject());
			}
		};
	}

	/**
	 * Creates the content component (label in this case) for the node
	 * 
	 * @param componentId
	 * @param tree
	 * @param model
	 * @return content component
	 */
	protected Component newContentComponent(String componentId, BaseTree tree, IModel model)
	{
		return new Label(componentId, model);
	}

	/**
	 * Returns the image resource reference based on the give tree node type.
	 * 
	 * @param tree
	 * @param node
	 * @return image resource reference
	 */
	protected ResourceReference getImageResourceReference(BaseTree tree, TreeNode node)
	{
		if (node.isLeaf())
		{
			return getResourceItemLeaf(node);
		}
		else
		{
			if (tree.getTreeState().isNodeExpanded(node))
			{
				return getResourceFolderOpen(node);
			}
			else
			{
				return getResourceFolderClosed(node);
			}
		}
	}

	/**
	 * Optional method for wrapping (creating an intermediate model) for the
	 * tree node model
	 * 
	 * @param nodeModel
	 * @return wrapped model
	 */
	protected IModel wrapNodeModel(IModel nodeModel)
	{
		return nodeModel;
	}

	/**
	 * Returns resource reference for closed folder icon.
	 * @param node
	 * @return resource reference
	 */
	protected ResourceReference getResourceFolderClosed(TreeNode node)
	{
		return RESOURCE_FOLDER_CLOSED;
	}

	/**
	 * Returns resource reference for open folder icon.
	 * @param node
	 * @return resource reference
	 */
	protected ResourceReference getResourceFolderOpen(TreeNode node)
	{
		return RESOURCE_FOLDER_OPEN;
	}

	/**
	 * Returns resource reference for a leaf icon.
	 * @param node
	 * @return resource reference
	 */
	protected ResourceReference getResourceItemLeaf(TreeNode node)
	{
		return RESOURCE_ITEM;
	}

	private static final ResourceReference RESOURCE_FOLDER_OPEN = new ResourceReference(
			LabelIconPanel.class, "res/folder-open.gif");
	private static final ResourceReference RESOURCE_FOLDER_CLOSED = new ResourceReference(
			LabelIconPanel.class, "res/folder-closed.gif");
	private static final ResourceReference RESOURCE_ITEM = new ResourceReference(
			LabelIconPanel.class, "res/item.gif");
}
