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
package navomatic;

import com.voicetribe.wicket.markup.html.border.Border;
import com.voicetribe.wicket.markup.html.border.BoxBorder;

/**
 * Border component.
 * @author Jonathan Locke
 */
public class NavomaticBorder extends Border
{
    /**
     * Constructor
     * @param componentName The name of this component
     */
    public NavomaticBorder(final String componentName)
    {
        super(componentName);
        add(new BoxBorder("boxBorder"));
        add(new BoxBorder("boxBorder2"));
    }
}

///////////////////////////////// End of File /////////////////////////////////
