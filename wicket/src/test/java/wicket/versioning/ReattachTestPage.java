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
package wicket.versioning;

import wicket.markup.html.WebPage;
import wicket.markup.html.basic.Label;

/**
 * @author jcompagner
 */
public class ReattachTestPage extends WebPage
{
	private static final long serialVersionUID = 1L;
	private Label label1;
	private Label label2;
	
	/**
	 * Construct.
	 */
	public ReattachTestPage()
	{
		label1 = new Label(this,"label","label1");
		label2 = new Label(this,"label","label2");
	}
	
	/**
	 * 
	 */
	public void reattachLabel1()
	{
		label1.reAttach();
	}
}