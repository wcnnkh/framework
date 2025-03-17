/*
 * Copyright 2002-2020 the original author or authors.
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

package run.soeasy.framework.core;

import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.NonNull;
import run.soeasy.framework.util.spi.NativeServiceLoader;

public class PrioritizedParameterNameDiscoverer implements ParameterNameDiscoverer {
	private static final ParameterNameDiscoverer[] PARAMETER_NAME_DISCOVERERS = NativeServiceLoader
			.load(ParameterNameDiscoverer.class).toArray(ParameterNameDiscoverer[]::new);

	private final List<ParameterNameDiscoverer> parameterNameDiscoverers = new ArrayList<>(2);

	public void addDiscoverer(ParameterNameDiscoverer pnd) {
		this.parameterNameDiscoverers.add(pnd);
	}

	protected String[] getParameterNames(List<ParameterNameDiscoverer> parameterNameDiscoverers, Method method) {
		for (ParameterNameDiscoverer pnd : parameterNameDiscoverers) {
			String[] result = pnd.getParameterNames(method);
			if (result != null) {
				return result;
			}
		}
		return null;
	}

	@Override
	public String[] getParameterNames(@NonNull Executable executable) {
		String[] result = getParameterNames(Arrays.asList(PARAMETER_NAME_DISCOVERERS), executable);
		if (result == null) {
			result = getParameterNames(this.parameterNameDiscoverers, executable);
		}
		return result;
	}

	protected String[] getParameterNames(List<ParameterNameDiscoverer> parameterNameDiscoverers,
			Executable executable) {
		for (ParameterNameDiscoverer pnd : parameterNameDiscoverers) {
			String[] result = pnd.getParameterNames(executable);
			if (result != null) {
				return result;
			}
		}
		return null;
	}
}
