package wicket.xtree.table;

import java.io.Serializable;

import javax.swing.tree.TreeNode;

import wicket.Component;
import wicket.MarkupContainer;

/**
 * Interface that represents a column in {@link TreeTable}.
 * 
 * @author Matej Knopp
 */
public interface IColumn extends Serializable {

	/**
	 * Sets the tree table this cell belongs to. This function is guaranteed 
	 * to be called before any other function. The treeTable instance
	 * is fully initialized.
	 *  
	 */
	public void setTreeTable(TreeTable treeTable);
	
	/**
	 * Returns a location of this columns. Location specifies how is column
	 * aligned and what is it's size.
	 * <p>
	 * In case location of a column changes, it is necessary to call the 
	 * <code>invalidateAll</code> methods on the {@link TreeTable} to prevent
	 * incorrect rendering. 
	 */
	public ColumnLocation getLocation();
	
	/**
	 * Returns, whether the column is visible. 
	 * <p>
	 * In case the visibility changes, it is necessary to call the 
	 * <code>invalidateAll</code> methods on the {@link TreeTable} to prevent
	 * incorrect rendering. 
	 */
	public boolean isVisible();
	
	/**
	 * Creates the header element for this column. In most situations this will
	 * be just a simple label showing column title.
	 */
	public Component createHeader(MarkupContainer<?> parent, String id);	 
	
	/**
	 * Creates the {@link IRenderable} instance for given node. {@link IRenderable}
	 * can be used as lightweight alternative to regular Component for cells, that
	 * don't require user interaction (just display data).
	 * <p>
	 * If this method returns null, {@link IColumn#createCell(MarkupContainer, String, TreeNode, int)}
	 * is used to popuplate the cell.
	 * 
	 * @param treeTable 
	 * 			TreeTable to which the cell belongs
	 * 
	 * @param node
	 * 			TreeNode for the cell
	 * 
	 * @param level
	 * 			Convenience parameter that indicates how deep the node is in hierarchy
	 */
	public IRenderable createCell(TreeNode node, int level);
	
	/**
	 * This method is used to populate the cell for given node in case when
	 * {@link IColumn#createCell(TreeTable, TreeNode, int)} returned null.
	 * 
	 * @param parent
	 * 			The parent to which the cell must be added. Can also be used to find
	 * 			the TreeTable instance (using <code>parent.findParent(TreeTable.cass)</code>)
	 * 
	 * @param node
	 * 			TreeNode for the cell
	 * 
	 * @param level
	 * 			Convenience parameter that indicates how deep the node is in hierarchy
	 */
	public Component createCell(MarkupContainer<?> parent, String id, TreeNode node, int level);		
	
	/**
	 * Returns the span for this cell. This method is called only for cells that are aligned in
	 * the middle. 
	 * <p> 
	 * The returned value implicates, over how many cells the cell in this column (in row
	 * determined by node) should span. This is analogical to colspan property of html element td. 
	 */
	public int getSpan(TreeNode node);
}
