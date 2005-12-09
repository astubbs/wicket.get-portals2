package wicket.extensions.markup.html.repeater.data.table.filter;

import wicket.markup.html.form.Button;
import wicket.markup.html.panel.Panel;
import wicket.model.IModel;
import wicket.model.Model;

/**
 * Filter component that generates a 'go' button that when pressed submits the
 * filter form
 * 
 * @author Igor Vaynberg (ivaynberg)
 * 
 */
public class GoFilter extends Panel
{
	private static final long serialVersionUID = 1L;

	protected static final IModel defaultGoModel = new Model("filter");

	private final Button go;

	/**
	 * Constructor
	 * 
	 * This constructor will use the default model for the button's text
	 * 
	 * @param id
	 *            component id
	 */
	public GoFilter(String id)
	{
		this(id, defaultGoModel);
	}

	/**
	 * Constructor
	 * 
	 * @param id
	 *            component id
	 * @param goModel
	 *            model for the button's text
	 */
	public GoFilter(String id, IModel goModel)
	{
		super(id);

		go = new Button("go", goModel)
		{
			private static final long serialVersionUID = 1L;

			protected void onSubmit()
			{
				onGoSubmit(this);
			}
		};

		add(go);
	}

	protected Button getGoButton()
	{
		return go;
	}

	/**
	 * This method can be overridden by subclasses to provide non-standard
	 * behaviour for the 'go' button.
	 * 
	 * @param button
	 *            the 'go' button, can be used to get to the Form object and
	 *            through that to the filter state object by retrieving the
	 *            form's model object
	 * 
	 */
	protected void onGoSubmit(Button button)
	{
	}

}
