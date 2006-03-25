/*
 * $Id: org.eclipse.jdt.ui.prefs 5004 2006-03-17 20:47:08 -0800 (Fri, 17 Mar
 * 2006) eelco12 $ $Revision: 5004 $ $Date: 2006-03-17 20:47:08 -0800 (Fri, 17
 * Mar 2006) $
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

public class AjaxPreprocessingCallDecorator implements IAjaxCallDecorator
{
	private final IAjaxCallDecorator delegate;

	public AjaxPreprocessingCallDecorator(IAjaxCallDecorator delegate)
	{
		this.delegate = delegate;
	}


	public String decorateScript(String script)
	{
		String s = preDecorateScript(script);
		return (delegate == null) ? s : delegate.decorateScript(script);
	}

	public String decorateOnSuccessScript(String script)
	{
		String s = preDecorateOnSuccessScript(script);
		return (delegate == null) ? s : delegate.decorateOnSuccessScript(script);
	}

	public String decorateOnFailureScript(String script)
	{
		String s = preDecorateOnFailureScript(script);
		return (delegate == null) ? s : delegate.decorateOnFailureScript(script);
	}


	public String preDecorateScript(String script)
	{
		return script;
	}

	public String preDecorateOnSuccessScript(String script)
	{
		return script;
	}

	public String preDecorateOnFailureScript(String script)
	{
		return script;
	}


}
