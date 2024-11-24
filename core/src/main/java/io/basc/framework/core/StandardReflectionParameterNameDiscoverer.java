/*
 * Copyright 2002-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.basc.framework.core;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class StandardReflectionParameterNameDiscoverer implements ParameterNameDiscoverer {

	@Override
	
	public String[] getParameterNames(Method method) {
		return getParameterNames(method.getParameters());
	}

	@Override
	
	public String[] getParameterNames(Constructor<?> ctor) {
		return getParameterNames(ctor.getParameters());
	}

	
	private String[] getParameterNames(Parameter[] parameters) {
		String[] parameterNames = new String[parameters.length];
		for (int i = 0; i < parameters.length; i++) {
			Parameter param = parameters[i];
			if (!param.isNamePresent()) {
				return null;
			}
			parameterNames[i] = param.getName();
		}
		return parameterNames;
	}

}
