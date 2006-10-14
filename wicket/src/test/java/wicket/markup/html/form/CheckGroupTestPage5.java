/*
 * $Id: CheckGroupTestPage5.java 5844 2006-05-24 20:53:56 +0000 (Wed, 24 May
 * 2006) joco01 $ $Revision$ $Date: 2006-05-24 20:53:56 +0000 (Wed, 24
 * May 2006) $
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

package wicket.markup.html.form;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import wicket.markup.html.WebMarkupContainer;
import wicket.markup.html.WebPage;
import wicket.model.Model;

/**
 * tests exception when check is outside any group
 * 
 * @author igor
 * 
 */
public class CheckGroupTestPage5 extends WebPage
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * Constructor
	 */
	public CheckGroupTestPage5()
	{
		List<String> list = new ArrayList<String>();
		Form form = new Form(this, "form");
		CheckGroup<String> group = new CheckGroup<String>(form, "group", new Model<Collection<String>>(list));
		new WebMarkupContainer(group, "container");
		new Check<String>(group, "check1", new Model<String>("check1"));
		// here we add check2 to the form so it is outside the group - it should
		// throw an exception when rendering
		new Check<String>(form, "check2", new Model<String>("check2"));
	}
}