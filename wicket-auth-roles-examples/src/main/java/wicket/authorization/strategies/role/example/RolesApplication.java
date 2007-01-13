/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package wicket.authorization.strategies.role.example;

import java.util.Arrays;
import java.util.List;

import wicket.ISessionFactory;
import wicket.Request;
import wicket.Session;
import wicket.authorization.strategies.role.RoleAuthorizationStrategy;
import wicket.authorization.strategies.role.example.pages.AdminBookmarkablePage;
import wicket.authorization.strategies.role.example.pages.AdminInternalPage;
import wicket.authorization.strategies.role.metadata.MetaDataRoleAuthorizationStrategy;
import wicket.protocol.http.WebApplication;

/**
 * Application object for this example.
 * 
 * @author Eelco Hillenius
 */
public class RolesApplication extends WebApplication implements ISessionFactory
{
	/**
	 * User DB.
	 */
	public static List<User> USERS = Arrays.asList(new User[] { new User("jon", "ADMIN"),
			new User("kay", "USER"), new User("pam", "") });

	/**
	 * Construct.
	 */
	public RolesApplication()
	{
		super();
	}

	/**
	 * @see wicket.Application#getHomePage()
	 */
	@Override
	public Class getHomePage()
	{
		return Index.class;
	}

	/**
	 * @see wicket.ISessionFactory#newSession(Request request)
	 */
	public Session newSession(Request request)
	{
		return new RolesSession(this, request);
	}

	@Override
	protected void init()
	{
		setSessionFactory(this);
		getSecuritySettings().setAuthorizationStrategy(
				new RoleAuthorizationStrategy(new UserRolesAuthorizer()));
		MetaDataRoleAuthorizationStrategy.authorize(AdminBookmarkablePage.class, "ADMIN");
		MetaDataRoleAuthorizationStrategy.authorize(AdminInternalPage.class, "ADMIN");
	}

}
