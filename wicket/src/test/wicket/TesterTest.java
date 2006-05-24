/*
 * $Id: org.eclipse.jdt.ui.prefs 5004 2006-03-17 20:47:08 -0800 (Fri, 17 Mar 2006) eelco12 $
 * $Revision: 5004 $
 * $Date: 2006-03-17 20:47:08 -0800 (Fri, 17 Mar 2006) $
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
package wicket;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;
import wicket.markup.html.WebPage;
import wicket.markup.html.basic.Label;
import wicket.util.tester.ITestPageSource;
import wicket.util.tester.WicketTester;

/**
 * @author jcompagner
 */
public class TesterTest extends TestCase {

    WicketTester tester;

    protected void setUp() throws Exception {
        tester = new WicketTester();
    }

    /**
     * 
     */
    public void testAssert() {
        tester.startPage(new ITestPageSource() {
            public Page getTestPage() {
                return new MyPage();
            }
        });
        tester.debugComponentTrees();
        try {
            tester.assertVisible("label");
            fail("Should fail, because label is invisible");
        } catch (AssertionFailedError e) {
        } catch (NullPointerException e) {
            fail("NullPointerException shouldn't be thrown, instead it must fail.");
        }
    }

    private class MyPage extends WebPage {
		private static final long serialVersionUID = 1L;

		/**
		 * Construct.
		 */
		public MyPage() {
            add(new Label(this,"label") {

            	private static final long serialVersionUID = 1L;

				public boolean isVisible() {
                    return false;
                }
            });
        }
        
    }


}
