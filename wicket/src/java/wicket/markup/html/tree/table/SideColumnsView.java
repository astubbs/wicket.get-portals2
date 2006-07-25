package wicket.markup.html.tree.table;

import java.util.ArrayList;
import java.util.List;

import javax.swing.tree.TreeNode;

import wicket.Component;
import wicket.MarkupContainer;
import wicket.RequestCycle;
import wicket.Response;
import wicket.markup.MarkupStream;
import wicket.markup.html.WebMarkupContainer;
import wicket.markup.html.tree.table.ColumnLocation.Alignment;
import wicket.markup.html.tree.table.ColumnLocation.Unit;

/**
 * Class that renders cell of columns aligned to the left or to the right. 
 * 
 * @author Matej Knopp
 */
class SideColumnsView extends WebMarkupContainer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private TreeNode node;
	
	/**
	 * Constructor.
	 * @param parent 
	 * @param id 
	 * @param node 
	 */
	public SideColumnsView(MarkupContainer parent, String id, TreeNode node) {
		super(parent, id);
		setRenderBodyOnly(true);
		this.node = node;
	}

	private List<IColumn> columns = new ArrayList<IColumn>();
	private List<Component> components = new ArrayList<Component>();
	private List<IRenderable> renderables = new ArrayList<IRenderable>();

	/**
	 * Adds a column to be rendered.
	 * @param column 
	 * @param component 
	 * @param renderable 
	 */
	public void addColumn(IColumn column, Component component, IRenderable renderable)
	{
		if (column.isVisible())
		{
			// if the column is aligned to the left, just append it. 
			// Otherwise we prepend it, because we want columns aligned to right
			// to be rendered reverse order (because they will have set float:right
			// in css, so they will be displayed in reverse order too).
			if (column.getLocation().getAlignment() == Alignment.LEFT)
			{
				columns.add(column);
				components.add(component);
				renderables.add(renderable);
			}
			else
			{
				columns.add(0, column);
				components.add(0, component);
				renderables.add(0, renderable);
			}
		}
	}
	
	/**
	 * Renders given unit as string.
	 */
	private String renderUnit(Unit unit)
	{
		if (unit == Unit.EM)
		{
			return "em";
		}
		else if (unit == Unit.PX)
		{
			return "px";
		}
		else if (unit == Unit.PERCENT)
		{
			return "%";
		}
		else 
		{
			throw new IllegalStateException("Wrong column unit for column aligned left or right.");
		}
	}

	/**
	 * Renders width of given column as string.
	 */
	private String renderColumnWidth(IColumn column)
	{
		ColumnLocation location = column.getLocation();
		return "" + location.getSize() + renderUnit(location.getUnit());
	}

	/**
	 * Renders the float css atribute of the given column.
	 */
	private String renderColumnFloat(IColumn column)
	{
		ColumnLocation location = column.getLocation();
		if (location.getAlignment() == Alignment.LEFT)
		{
			return "left";
		}
		else if (location.getAlignment() == Alignment.RIGHT)
		{
			return "right";
		}
		else
		{
			throw new IllegalStateException("Wrong column allignment.");
		}
	}

	/**
	 * Renders content of the style attribute for the given column.
	 */
	private String renderColumnStyle(IColumn column)
	{
		return "width:" + renderColumnWidth(column) + ";float:" + renderColumnFloat(column);
	}
	
	/**
	 * Renders the columns.
	 */
	@Override
	protected void onRender(final MarkupStream markupStream)
	{
		final int markupStart = markupStream.getCurrentIndex();
		Response response = RequestCycle.get().getResponse();
	
		boolean firstLeft = true; // whether there was no left column rendered yet
		boolean rendered = false;
		
		for (int i = 0; i < columns.size(); ++i)
		{
			IColumn column = columns.get(i);
			Component component = components.get(i);
			IRenderable renderable = renderables.get(i);
			
			// write wrapping markup
			response.write("<span class=\"b_\" style=\"" + renderColumnStyle(column) + "\">");
			if (column.getLocation().getAlignment() == Alignment.LEFT && firstLeft == true)
			{
				// for the first left column we have different style class (without the left border)
				response.write("<span class=\"d_\">");
				firstLeft = false;
			}				
			else
			{
				response.write("<span class=\"c_\">");
			}
			
			if (component != null)
			{
				markupStream.setCurrentIndex(markupStart);
				component.render(markupStream);
				rendered = true;
			}
			else if (renderable != null)
			{
				renderable.render(node, response);
			}
			else
			{
				throw new IllegalStateException("Either renderable or cell component must be created for this noode");
			}
			
			response.write("</span></span>\n");
		}
		
		// if no component was rendered just advance in the markup stream 
		if (rendered == false)
		{
			markupStream.skipComponent();
		}		
	}
	
}
