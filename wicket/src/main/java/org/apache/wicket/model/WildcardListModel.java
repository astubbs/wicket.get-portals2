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
package org.apache.wicket.model;

import java.util.ArrayList;
import java.util.List;


/**
 * Based on <code>Model</code> but for lists of serializable objects.
 *
 * @author Timo Rantalaiho
 */
public class WildcardListModel<T> extends CollectionModelBase<List<? extends T>>
{
    public WildcardListModel()
    {
    }

    public WildcardListModel(List<? extends T> object)
    {
        setObject(object);
    }


    @Override
    protected List<? extends T> createSerializableVersionOf(List<? extends T> object)
    {
        return new ArrayList<T>(object);
    }
}