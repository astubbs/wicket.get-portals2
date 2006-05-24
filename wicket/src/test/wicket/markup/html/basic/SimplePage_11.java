/*
 * $Id: SimplePage_10.java 3749 2006-01-14 00:54:30Z ivaynberg $
 * $Revision$ $Date: 2006-01-14 01:54:30 +0100 (Sa, 14 Jan 2006) $
 * 
 * ==================================================================== Licensed
 * under the Apache License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the
 * License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package wicket.markup.html.basic;

import wicket.markup.html.WebPage;


/**
 * Mock page for testing.
 * 
 * @author Chris Turner
 */
public class SimplePage_11 extends WebPage
{
	private static final long serialVersionUID = 1L;

	/**
	 * Construct.
	 */
	public SimplePage_11()
	{
		add(new Label(this,"panel", "panel"));
		add(new Label(this,"border", "border"));
		add(new Label(this,"body", "body"));
		add(new Label(this,"child", "child"));
		add(new Label(this,"extend", "extend"));
		add(new Label(this,"message", "message"));
		add(new Label(this,"component", "component"));
		add(new Label(this,"id", "id"));
		add(new Label(this,"head", "head"));
		add(new Label(this,"fragment", "fragment"));
	}
}
