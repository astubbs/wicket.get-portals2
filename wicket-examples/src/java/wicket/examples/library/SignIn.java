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
package wicket.examples.library;

import wicket.PageParameters;
import wicket.contrib.markup.html.panel.signin.SignInPanel;
import wicket.examples.WicketExamplePage;

/**
 * Simple example of a sign in page.
 * @author Jonathan Locke
 */
public final class SignIn extends WicketExamplePage
{
    /**
     * Constructor
     * @param parameters The page parameters
     */
    public SignIn(final PageParameters parameters)
    {
        add(new SignInPanel("signInPanel")
        {
            public boolean signIn(final String username, final String password)
            {
                // Sign the user in
                final User user = ((LibrarySession)getSession()).authenticate(username, password);

                // If the user was signed in
                if (user != null)
                {
                    return true;
                }
                else
                {
                    error(getLocalizer().getString("couldNotAuthenticate", this));
                    return false;
                }
            }
        });
    }
    
    /**
     * Constructor
     */
    public SignIn()
    {
        this(null);
    }
}

// 
