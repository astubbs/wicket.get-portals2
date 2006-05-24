package wicket.extensions.markup.html.form.palette.component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import wicket.MarkupContainer;
import wicket.extensions.markup.html.form.palette.Palette;
import wicket.markup.html.form.HiddenField;
import wicket.markup.html.form.IChoiceRenderer;
import wicket.model.Model;
import wicket.util.string.Strings;

/**
 * Component to keep track of selections on the html side. Also used for
 * encoding and decoding those selections between html and java.
 * 
 * @author Igor Vaynberg ( ivaynberg )
 */
public class Recorder extends HiddenField
{
	private static final long serialVersionUID = 1L;

	private static final String[] EMPTY_IDS=new String[0];
	
	/** conviniently maintained array of selected ids */
	private String[] ids;

	/** parent palette object */
	private Palette palette;

	/**
	 * @return parent Palette object
	 */
	public Palette getPalette()
	{
		return palette;
	}

	/**
	 * @param id
	 *            component id
	 * @param palette
	 *            parent palette object
	 */
	public Recorder(MarkupContainer parent,final String id, Palette palette)
	{
		super(parent,id);
		this.palette = palette;

		// construct the model string based on selection collection
		IChoiceRenderer renderer = getPalette().getChoiceRenderer();
		StringBuffer modelStringBuffer = new StringBuffer();
		Iterator selection = getPalette().getModelCollection().iterator();
		while (selection.hasNext())
		{
			modelStringBuffer.append(renderer.getIdValue(selection.next(), 0));
			if (selection.hasNext())
			{
				modelStringBuffer.append(",");
			}
		}

		// set model and update ids array
		String modelString = modelStringBuffer.toString();
		setModel(new Model(modelString));
		updateIds(modelString);
	}

	
	protected void onValid()
	{
		super.onValid();
		updateIds();
	}

	/**
	 * @return iterator over selected choices
	 */
	public Iterator getSelectedChoices()
	{
		IChoiceRenderer renderer = getPalette().getChoiceRenderer();

		if (ids.length == 0)
		{
			return Collections.EMPTY_LIST.iterator();
		}

		List selected = new ArrayList(ids.length);
		for (int i = 0; i < ids.length; i++)
		{
			Iterator it = getPalette().getChoices().iterator();
			while (it.hasNext())
			{
				final Object choice = it.next();
				if (renderer.getIdValue(choice, 0).equals(ids[i]))
				{
					selected.add(choice);
					break;
				}
			}
		}
		return selected.iterator();
	}

	/**
	 * @return iterator over unselected choices
	 */
	public Iterator getUnselectedChoices()
	{
		IChoiceRenderer renderer = getPalette().getChoiceRenderer();
		Collection choices = getPalette().getChoices();

		if (choices.size() - ids.length == 0)
		{
			return Collections.EMPTY_LIST.iterator();
		}

		List unselected = new ArrayList(Math.max(1, choices.size() - ids.length));
		Iterator it = choices.iterator();
		while (it.hasNext())
		{
			final Object choice = it.next();
			final String choiceId = renderer.getIdValue(choice, 0);
			boolean selected = false;
			for (int i = 0; i < ids.length; i++)
			{
				if (ids[i].equals(choiceId))
				{
					selected = true;
					break;
				}
			}
			if (!selected)
			{
				unselected.add(choice);
			}
		}
		return unselected.iterator();
	}


	protected void onInvalid()
	{
		super.onInvalid();
		updateIds();
	}

	private void updateIds()
	{
		updateIds(getValue());
	}

	private void updateIds(String value) {
		if (Strings.isEmpty(value)) {
			ids=EMPTY_IDS;
		} else {
			ids = value.split(",");
		}
	}

}
