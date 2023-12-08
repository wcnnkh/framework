package io.basc.framework.execution;

import io.basc.framework.execution.param.ParameterMatchingResults;
import io.basc.framework.execution.param.Parameters;
import io.basc.framework.mapper.ParameterDescriptor;
import io.basc.framework.util.element.Elements;

/**
 * 所有执行器的基类
 */
public interface Executor extends Constructor {
	@Override
	default boolean canExecuted(Elements<? extends Class<?>> parameterTypes) {
		return getParameterDescriptors().map((e) -> e.getTypeDescriptor().getType()).equals(parameterTypes,
				Class::isAssignableFrom);
	}

	default boolean canExecuted(Parameters parameters) {
		ParameterMatchingResults results = parameters.apply(getParameterDescriptors());
		return results.isSuccessful();
	}

	@Override
	default Object execute(Elements<? extends Class<?>> parameterTypes, Elements<? extends Object> args)
			throws Throwable {
		if (!canExecuted(parameterTypes)) {
			throw new IllegalArgumentException("Parameter type mismatch");
		}

		return execute(args);
	}

	/**
	 * 执行
	 * 
	 * @param args
	 * @return
	 */
	Object execute(Elements<? extends Object> args) throws Throwable;

	default Object execute(Parameters parameters) throws Throwable {
		ParameterMatchingResults results = parameters.apply(getParameterDescriptors());
		if (!results.isSuccessful()) {
			throw new IllegalArgumentException("Parameter mismatch");
		}

		Elements<Object> args = results.getParameters();
		return execute(args);
	}

	/**
	 * 执行需要的参数描述
	 * 
	 * @return
	 */
	Elements<ParameterDescriptor> getParameterDescriptors();
}
