/*
 * $Id: FormInputApplication.java 5394 2006-04-16 06:36:52 -0700 (Sun, 16 Apr
 * 2006) jdonnerstag $ $Revision$ $Date: 2006-04-16 06:36:52 -0700 (Sun,
 * 16 Apr 2006) $
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
package wicket.examples.forminput;

import java.awt.Font;
import java.util.Locale;

import wicket.examples.WicketExampleApplication;
import wicket.markup.html.ServerAndClientTimeFilter;
import wicket.markup.html.image.resource.DefaultButtonImageResource;
import wicket.protocol.http.request.urlcompressing.URLCompressor;
import wicket.protocol.http.request.urlcompressing.WebURLCompressingCodingStrategy;
import wicket.protocol.http.request.urlcompressing.WebURLCompressingTargetResolverStrategy;
import wicket.request.IRequestCycleProcessor;
import wicket.request.compound.CompoundRequestCycleProcessor;

/**
 * Application class for form input example.
 * 
 * @author Eelco Hillenius
 */
public class FormInputApplication extends WicketExampleApplication
{
	/**
	 * Constructor.
	 */
	public FormInputApplication()
	{
	}

	/**
	 * @see wicket.protocol.http.WebApplication#init()
	 */
	protected void init()
	{
		getExceptionSettings().setThrowExceptionOnMissingResource(false);
		getRequestCycleSettings().addResponseFilter(new ServerAndClientTimeFilter());
		getMarkupSettings().setStripWicketTags(true);
		Font font = new Font("SimSun", Font.BOLD, 16);
		DefaultButtonImageResource imgSave = new DefaultButtonImageResource("\u4FDD\u5B58");
		imgSave.setFont(font);
		DefaultButtonImageResource imgReset = new DefaultButtonImageResource("\u91CD\u7F6E");
		imgReset.setFont(font);
		getSharedResources().add("save", Locale.SIMPLIFIED_CHINESE, imgSave);
		getSharedResources().add("reset", Locale.SIMPLIFIED_CHINESE, imgReset);
		Font fontJa = new Font("Serif", Font.BOLD, 16);
		DefaultButtonImageResource imgSaveJa = new DefaultButtonImageResource("\u4fdd\u5b58");
		imgSaveJa.setFont(fontJa);
		DefaultButtonImageResource imgResetJa = new DefaultButtonImageResource(
				"\u30ea\u30bb\u30c3\u30c8");
		imgResetJa.setFont(fontJa);
		getSharedResources().add("save", Locale.JAPANESE, imgSaveJa);
		getSharedResources().add("reset", Locale.JAPANESE, imgResetJa);
	}

	/**
	 * Special overwrite to have url compressing for this example.
	 * 
	 * @see URLCompressor
	 * @see wicket.protocol.http.WebApplication#newRequestCycleProcessor()
	 */
	protected IRequestCycleProcessor newRequestCycleProcessor()
	{
		return new CompoundRequestCycleProcessor(new WebURLCompressingCodingStrategy(),
				new WebURLCompressingTargetResolverStrategy(), null, null, null);
	}

	/**
	 * @see wicket.protocol.http.WebApplication#newWebRequest(javax.servlet.http.HttpServletRequest)
	 *      protected WebRequest newWebRequest(HttpServletRequest
	 *      servletRequest) { return new
	 *      WebRequestWithCryptedUrl(servletRequest); }
	 */

	/**
	 * @see wicket.protocol.http.WebApplication#newWebResponse(javax.servlet.http.HttpServletResponse)
	 *      protected WebResponse newWebResponse(HttpServletResponse
	 *      servletResponse) { return new
	 *      WebResponseWithCryptedUrl(servletResponse); }
	 */

	/**
	 * @see wicket.Application#getHomePage()
	 */
	public Class getHomePage()
	{
		return FormInput.class;
	}
}
