/**
 * 
 */
package wicket.examples.ajax.builtin.modal;

import wicket.ajax.AjaxRequestTarget;
import wicket.ajax.markup.html.AjaxLink;
import wicket.extensions.ajax.markup.html.modal.ModalWindow;
import wicket.markup.html.WebPage;

/**
 * @author Matej Knopp
 *
 */
public class ModalContent2Page extends WebPage {

	/**
	 * 
	 */
	public ModalContent2Page() 
	{
		add(new AjaxLink("close")
		{
			public void onClick(AjaxRequestTarget target) {
				ModalWindow.close(target);
			}
		});

		
	}


}
