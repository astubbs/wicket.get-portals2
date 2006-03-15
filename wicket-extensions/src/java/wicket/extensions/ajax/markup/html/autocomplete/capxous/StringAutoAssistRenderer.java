package wicket.extensions.ajax.markup.html.autocomplete.capxous;

import wicket.Response;

/**
 * An renderer that assumes that assist objects are {@link String}s. Great for
 * quickly generating a list of assists.
 * 
 * @author Igor Vaynberg (ivaynberg)
 * 
 */
public class StringAutoAssistRenderer extends AbstractAutoAssistRenderer
{
	/**
	 * A singleton instance
	 */
	public static final IAutoAssistRenderer INSTANCE = new StringAutoAssistRenderer();


	protected void renderAssist(Object object, Response response)
	{
		response.write(object.toString());
	}

	protected String getTextValue(Object object)
	{
		return object.toString();
	}

}
