/*
 * $Id$ $Revision$
 * $Date$
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
package wicket.markup.html.form.encryption;

/**
 * Encryption and decryption implementations are accessed through this
 * interface. It provide some simple means to encrypt and decrypt strings, like
 * passwords etc.. It depends on the implementation itself which algorithms are
 * used to en-/decrypt the data.
 * 
 * @author Juergen Donnerstag
 */
public interface ICrypt
{
	/**
	 * Decrypts a string.
	 * 
	 * @param text
	 *            the text to decrypt
	 * @return the decrypted string.
	 */
	public String decryptString(final String text);
	/**
	 * Encrypts a string.
	 * 
	 * @param plainText
	 * @return encrypted string
	 */
	public String encryptString(final String plainText);

	/**
	 * Sets private encryption key. It depends on the implementation if a
	 * default key is applied or an exception is thrown, if no private key has
	 * been provided.
	 * 
	 * @param key
	 *            private key
	 */
	public void setKey(final String key);
}