/*
 * $Id: org.eclipse.jdt.ui.prefs,v 1.6 2006/02/06 08:27:03 ivaynberg Exp $
 * $Revision: 1.6 $
 * $Date: 2006/02/06 08:27:03 $
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
package wicket.ajax.calldecorator;

import wicket.ajax.IAjaxCallDecorator;

/**
 * 
 * @author Igor Vaynberg (ivaynberg)
 */
public class AjaxCallDecoratorAdapter implements IAjaxCallDecorator
{

	/**
	 * @see wicket.ajax.IAjaxCallDecorator#getBeforeScript()
	 */
	public String getBeforeScript()
	{
		return null;
	}

	/**
	 * @see wicket.ajax.IAjaxCallDecorator#getAfterScript()
	 */
	public String getAfterScript()
	{
		return null;
	}

	/**
	 * @see wicket.ajax.IAjaxCallDecorator#getOnSuccessScript()
	 */
	public String getOnSuccessScript()
	{
		return null;
	}

	/**
	 * @see wicket.ajax.IAjaxCallDecorator#getOnFailureScript()
	 */
	public String getOnFailureScript()
	{
		return null;
	}

}
