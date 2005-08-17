/*
 * $Id$
 * $Revision$
 * $Date$
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package wicket.examples.stockquote;

import java.io.Serializable;

import wicket.examples.WicketExamplePage;
import wicket.markup.html.basic.Label;
import wicket.markup.html.form.Form;
import wicket.markup.html.form.TextField;
import wicket.model.IModel;
import wicket.model.PropertyModel;

/**
 * Stock quote webservice custom component example.
 * 
 * @author Martijn Dashorst
 */
public class StockQuote extends WicketExamplePage
{
	/**
	 * Constructor
	 */
	public StockQuote()
	{
		// code for the first example, directly using the
		// stock quote component.
		add(new StockQuoteLabel("stockIBM", "IBM"));

		// code for second example: using a form for selecting the
		// symbol of the stock quote.

		// shared model to get and set the symbol property on the 
		// quote instance
		final IModel model = new PropertyModel(quote, "symbol");
		
		// form holding the input field.
		final Form form = new Form("form");
		add(form);
		form.add(new TextField("symbol", model));

		// labels for displaying the chosen symbol
		add(new Label("symbol", model));

		// and its quote
		add(new StockQuoteLabel("quote", model));
	}

	/**
	 * Quote instance used for communicating between the form input field
	 * and the display labels for showing the stock quote.
	 */
	private final Quote quote = new Quote();

	/**
	 * POJO to hold the symbol for the quote query.
	 */
	public static class Quote implements Serializable
	{
		private String symbol;

		/**
		 * Gets the symbol.
		 * @return the symbol
		 */
		public String getSymbol()
		{
			return symbol;
		}

		/**
		 * Sets the symbol.
		 * @param symbol the symbol
		 */
		public void setSymbol(String symbol)
		{
			this.symbol = symbol;
		}
	}
}