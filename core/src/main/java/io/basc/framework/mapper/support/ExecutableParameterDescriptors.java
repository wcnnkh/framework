package io.basc.framework.mapper.support;

import java.lang.reflect.Executable;

import io.basc.framework.core.ParameterNameDiscoverer;
import io.basc.framework.mapper.ParameterUtils;

public class ExecutableParameterDescriptors extends DefaultParameterDescriptors<Executable> {

	public ExecutableParameterDescriptors(Class<?> targetClass, Executable executable) {
		this(ParameterUtils.getParameterNameDiscoverer(), targetClass, executable);
	}

	public ExecutableParameterDescriptors(ParameterNameDiscoverer parameterNameDiscoverer, Class<?> targetClass,
			Executable executable) {
		super(targetClass, executable, executable, ParameterUtils.getParameters(parameterNameDiscoverer, executable));
	}
}
