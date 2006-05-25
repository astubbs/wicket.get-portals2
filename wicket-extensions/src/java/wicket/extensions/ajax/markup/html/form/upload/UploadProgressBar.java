package wicket.extensions.ajax.markup.html.form.upload;

import wicket.Application;
import wicket.AttributeModifier;
import wicket.Component;
import wicket.IInitializer;
import wicket.MarkupContainer;
import wicket.ResourceReference;
import wicket.markup.html.PackageResource;
import wicket.markup.html.PackageResourceReference;
import wicket.markup.html.WebMarkupContainer;
import wicket.markup.html.form.Form;
import wicket.markup.html.panel.Panel;
import wicket.markup.html.resources.JavaScriptReference;
import wicket.model.Model;

/**
 * ProgressbarPanel
 * 
 * @author Andrew Lombardi
 */
public class UploadProgressBar extends Panel
{

	private static final String RESOURCE_NAME = UploadProgressBar.class.getName();

	private static final long serialVersionUID = 1L;

	private static final PackageResourceReference JS_PROGRESSBAR = new PackageResourceReference(
			UploadProgressBar.class, "progressbar.js");

	/**
	 * @param id
	 * @param form
	 */
	public UploadProgressBar(MarkupContainer parent, final String id, final Form form)
	{
		super(parent, id);
		setOutputMarkupId(true);
		form.setOutputMarkupId(true);
		setRenderBodyOnly(true);

		add(new JavaScriptReference(this, "javascript", JS_PROGRESSBAR));


		final WebMarkupContainer barDiv = new WebMarkupContainer(this, "bar");
		barDiv.setOutputMarkupId(true);
		add(barDiv);

		final WebMarkupContainer statusDiv = new WebMarkupContainer(this, "status");
		statusDiv.setOutputMarkupId(true);
		add(statusDiv);

		form.add(new AttributeModifier("onsubmit", true, new Model()
		{

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;


			@Override
			public Object getObject(Component component)
			{
				ResourceReference ref = new ResourceReference(RESOURCE_NAME);

				return "var def=new wupb.Def('" + form.getMarkupId() + "', '"
						+ statusDiv.getMarkupId() + "', '" + barDiv.getMarkupId() + "', '"
						+ getPage().urlFor(ref) + "'); wupb.start(def);";
			}
		}));
	}

	/**
	 * Initializer for this component; binds static resources.
	 */
	public final static class ComponentInitializer implements IInitializer
	{
		/**
		 * @see wicket.IInitializer#init(wicket.Application)
		 */
		public void init(Application application)
		{
			PackageResource.bind(application, ComponentInitializer.class, "progressbar.js");

			// register the upload status resource
			Application.get().getSharedResources().add(RESOURCE_NAME, new UploadStatusResource());
		}
	}

}
