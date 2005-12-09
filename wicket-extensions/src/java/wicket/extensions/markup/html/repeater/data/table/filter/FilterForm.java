package wicket.extensions.markup.html.repeater.data.table.filter;

import wicket.AbstractBehaviour;
import wicket.Component;
import wicket.markup.ComponentTag;
import wicket.markup.html.form.Form;
import wicket.markup.html.form.FormComponent;
import wicket.markup.html.form.HiddenField;
import wicket.model.AbstractModel;
import wicket.model.IModel;
import wicket.model.Model;

/**
 * A form with filter-related special functionality for its form components.
 * 
 * @author igor
 * 
 */
public class FilterForm extends Form
{
	private static final long serialVersionUID = 1L;

	private final HiddenField hidden;
	private final IFilterStateLocator locator;

	/**
	 * @param id
	 *            component id
	 * @param locator
	 *            filter state locator
	 */
	public FilterForm(String id, IFilterStateLocator locator)
	{
		super(id, new FilterStateModel(locator));

		this.locator = locator;

		hidden = new HiddenField("focus-tracker", new Model());

		hidden.add(new AbstractBehaviour()
		{
			private static final long serialVersionUID = 1L;

			public void onComponentTag(Component component, ComponentTag tag)
			{
				tag.put("id", getFocusTrackerFieldCssId());
				super.onComponentTag(component, tag);
			}
		});

		add(hidden);
	}

	/**
	 * @return css id of the hidden form input that keeps track of the focused
	 *         input field
	 */
	public final String getFocusTrackerFieldCssId()
	{
		return hidden.getPageRelativePath();
	}

	/**
	 * @return IFilterStateLocator passed to this form
	 */
	public final IFilterStateLocator getStateLocator()
	{
		return locator;
	}

	/**
	 * Adds behaviour to the form component to allow this form to keep track of
	 * the component's focus which will be restored after a form submit.
	 * 
	 * @param fc
	 *            form component
	 */
	public final void enableFocusTracking(FormComponent fc)
	{
		fc.add(new AbstractBehaviour()
		{
			private static final long serialVersionUID = 1L;

			public void onComponentTag(Component component, ComponentTag tag)
			{
				tag.put("id", component.getPageRelativePath());
				tag.put("onfocus", getFocusTrackingHandler(component));
				super.onComponentTag(component, tag);
			}
		});
	}

	/**
	 * Returns the javascript focus handler necessary to notify the form of
	 * focus tracking changes on the component
	 * 
	 * Useful when components want to participate in focus tracking but want to
	 * add the handler their own way.
	 * 
	 * A unique css id is required on the form component for focus tracking to
	 * work.
	 * 
	 * @param component
	 *            component to 
	 * @return the javascript focus handler necessary to notify the form of
	 *         focus tracking changes on the component
	 */
	public final String getFocusTrackingHandler(Component component)
	{
		return ("_filter_focus(this, '" + getFocusTrackerFieldCssId() + "');");
	}


	/**
	 * Model that uses filter state locator as a passthrough for model objects
	 * 
	 * @author Igor Vaynberg (ivaynberg)
	 * 
	 */
	private static class FilterStateModel extends AbstractModel
	{
		private static final long serialVersionUID = 1L;

		private IFilterStateLocator locator;

		/**
		 * Constructor
		 * 
		 * @param locator
		 *            IFilterStateLocator implementation used to provide model
		 *            object for this model
		 */
		public FilterStateModel(IFilterStateLocator locator)
		{
			if (locator == null)
			{
				throw new IllegalArgumentException("argument [locator] cannot be null");
			}
			this.locator = locator;
		}

		/**
		 * @see wicket.model.IModel#getNestedModel()
		 */
		public IModel getNestedModel()
		{
			return null;
		}

		/**
		 * @see wicket.model.IModel#getObject(wicket.Component)
		 */
		public Object getObject(Component component)
		{
			return locator.getFilterState();
		}

		/**
		 * @see wicket.model.IModel#setObject(wicket.Component,
		 *      java.lang.Object)
		 */
		public void setObject(Component component, Object object)
		{
			locator.setFilterState(object);
		}

	}

}
