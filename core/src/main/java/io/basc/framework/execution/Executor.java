package io.basc.framework.execution;

import io.basc.framework.mapper.ParameterDescriptor;
import io.basc.framework.util.element.Elements;

public interface Executor extends Invocation, Executable {
	/**
	 * 执行需要的参数描述
	 * 
	 * @return
	 */
	Elements<ParameterDescriptor> getParameterDescriptors();

	/**
	 * 执行
	 * 
	 * @param args
	 * @return
	 */
	Object execute(Elements<Object> args) throws Throwable;

	@Override
	default boolean test(Elements<Parameter> parameters) {
		return Invocation.test(getParameterDescriptors(), parameters);
	}

	@Override
	default Object process(Elements<Parameter> parameters) throws Throwable {
		Elements<Object> args = Invocation.accept(getParameterDescriptors(), parameters);
		return execute(args);
	}
}
