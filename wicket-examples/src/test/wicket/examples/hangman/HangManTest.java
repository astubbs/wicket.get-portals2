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

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestSuite;
import net.sourceforge.jwebunit.WebTestCase;
import nl.openedge.util.jetty.JettyDecorator;

/**
 * Testcase for the <code>Hangman</code> class.
 * 
 * @author Chris Turner
 * @version 1.0
 */
public class HangManTest extends WebTestCase
{

	/**
	 * Create the test case.
	 * 
	 * @param message
	 *           The test name
	 */
	public HangManTest(String message)
	{
		super(message);
	}

	/**
	 * Tests the hangman class directly for a winning game.
	 * 
	 * @throws Exception
	 */
	public void testHangmanWinGame() throws Exception
	{
		Hangman hangman = new Hangman();
		hangman.newGame(5, "testing");
		
		Assert.assertEquals(5, hangman.getGuessesRemaining());
		Assert.assertFalse(hangman.isWon());
		Assert.assertFalse(hangman.isLost());

		doGuessTest(hangman, 'a', false);
		Assert.assertEquals(4, hangman.getGuessesRemaining());
		Assert.assertFalse(hangman.isWon());
		Assert.assertFalse(hangman.isLost());

		hangman.guessLetter('a');
		Assert.assertEquals(4, hangman.getGuessesRemaining());
		Assert.assertFalse(hangman.isWon());
		Assert.assertFalse(hangman.isLost());

		doGuessTest(hangman, 't', true);
		Assert.assertEquals(4, hangman.getGuessesRemaining());
		Assert.assertFalse(hangman.isWon());
		Assert.assertFalse(hangman.isLost());

		doGuessTest(hangman, 'e', true);
		Assert.assertEquals(4, hangman.getGuessesRemaining());
		Assert.assertFalse(hangman.isWon());
		Assert.assertFalse(hangman.isLost());

		doGuessTest(hangman, 's', true);
		Assert.assertEquals(4, hangman.getGuessesRemaining());
		Assert.assertFalse(hangman.isWon());
		Assert.assertFalse(hangman.isLost());

		doGuessTest(hangman, 'i', true);
		Assert.assertEquals(4, hangman.getGuessesRemaining());
		Assert.assertFalse(hangman.isWon());
		Assert.assertFalse(hangman.isLost());

		doGuessTest(hangman, 'n', true);
		Assert.assertEquals(4, hangman.getGuessesRemaining());
		Assert.assertFalse(hangman.isWon());
		Assert.assertFalse(hangman.isLost());

		doGuessTest(hangman, 'g', true);
		Assert.assertEquals(4, hangman.getGuessesRemaining());
		Assert.assertTrue(hangman.isWon());
		Assert.assertFalse(hangman.isLost());
	}

	/**
	 * Tests the hangman class directly for a lost game.
	 * 
	 * @throws Exception
	 */
	public void testHangmanLooseGame() throws Exception
	{
		Hangman hangman = new Hangman();
		hangman.newGame(2, "foo");
		
		Assert.assertEquals(2, hangman.getGuessesRemaining());
		Assert.assertFalse(hangman.isWon());
		Assert.assertFalse(hangman.isLost());

		doGuessTest(hangman, 'z', false);
		Assert.assertEquals(1, hangman.getGuessesRemaining());
		Assert.assertFalse(hangman.isWon());
		Assert.assertFalse(hangman.isLost());

		doGuessTest(hangman, 'v', false);
		Assert.assertEquals(0, hangman.getGuessesRemaining());
		Assert.assertFalse(hangman.isWon());
		Assert.assertTrue(hangman.isLost());
	}

	/**
	 * Tests the webapplication for a successful match.
	 */
	public void testHangmanSuccessWebGame()
	{
		getTestContext().setBaseUrl("http://localhost:8098/wicket-examples");
		beginAt("/hangman?setWord=hangman");

		assertTitleEquals("Wicket Examples - hangman");
		assertLinkPresent("start");
		clickLink("start");
		assertTitleEquals("Wicket Examples - hangman");
		assertTextPresent("guesses remaining");

		assertElementPresent("guessesRemaining");
		assertTextInElement("guessesRemaining", "5");

		clickLink("letter_f");

		assertElementPresent("guessesRemaining");
		assertTextInElement("guessesRemaining", "4");

		clickLink("letter_h");
		assertElementPresent("guessesRemaining");
		assertTextInElement("guessesRemaining", "4");

		clickLink("letter_a");
		clickLink("letter_n");
		clickLink("letter_g");
		clickLink("letter_m");
		assertTextPresent("Congratulations! You guessed that the word was ");
	}

	/**
	 * Tests the webapplication for an unsuccessful match.
	 */
	public void testHangmanFailureWebGame()
	{
		getTestContext().setBaseUrl("http://localhost:8098/wicket-examples");
		beginAt("/hangman?setWord=hangman");

		assertTitleEquals("Wicket Examples - hangman");
		assertLinkPresent("start");
		clickLink("start");
		assertTitleEquals("Wicket Examples - hangman");
		assertTextPresent("guesses remaining");

		assertElementPresent("guessesRemaining");
		assertTextInElement("guessesRemaining", "5");

		clickLink("letter_f");

		assertElementPresent("guessesRemaining");
		assertTextInElement("guessesRemaining", "4");

		assertLinkNotPresent("letter_f");
		clickLink("letter_x");
		assertTextInElement("guessesRemaining", "3");

		clickLink("letter_e");
		assertTextInElement("guessesRemaining", "2");

		clickLink("letter_t");
		assertTextInElement("guessesRemaining", "1");

		clickLink("letter_v");
		assertTextPresent("Bad Luck! You failed to guess that the word was");
	}

	/**
	 * Performs a guess.
	 * @param hangman
	 * @param c
	 * @param expected
	 */
	private void doGuessTest(Hangman hangman, char c, boolean expected)
	{
		Assert.assertFalse(hangman.isGuessed(c));
		Assert.assertEquals(expected, hangman.guessLetter(c));
		Assert.assertTrue(hangman.isGuessed(c));
	}

	/**
	 * Creates the testsuite.
	 * 
	 * @return the testsuite.
	 */
	public static Test suite()
	{
		TestSuite suite = new TestSuite();
		suite.addTestSuite(HangManTest.class);

		JettyDecorator deco = new JettyDecorator(suite);
		deco.setPort(8098);
		deco.setWebappContextRoot("src/webapp");
		deco.setContextPath("/wicket-examples");
		return deco;
	}
}
