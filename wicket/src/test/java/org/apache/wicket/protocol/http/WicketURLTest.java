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
package org.apache.wicket.protocol.http;

import junit.framework.TestCase;

/**
 * @author Doug Donohoe
 */
public class WicketURLTest extends TestCase
{
    public void testPathEncoder()
    {
        assertEquals("+", WicketURLEncoder.PATH_INSTANCE.encode("+", "UTF-8"));
        assertEquals("%20", WicketURLEncoder.PATH_INSTANCE.encode(" ", "UTF-8"));
    }

    public void testQueryEncoder()
    {
        assertEquals("+", WicketURLEncoder.QUERY_INSTANCE.encode(" ", "UTF-8"));
        assertEquals("%2B", WicketURLEncoder.QUERY_INSTANCE.encode("+", "UTF-8"));
    }

    public void testPathDecoder()
    {
        assertEquals("+", WicketURLDecoder.PATH_INSTANCE.decode("+", "UTF-8"));
        assertEquals(" ", WicketURLDecoder.PATH_INSTANCE.decode("%20", "UTF-8"));
    }

    public void testQueryDecoder()
    {
        assertEquals(" ", WicketURLDecoder.QUERY_INSTANCE.decode("+", "UTF-8"));
        assertEquals("+", WicketURLDecoder.QUERY_INSTANCE.decode("%2B", "UTF-8"));
    }
}
