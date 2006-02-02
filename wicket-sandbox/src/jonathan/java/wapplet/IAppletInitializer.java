package wapplet;

import java.awt.Container;

/**
 * The IAppletInitializer interface should be implemented by the class passed to
 * the Applet constructor. This class and every class referenced by it will be
 * automatically included in the applet JAR file for this applet. When the
 * applet is loaded by the client browser, the init() method will be called,
 * passing in a Container to populate with components and the model object
 * produced by the Applet component's IModel.
 * 
 * @author Jonathan Locke
 */
public interface IAppletInitializer
{
	/**
	 * Interface to code that initializes a Container using a model.
	 * 
	 * @param container
	 *            The Swing container to populate with components
	 * @param model
	 *            The model to update in the applet
	 */
	void init(Container container, Object model);
}
