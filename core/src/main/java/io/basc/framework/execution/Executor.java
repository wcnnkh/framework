package io.basc.framework.execution;

import io.basc.framework.mapper.ParameterDescriptor;
import io.basc.framework.util.element.Elements;

public interface Executor extends Executable {
	/**
	 * 执行需要的参数描述
	 * 
	 * @return
	 */
	Elements<ParameterDescriptor> getParameterDescriptors();

	@Override
	default boolean canExecuted() {
		return getParameterDescriptors().isEmpty();
	}

	@Override
	default Object execute() throws Throwable {
		return execute(Elements.empty());
	}

	/**
	 * 执行
	 * 
	 * @param args
	 * @return
	 */
	Object execute(Elements<Object> args) throws Throwable;
}
