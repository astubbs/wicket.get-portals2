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
package wicket.examples.hangman;

import wicket.ApplicationSettings;
import wicket.protocol.http.WebApplication;
import wicket.util.time.Duration;


/**
 * Class defining the main Hangman application.
 * 
 * @author Chris Turner
 * @version 1.0
 */
public class HangmanApplication extends WebApplication
{
    /**
     * Create the hangman application.
     */
    public HangmanApplication()
    {
        this.getSettings().setStripComponentNames(true);

        // Initialise Wicket settings
        getPages().setHomePage(Home.class);

        ApplicationSettings settings = getSettings();
        if (!Boolean.getBoolean("cache-templates"))
        {
            settings.setResourcePollFrequency(Duration.ONE_SECOND);
        }
    }
}