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
package wicket.examples;

import wicket.markup.html.form.encryption.NoCrypt;
import wicket.protocol.http.WebApplication;
import wicket.util.file.Folder;
import wicket.util.file.Path;
import wicket.util.time.Duration;

/**
 * WicketServlet class for hello world example.
 * @author Jonathan Locke
 */
public abstract class WicketExampleApplication extends WebApplication
{
    /**
     * Constructor.
     */
    public WicketExampleApplication()
    {        
        // WARNING: DO NOT do this on a real world application unless
        // you really want your app's passwords all passed around and 
        // stored in unencrypted browser cookies (BAD IDEA!)!!! 
        
        // The NoCrypt class is being used here because not everyone
        // has the java security classes required by Crypt installed
        // and we want them to be able to run the examples out of the
        // box.
        getSettings().setCryptClass(NoCrypt.class);

        try
        {
        	getSettings().setSourcePath(new Path(new Folder("c:\\Proects\\wicket-examples\\src\\java")));
        }
        catch (IllegalArgumentException e)
        {  
            // Ignore if folder cannot be found
        }
        getSettings().setResourcePollFrequency(Duration.ONE_SECOND);
    }
}
