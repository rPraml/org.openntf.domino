/*
 * Copyright 2015 - FOCONIS AG
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at:
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express o 
 * implied. See the License for the specific language governing 
 * permissions and limitations under the License.
 * 
 */
package org.openntf.domino.commons;

import java.util.List;
import java.util.ServiceLoader;

/**
 * A DataConverter. You can register your own dataConverters as Service. See {@link ServiceLoader}
 * 
 * 
 * TODO: This is work in progress
 * 
 * @author Roland Praml, FOCONIS AG
 */
public interface IDataConverter<T> {

	public enum $ {
		;
		@SuppressWarnings("rawtypes")
		public static List<IDataConverter> getInstances() {
			return ServiceLocator.findApplicationServices(IDataConverter.class);
		}
	}

	T convertTo(Object o);

	Class<? extends T> getType();
}
