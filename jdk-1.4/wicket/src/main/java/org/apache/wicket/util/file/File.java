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
package org.apache.wicket.util.file;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import org.apache.wicket.util.io.Streams;
import org.apache.wicket.util.time.Time;
import org.apache.wicket.util.watch.IModifiable;


/**
 * Simple extension of File that adds an implementation of IModifiable for
 * files. This allows the ModificationWatcher class to watch files for
 * modification. The IModifiable.lastModifiedTime() method also returns a Time
 * object with a more convenient API than either Date or a value in
 * milliseconds.
 * 
 * @author Jonathan Locke
 */
public class File extends java.io.File implements IModifiable
{
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor.
	 * 
	 * @param parent
	 *            parent
	 * @param child
	 *            child
	 */
	public File(final File parent, final String child)
	{
		super(parent, child);
	}

	/**
	 * Construct.
	 * @param parent
	 * @param child
	 */
	public File(final java.io.File parent, final String child)
	{
		super(parent, child);
	}

	/**
	 * Construct.
	 * 
	 * @param file
	 *            File from java.io package
	 */
	public File(final java.io.File file)
	{
		super(file.getAbsolutePath());
	}

	/**
	 * Constructor.
	 * 
	 * @param pathname
	 *            path name
	 */
	public File(final String pathname)
	{
		super(pathname);
	}

	/**
	 * Constructor.
	 * 
	 * @param parent
	 *            parent
	 * @param child
	 *            child
	 */
	public File(final String parent, final String child)
	{
		super(parent, child);
	}

	/**
	 * Constructor.
	 * 
	 * @param uri
	 *            file uri
	 */
	public File(final URI uri)
	{
		super(uri);
	}

    /**
     * @return File extension (whatever is after the last '.' in the file name)
     */
    public String getExtension()
    {
        final int lastDot = getName().lastIndexOf('.');
        if (lastDot >= 0)
        {
            return getName().substring(lastDot + 1);
        }
        return null;
    }
	
	/**
	 * @return Parent folder
	 */
	public Folder getParentFolder()
	{
		return new Folder(getParent());
	}

	/**
	 * Returns a Time object representing the most recent time this file was
	 * modified.
	 * 
	 * @return This file's lastModified() value as a Time object
	 */
	public final Time lastModifiedTime()
	{
		return Time.milliseconds(lastModified());
	}

	/**
	 * @return String read from this file
	 * @throws IOException
	 */
	public final String readString() throws IOException
	{
		final InputStream in = new FileInputStream(this);
		try
		{
			return Streams.readString(in);
		}
		finally
		{
			in.close();
		}
	}

	/**
	 * @return True if the file was removed
	 * @see java.io.File#delete()
	 */
	public boolean remove()
	{
		return Files.remove(this);
	}
	
	/**
	 * Force contents of file to physical storage
	 * @throws IOException 
	 */
	public void sync() throws IOException
	{	
		final FileInputStream in = new FileInputStream(this);
		try
		{
			in.getFD().sync();			
		}
		finally
		{
			in.close();
		}
	}
	
	/**
	 * Writes the given file to this one
	 * 
	 * @param file 
	 *            The file to copy
	 * @throws IOException 
	 */
	public final void write(final File file) throws IOException
	{
        final InputStream in = new BufferedInputStream(new FileInputStream(file));
        try
        {
        	write(in);
        }
        finally
        {
        	in.close();
        }
	}

	/**
	 * Writes the given input stream to this file
	 * 
	 * @param input
	 *            The input
	 * @return Number of bytes written
	 * @throws IOException
	 */
	public final int write(final InputStream input) throws IOException
	{
		return Files.writeTo(this, input);
	}

	/**
	 * Write the given string to this file
	 * 
	 * @param string
	 *            The string to write
	 * @throws IOException
	 */
	public final void write(final String string) throws IOException
	{
		final FileWriter out = new FileWriter(this);
		try
		{
			out.write(string);
		}
		finally
		{
			out.close();
		}
	}
}
