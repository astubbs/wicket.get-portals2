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
package wicket.examples.displaytag.utils;

import java.util.List;

import wicket.AttributeModifier;
import wicket.markup.html.list.ListItem;
import wicket.markup.html.list.PageableListView;
import wicket.model.Model;


/**
 * Paged table with alternating row styles
 * 
 * @author Juergen
 */
public abstract class PagedTableWithAlternatingRowStyle extends PageableListView
{
    /**
     * Constructor
     * 
     * @param componentName
     * @param data
     * @param pageSize
     */
    public PagedTableWithAlternatingRowStyle(final String componentName, final List data, int pageSize)
    {
        super(componentName, data, pageSize);
    }


    /**
     * Change the style with every other row
     * 
     * @see wicket.markup.html.list.ListView#populateItem(wicket.markup.html.list.ListItem)
     */
    protected void populateItem(final ListItem listItem)
    {
        listItem.add(
                new AttributeModifier(
                        "class",
                        new Model(listItem.isEvenIndex() ? "even" : "odd")));
        
    }
}
