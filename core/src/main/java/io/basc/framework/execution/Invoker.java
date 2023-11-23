package io.basc.framework.execution;

import io.basc.framework.mapper.ParameterDescriptor;
import io.basc.framework.util.element.Elements;

public interface Invoker extends Invocation {
	/**
	 * 执行需要的参数描述
	 * 
	 * @return
	 */
	Elements<ParameterDescriptor> getParameterDescriptors();

	public static boolean test(Elements<ParameterDescriptor> parameterDescriptors, Elements<Parameter> parameters) {
		return false;
	}

	public static Elements<Object> accept(Elements<ParameterDescriptor> parameterDescriptors,
			Elements<Parameter> parameters) {
		return null;
	}
}
