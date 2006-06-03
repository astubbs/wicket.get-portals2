package wicket.extensions.ajax.markup.html;

import wicket.MarkupContainer;
import wicket.RequestCycle;
import wicket.ajax.AbstractDefaultAjaxBehavior;
import wicket.ajax.AjaxEventBehavior;
import wicket.ajax.AjaxRequestTarget;
import wicket.markup.ComponentTag;
import wicket.markup.html.basic.Label;
import wicket.markup.html.form.TextField;
import wicket.markup.html.panel.Panel;
import wicket.model.IModel;

/**
 * A simple implementation of ajaxified edit-in-place component. Currently the
 * implementation is pretty inflexible, it is missing validator/error support.
 * It also does not allow the customization of save/cancel triggers. Maybe a
 * textarea instead of an input field would be nicer as well.
 * <p>
 * Current triggers: Save the edit if either enter is pressed or the component
 * loses focus. Cancel if esc is pressed.
 * 
 * @author Igor Vaynberg (ivaynberg)
 * 
 */
public class AjaxEditableLabel extends Panel
{
	private static final long serialVersionUID = 1L;

	/** label component */
	private final Label label;

	/** editor component */
	private final TextField editor;

	/**
	 * @see wicket.Component#Component(MarkupContainer, String)
	 */
	public AjaxEditableLabel(MarkupContainer parent, final String id)
	{
		this(parent, id, null);
	}

	/**
	 * @see wicket.Component#Component(MarkupContainer, String, IModel)
	 */
	public AjaxEditableLabel(MarkupContainer parent, final String id, IModel model)
	{
		super(parent, id);
		setOutputMarkupId(true);

		label = new Label(this, "label", model);
		label.setOutputMarkupId(true);
		label.add(new LabeAjaxBehavior("onClick"));

		editor = new TextField(this, "editor", model);
		editor.setOutputMarkupId(true);
		editor.setVisible(false);
		editor.add(new EditorAjaxBehavior());

	}

	/**
	 * 
	 * @author Igor Vaynberg (ivaynberg)
	 * 
	 */
	private final class LabeAjaxBehavior extends AjaxEventBehavior
	{
		private static final long serialVersionUID = 1L;

		private LabeAjaxBehavior(String event)
		{
			super(event);
		}

		@Override
		protected void onEvent(AjaxRequestTarget target)
		{
			label.setVisible(false);
			editor.setVisible(true);
			target.addComponent(AjaxEditableLabel.this);
			// put focus on the textfield and stupid explorer hack to move the
			// caret to the end
			target.addJavascript("{ var el=wicketGet('" + editor.getMarkupId() + "');"
					+ "  el.focus(); " + "  if (el.createTextRange) { "
					+ "     var v = el.value; var r = el.createTextRange(); "
					+ "     r.moveStart('character', v.length); r.select(); } }");
		}
	}

	/**
	 * 
	 * @author Igor Vaynberg (ivaynberg)
	 * 
	 */
	private class EditorAjaxBehavior extends AbstractDefaultAjaxBehavior
	{

		private static final long serialVersionUID = 1L;

		/**
		 * Constructor
		 */
		public EditorAjaxBehavior()
		{
		}

		@Override
		protected void onComponentTag(ComponentTag tag)
		{
			super.onComponentTag(tag);
			final String saveCall = "{wicketAjaxGet('" + getCallbackUrl()
					+ "&save=true&'+this.name+'='+wicketEncode(this.value)); return true;}";

			final String cancelCall = "{wicketAjaxGet('" + getCallbackUrl()
					+ "&save=false'); return false;}";


			final String keypress = "var kc=wicketKeyCode(event); if (kc==27) " + cancelCall
					+ " else if (kc!=13) { return true; } else " + saveCall;

			tag.put("onblur", saveCall);
			tag.put("onkeypress", keypress);

		}

		@Override
		protected void respond(AjaxRequestTarget target)
		{
			RequestCycle rc = RequestCycle.get();
			boolean save = Boolean.valueOf(rc.getRequest().getParameter("save")).booleanValue();
			if (save)
			{
				editor.processInput();
			}
			label.setVisible(true);
			editor.setVisible(false);
			target.addComponent(AjaxEditableLabel.this);
		}

	}
}
