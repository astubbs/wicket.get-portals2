/*
 * $Id: IResourcePath.java 3585 2006-01-02 07:37:31 +0000 (Mon, 02 Jan 2006)
 * jonathanlocke $ $Revision$ $Date: 2006-01-02 07:37:31 +0000 (Mon, 02
 * Jan 2006) $
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
package wicket.util.file;

/**
 * Knows how to manage paths and folders, and how to find resources in them.
 * 
 * @author jcompagner
 */
public interface IResourcePath extends IResourceFinder
{
	/**
	 * @param folder
	 *            Adds a folder to the path
	 */
	void add(String folder);
}
