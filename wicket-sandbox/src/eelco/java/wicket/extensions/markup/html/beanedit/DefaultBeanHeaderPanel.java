/*
 * $Id$ $Revision$ $Date$
 * 
 * ==============================================================================
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package wicket.extensions.markup.html.beanedit;

import wicket.MarkupContainer;
import wicket.markup.html.basic.Label;
import wicket.markup.html.panel.Panel;

/**
 * Default header for a bean editor.
 * 
 * @author Eelco Hillenius
 */
public class DefaultBeanHeaderPanel extends Panel
{
	private static final long serialVersionUID = 1L;

	/**
	 * Construct.
	 * 
	 * @param id
	 *            component id
	 * @param beanModel
	 *            model with the JavaBean to be edited or displayed
	 */
	public DefaultBeanHeaderPanel(MarkupContainer parent,String id, BeanModel beanModel)
	{
		super(parent,id);
		new Label(this,"displayName", new BeanDisplayNameModel(beanModel));
		setRenderBodyOnly(true);
	}
}
