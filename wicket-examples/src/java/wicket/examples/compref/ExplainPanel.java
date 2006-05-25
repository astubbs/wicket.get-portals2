/*
 * $Id$ $Revision$
 * $Date$
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
package wicket.examples.compref;

import wicket.MarkupContainer;
import wicket.markup.html.basic.MultiLineLabel;
import wicket.markup.html.panel.Panel;

/**
 * A explanation panel component.
 * 
 * @author Gwyn Evans
 */
class ExplainPanel extends Panel
{
	/**
	 * Construct.
	 * 
	 * @param html
	 * @param code
	 */
	public ExplainPanel(MarkupContainer parent,String html, String code)
	{
		super(parent,"explainPanel");
		add(new MultiLineLabel(this,"html", html));
		add(new MultiLineLabel(this,"code", code).setEscapeModelStrings(false));
	}
}
