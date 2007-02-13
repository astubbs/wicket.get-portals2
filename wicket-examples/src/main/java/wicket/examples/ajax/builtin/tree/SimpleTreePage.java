package wicket.examples.ajax.builtin.tree;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import wicket.extensions.markup.html.tree.AbstractTree;
import wicket.extensions.markup.html.tree.Tree;

/**
 * Page that shuws a simple tree (not a table).
 *  
 * @author Matej
 *
 */
public class SimpleTreePage extends BaseTreePage
{
	private Tree tree;

	protected AbstractTree getTree()
	{
		return tree;
	}
	
	/**
	 * Page constructor
	 *
	 */
	public SimpleTreePage()
	{
		tree = new Tree("tree", createTreeModel()) 
		{
			protected String renderNode(TreeNode node)
			{
				ModelBean bean = (ModelBean) ((DefaultMutableTreeNode)node).getUserObject();
				return bean.getProperty1();
			}
		};
		add(tree);		
		tree.getTreeState().collapseAll();
	}

}
