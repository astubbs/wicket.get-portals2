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
package wicket.markup.html;

import java.util.HashSet;
import java.util.Set;

import wicket.util.lang.Packages;

/**
 * Default implementation of {@link IPackageResourceGuard}. By default, the
 * extensions 'properties', 'html', 'class' and 'java' are blocked.
 * 
 * @author eelcohillenius
 */
public class PackageResourceGuard implements IPackageResourceGuard
{
	/** Set of extensions that are not allowed access. */
	private Set blockedExtensions = new HashSet(4);

	/**
	 * Construct.
	 */
	public PackageResourceGuard()
	{
		blockedExtensions.add("properties");
		blockedExtensions.add("class");
		blockedExtensions.add("java");
	}

	/**
	 * @see wicket.markup.html.IPackageResourceGuard#accept(java.lang.Class,
	 *      java.lang.String)
	 */
	public boolean accept(Class scope, String path)
	{
		String absolutePath = Packages.absolutePath(scope, path);
		return acceptAbsolutePath(absolutePath);
	}

	/**
	 * Whether the provided absolute path is accepted.
	 * 
	 * @param path
	 *            The absolute path, starting from the class root (packages are
	 *            seperated with forward slashes instead of dots).
	 * @return True if accepted, false otherwise.
	 */
	protected boolean acceptAbsolutePath(String path)
	{
		int ixExtension = path.lastIndexOf('.');
		int len = path.length();
		final String ext;
		if (ixExtension <= 0 || ixExtension == len || (path.lastIndexOf('/') + 1) == ixExtension)
		{
			ext = null;
		}
		else
		{
			ext = path.substring(ixExtension + 1).toLowerCase();
		}
		if ("html".equals(ext) && getClass().getClassLoader().getResource(path.replaceAll(".html", ".class")) != null)
		{
			return false;
		}
		return acceptExtension(ext);
	}

	/**
	 * Whether the provided extension is accepted.
	 * 
	 * @param extension
	 *            The extension, starting from the class root (packages are
	 *            seperated with forward slashes instead of dots).
	 * @return True if accepted, false otherwise.
	 */
	protected boolean acceptExtension(String extension)
	{
		return (!blockedExtensions.contains(extension));
	}

	/**
	 * Gets the set of extensions that are not allowed access.
	 * 
	 * @return The set of extensions that are not allowed access
	 */
	protected final Set getBlockedExtensions()
	{
		return blockedExtensions;
	}

	/**
	 * Sets the set of extensions that are not allowed access.
	 * 
	 * @param blockedExtensions
	 *            Set of extensions that are not allowed access
	 */
	protected final void setBlockedExtensions(Set blockedExtensions)
	{
		this.blockedExtensions = blockedExtensions;
	}
}
