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
package wicket.util.crypt;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;

import javax.crypto.Cipher;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import wicket.WicketRuntimeException;

/**
 * Abstract base class for JCE based ICrypt implementations.
 * 
 * @author Juergen Donnerstag
 */
public abstract class AbstractCrypt implements ICrypt
{
	/** Default encryption key */
	private static final String DEFAULT_ENCRYPTION_KEY = "WiCkEt-CrYpT";

	/** Encoding used to convert java String from and to byte[] */
	private static final String CHARACTER_ENCODING = "UTF-8";

	/** Log. */
	private static Log log = LogFactory.getLog(AbstractCrypt.class);

	/** Key used to de-/encrypt the data */
	private String encryptionKey = DEFAULT_ENCRYPTION_KEY;

	/**
	 * Constructor
	 */
	public AbstractCrypt()
	{
	}

	/**
	 * Decrypts a string into a string.
	 * 
	 * @param text
	 *            text to decript
	 * @return the decrypted text
	 */
	public final String decrypt(final String text)
	{
		try
		{
			return new String(decryptStringToByteArray(text), CHARACTER_ENCODING);
		}
		catch (UnsupportedEncodingException ex)
		{
			throw new WicketRuntimeException(ex.getMessage());
		}
	}

	/**
	 * Encrypt a string into a string
	 * 
	 * @param plainText
	 *            text to encrypt
	 * @return encrypted string
	 */
	public final String encrypt(final String plainText)
	{
		try
		{
			byte[] cipherText = encryptStringToByteArray(plainText);
			String text = new BASE64Encoder().encode(cipherText);
			
			// A standard compliant base64 encoder will insert a newline 
			// after 76 chars. Remove it. Hopefully the decoder is flexible
			// enough to cope with the missing new line.
			if ((text.indexOf("\n") != -1) || (text.indexOf("\r") != -1))
			{
				StringBuffer buf = new StringBuffer(text.length());
				for (int i=0; i < text.length(); i++)
				{
					char ch = text.charAt(i);
					if ((ch != '\n') && (ch != '\r'))
					{
						buf.append(ch);
					}
				}
				text = buf.toString();
			}
			
			return text;
		}
		catch (GeneralSecurityException e)
		{
			log.error("Unable to encrypt text '" + plainText + "'", e);
			return null;
		}
	}

	/**
	 * Get encryption private key
	 * 
	 * @return encryption private key
	 */
	public String getKey()
	{
		return this.encryptionKey;
	}

	/**
	 * Set encryption private key
	 * 
	 * @param key
	 *            private key to make de-/encryption unique
	 */
	public void setKey(final String key)
	{
		this.encryptionKey = key;
	}

	/**
	 * Crypts the given byte array
	 * 
	 * @param input
	 *            byte array to be crypted
	 * @param mode
	 *            crypt mode
	 * @return the input crypted. Null in case of an error
	 * @throws GeneralSecurityException
	 */
	protected abstract byte[] crypt(final byte[] input, final int mode)
			throws GeneralSecurityException;

	/**
	 * Decrypts a String into a byte array.
	 * 
	 * @param encrypted
	 *            text to decrypt
	 * @return the decrypted text
	 */
	private final byte[] decryptStringToByteArray(final String encrypted)
	{
		final byte[] plainBytes;
		try
		{
			plainBytes = new BASE64Decoder().decodeBuffer(encrypted);
			return crypt(plainBytes, Cipher.DECRYPT_MODE);
		}
		catch (IOException e)
		{
			throw new WicketRuntimeException(e.getMessage());
		}
		catch (GeneralSecurityException e)
		{
			log.error("Unable to decrypt text '" + encrypted + "'", e);
			return null;
		}
	}

	/**
	 * Encrypts the given text into a byte array.
	 * 
	 * @param plainText
	 *            text to encrypt
	 * @return the string encrypted
	 * @throws GeneralSecurityException
	 */
	private final byte[] encryptStringToByteArray(final String plainText)
			throws GeneralSecurityException
	{
		try
		{
			return crypt(plainText.getBytes(CHARACTER_ENCODING), Cipher.ENCRYPT_MODE);
		}
		catch (UnsupportedEncodingException ex)
		{
			throw new WicketRuntimeException(ex.getMessage());
		}
	}
}
