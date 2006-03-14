package wicket.extensions.ajax.markup.html;

import wicket.ajax.IAjaxIndicatorAware;
import wicket.ajax.markup.html.AjaxLink;
import wicket.model.IModel;

/**
 * A variant of the {@link AjaxLink} that displays a busy indicator while the
 * ajax request is in progress.
 * 
 * @since 1.2
 * 
 * @author Igor Vaynberg (ivaynberg)
 * 
 */
public abstract class IndicatingAjaxLink extends AjaxLink implements IAjaxIndicatorAware
{
	private final WicketAjaxIndicatorAppender indicatorAppender = new WicketAjaxIndicatorAppender();

	/**
	 * Constructor
	 * @param id
	 */
	public IndicatingAjaxLink(String id)
	{
		this(id, null);
	}

	/**
	 * Constructor
	 * @param id
	 * @param model
	 */
	public IndicatingAjaxLink(String id, IModel model)
	{
		super(id, model);
		add(indicatorAppender);
	}

	/**
	 * @see wicket.ajax.IAjaxIndicatorAware#getAjaxIndicatorMarkupId()
	 */
	public String getAjaxIndicatorMarkupId()
	{
		return indicatorAppender.getMarkupId();
	}

}
