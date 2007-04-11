/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.wicket.examples.hangman;

import java.util.Iterator;

import org.apache.wicket.examples.WicketWebTestCase;
import org.apache.wicket.examples.hangman.Game;
import org.apache.wicket.examples.hangman.Letter;
import org.apache.wicket.examples.hangman.WordGenerator;

import junit.framework.Assert;
import junit.framework.Test;

/**
 * Testcase for the <code>Game</code> class.
 * 
 * @author Chris Turner
 * @version 1.0
 */
public class HangManTest extends WicketWebTestCase
{
	/**
	 * 
	 * @return Test
	 */
	public static Test suite()
	{
		return suite(HangManTest.class);
	}

	/**
	 * Create the test case.
	 * 
	 * @param message
	 *            The test name
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
	public void test_1() throws Exception
	{
		Game hangman = new Game();
		hangman.newGame(5, new WordGenerator(new String[] { "testing" }));

		Assert.assertEquals(5, hangman.getGuessesRemaining());
		Assert.assertFalse(hangman.isWon());
		Assert.assertFalse(hangman.isLost());

		doGuessTest(hangman, 'a', false);
		Assert.assertEquals(4, hangman.getGuessesRemaining());
		Assert.assertFalse(hangman.isWon());
		Assert.assertFalse(hangman.isLost());

		guess(hangman, 'a');
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

	private Letter letter(Game hangman, char c)
	{
		for (Iterator iter = hangman.getLetters().iterator(); iter.hasNext();)
		{
			Letter letter = (Letter)iter.next();
			if (letter.asString().equalsIgnoreCase(Character.toString(c)))
			{
				return letter;
			}
		}
		return null;
	}

	private boolean guess(Game hangman, char c)
	{
		return hangman.guess(letter(hangman, c));
	}

	/**
	 * Tests the hangman class directly for a lost game.
	 * 
	 * @throws Exception
	 */
	public void testHangmanLoseGame() throws Exception
	{
		Game hangman = new Game();
		hangman.newGame(2, new WordGenerator(new String[] { "foo" }));

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
		beginAt("/hangman/?word=hangman");

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
		beginAt("/hangman/?word=hangman");

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

		// todo jwebunit doesn't really test if it is a link!
		// it just finds a html element by that id, and we have one
//		assertLinkNotPresent("letter_f");
		clickLink("letter_x");
		assertTextInElement("guessesRemaining", "3");

		clickLink("letter_e");
		assertTextInElement("guessesRemaining", "2");

		clickLink("letter_t");
		assertTextInElement("guessesRemaining", "1");

		clickLink("letter_v");
		assertTextPresent("Bad luck. You failed to guess that the word was");
	}

	/**
	 * Performs a guess.
	 * 
	 * @param hangman
	 * @param c
	 * @param expected
	 */
	private void doGuessTest(Game hangman, char c, boolean expected)
	{
		Assert.assertFalse(letter(hangman, c).isGuessed());
		Assert.assertEquals(expected, guess(hangman, c));
		Assert.assertTrue(letter(hangman, c).isGuessed());
	}
}
