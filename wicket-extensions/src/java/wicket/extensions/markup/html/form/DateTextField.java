package wicket.extensions.markup.html.form;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import wicket.MarkupContainer;
import wicket.markup.html.form.TextField;
import wicket.model.IModel;
import wicket.util.convert.IConverter;
import wicket.util.convert.converters.DateConverter;

/**
 * A TextField that is mapped to a <code>java.util.Date</code> object.
 * 
 * If you provide a <code>SimpleDateFormat</code> pattern, it will both parse
 * and validate the text field according to it.
 * 
 * If you don't, it is the same as creating a <code>TextField</code> with
 * <code>java.util.Date</code> as it's type (it will get the pattern
 * from the user's locale)
 * 
 * @author Stefan Kanev
 *
 */
public class DateTextField extends TextField
{

	private static final long serialVersionUID = 1L;

	/**
	 * The date pattern of the text field
	 */
	private SimpleDateFormat dateFormat = null;
	
	/**
	 * The converter for the TextField
	 */
	private IConverter converter = null;
	
	/**
	 * Creates a new DateTextField, without a specified pattern. This
	 * is the same as calling <code>new TextField(id, Date.class)</code>
	 *  
	 * @param id The id of the text field
	 * 
	 * @see wicket.markup.html.form.TextField
	 */
	public DateTextField(MarkupContainer parent,final String id)
	{
		super(parent,id, Date.class);
	}

	/**
	 * Creates a new DateTextField, without a specified pattern. This
	 * is the same as calling <code>new TextField(id, object, Date.class)</code>
	 * 
	 * @param id The id of the text field
	 * @param object The model
	 * 
	 * @see wicket.markup.html.form.TextField
	 */
	public DateTextField(MarkupContainer parent,final String id, IModel object)
	{
		super(parent,id, object, Date.class);
	}

	/**
	 * Creates a new DateTextField bound with a specific 
	 * <code>SimpleDateFormat</code> pattern.
	 * 
	 * @param id The id of the text field
	 * @param datePattern A <code>SimpleDateFormat</code> pattern
	 * 
	 * @see wicket.markup.html.form.TextField
	 */
	public DateTextField(MarkupContainer parent,final String id, String datePattern)
	{
		super(parent,id, Date.class);
		this.dateFormat = new SimpleDateFormat(datePattern);
		this.converter = new DateConverter()
		{
			private static final long serialVersionUID = 1L;

			/**
			 * @see wicket.util.convert.converters.DateConverter#getDateFormat(java.util.Locale)
			 */
			public DateFormat getDateFormat(Locale locale)
			{
				return dateFormat;
			}
		};
	}
	
	/**
	 * Creates a new DateTextField bound with a specific 
	 * <code>SimpleDateFormat</code> pattern.
	 * 
	 * @param id The id of the text field
	 * @param object The model
	 * @param datePattern A <code>SimpleDateFormat</code> pattern
	 * 
	 * @see wicket.markup.html.form.TextField
	 */
	public DateTextField(MarkupContainer parent,final String id, IModel object, String datePattern)
	{
		this(parent,id,datePattern);
		setModel(object);
	}

	/**
	 * Returns the default converter if created without pattern; otherwise it
	 * returns a pattern-specific converter.
	 * 
	 * @param type The type for wich a converter must be get. 
	 * 
	 * @return A pattern-specific converter
	 * 
	 * @see wicket.markup.html.form.TextField
	 */
	public IConverter getConverter(Class type)
	{
		if (converter == null) 
		{ 
			return super.getConverter(type);
		} else 
		{
			return converter;
		}
	}
}
