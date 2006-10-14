/*
 * $Id: TimeOfDayTest.java 5118 2006-03-25 16:28:23 +0000 (Sat, 25 Mar 2006)
 * dashorst $ $Revision$ $Date: 2006-03-25 16:28:23 +0000 (Sat, 25 Mar
 * 2006) $
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
package wicket.util.time;


import junit.framework.Assert;
import junit.framework.TestCase;
import wicket.util.time.TimeOfDay.Meridian;

/**
 * Test cases for this object
 * 
 * @author Jonathan Locke
 */
public final class TimeOfDayTest extends TestCase
{
	/**
	 * 
	 */
	public void test()
	{
		Assert.assertEquals(0, TimeOfDay.MIDNIGHT.hour());
		Assert.assertEquals(TimeOfDay.MIDNIGHT, TimeOfDay.valueOf(TimeOfDay.MIDNIGHT.next()));

		final TimeOfDay three = TimeOfDay.time(3, 0, Meridian.PM);
		final TimeOfDay five = TimeOfDay.time(5, 0, Meridian.PM);

		Assert.assertTrue(five.after(three));
	}
}