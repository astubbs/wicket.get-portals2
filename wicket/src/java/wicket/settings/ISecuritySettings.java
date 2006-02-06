package wicket.settings;

import wicket.authorization.IAuthorizationStrategy;
import wicket.markup.html.form.persistence.CookieValuePersisterSettings;
import wicket.util.crypt.ICryptFactory;

/**
 * Interface for security related settings
 * 
 * @author Igor Vaynberg (ivaynberg)
 */
public interface ISecuritySettings
{
	/**
	 * encryption key used by default crypt factory
	 */
	public static final String DEFAULT_ENCRYPTION_KEY = "WiCkEt-FRAMEwork";

	/**
	 * Gets the authorization strategy.
	 * 
	 * @return Returns the authorizationStrategy.
	 */
	IAuthorizationStrategy getAuthorizationStrategy();

	/**
	 * Get the defaults to be used by persistence manager
	 * 
	 * @return CookieValuePersisterSettings
	 */
	CookieValuePersisterSettings getCookieValuePersisterSettings();

	/**
	 * @return crypt factory used to generate crypt objects
	 */
	ICryptFactory getCryptFactory();

	/**
	 * Gets the sign in page class.
	 * 
	 * @return Page class for sign-in.
	 */
	Class getSignInPage();


	/**
	 * Sets the authorization strategy.
	 * 
	 * @param strategy
	 *            new authorization strategy
	 * 
	 */
	void setAuthorizationStrategy(IAuthorizationStrategy strategy);

	/**
	 * @param cookieValuePersisterSettings
	 *            The cookieValuePersisterSettings to set.
	 */
	void setCookieValuePersisterSettings(CookieValuePersisterSettings cookieValuePersisterSettings);

	/**
	 * Sets the factory that will be used to create crypt objects. The crypt
	 * object returned from the first call is cached.
	 * 
	 * @param cryptFactory
	 */
	void setCryptFactory(ICryptFactory cryptFactory);

	/**
	 * Sets the sign in page class.
	 * 
	 * @param signInPage
	 *            Page class for sign-in.
	 */
	void setSignInPage(Class signInPage);

}