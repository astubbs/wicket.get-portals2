/*
 * $Id: UploadPage.java 4619 2006-02-23 14:25:06 -0800 (Thu, 23 Feb 2006)
 * jdonnerstag $ $Revision$ $Date: 2006-02-23 14:25:06 -0800 (Thu, 23
 * Feb 2006) $
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
package wicket.examples.upload;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wicket.MarkupContainer;
import wicket.PageParameters;
import wicket.examples.WicketExamplePage;
import wicket.extensions.ajax.markup.html.form.upload.UploadProgressBar;
import wicket.markup.html.basic.Label;
import wicket.markup.html.form.Form;
import wicket.markup.html.form.upload.FileUpload;
import wicket.markup.html.form.upload.FileUploadField;
import wicket.markup.html.link.Link;
import wicket.markup.html.list.ListItem;
import wicket.markup.html.list.ListView;
import wicket.markup.html.panel.FeedbackPanel;
import wicket.model.IModel;
import wicket.model.LoadableDetachableModel;
import wicket.util.file.Files;
import wicket.util.file.Folder;
import wicket.util.lang.Bytes;

/**
 * Upload example.
 * 
 * @author Eelco Hillenius
 */
public class UploadPage extends WicketExamplePage
{
	/**
	 * List view for files in upload folder.
	 */
	private class FileListView extends ListView<File>
	{
		/**
		 * Construct.
		 * 
		 * @param parent
		 *            The parent
		 * 
		 * @param name
		 *            Component name
		 * @param files
		 *            The file list model
		 */
		public FileListView(final MarkupContainer parent, final String name,
				final IModel<List<File>> files)
		{
			super(parent, name, files);
		}

		/**
		 * @see ListView#populateItem(ListItem)
		 */
		@Override
		protected void populateItem(final ListItem<File> listItem)
		{
			final File file = listItem.getModelObject();
			new Label(listItem, "file", file.getName());
			new Link(listItem, "delete")
			{
				@Override
				public void onClick()
				{
					Files.remove(file);
					UploadPage.this.info("Deleted " + file);
				}
			};
		}
	}

	/**
	 * Form for uploads.
	 */
	private class FileUploadForm extends Form
	{
		private FileUploadField fileUploadField;

		/**
		 * Construct.
		 * 
		 * @param parent
		 *            The parent
		 * 
		 * @param name
		 *            Component name
		 */
		public FileUploadForm(final MarkupContainer parent, final String name)
		{
			super(parent, name);

			// set this form to multipart mode (allways needed for uploads!)
			setMultiPart(true);

			// Add one file input field
			fileUploadField = new FileUploadField(this, "fileInput");

			// Set maximum size to 100K for demo purposes
			setMaxSize(Bytes.kilobytes(100));
		}

		/**
		 * @see wicket.markup.html.form.Form#onSubmit()
		 */
		@Override
		protected void onSubmit()
		{
			final FileUpload upload = fileUploadField.getFileUpload();
			if (upload != null)
			{
				// Create a new file
				File newFile = new File(getUploadFolder(), upload.getClientFileName());

				// Check new file, delete if it allready existed
				checkFileExists(newFile);
				try
				{
					// Save to new file
					newFile.createNewFile();
					upload.writeTo(newFile);

					UploadPage.this.info("saved file: " + upload.getClientFileName());
				}
				catch (Exception e)
				{
					throw new IllegalStateException("Unable to write file");
				}
			}
		}
	}

	/** Log. */
	@SuppressWarnings("unused")
	private static final Logger log = LoggerFactory.getLogger(UploadPage.class);

	/** Reference to listview for easy access. */
	private final FileListView fileListView;

	/**
	 * Constructor.
	 * 
	 * @param parameters
	 *            Page parameters
	 */
	public UploadPage(final PageParameters parameters)
	{
		Folder uploadFolder = getUploadFolder();

		// Create feedback panels
		new FeedbackPanel(this, "uploadFeedback");

		// Add simple upload form, which is hooked up to its feedback panel by
		// virtue of that panel being nested in the form.
		new FileUploadForm(this, "simpleUpload");

		// Add folder view
		new Label(this, "dir", uploadFolder.getAbsolutePath());
		fileListView = new FileListView(this, "fileList", new LoadableDetachableModel<List<File>>()
		{
			@Override
			protected List<File> load()
			{
				return Arrays.asList(getUploadFolder().listFiles());
			}
		});

		// Add upload form with ajax progress bar
		final FileUploadForm ajaxSimpleUploadForm = new FileUploadForm(this, "ajax-simpleUpload");
		new UploadProgressBar(ajaxSimpleUploadForm, "progress", ajaxSimpleUploadForm);

	}

	/**
	 * Check whether the file allready exists, and if so, try to delete it.
	 * 
	 * @param newFile
	 *            the file to check
	 */
	private void checkFileExists(final File newFile)
	{
		if (newFile.exists())
		{
			// Try to delete the file
			if (!Files.remove(newFile))
			{
				throw new IllegalStateException("Unable to overwrite " + newFile.getAbsolutePath());
			}
		}
	}

	private Folder getUploadFolder()
	{
		return UploadApplication.get().getUploadFolder();
	}
}