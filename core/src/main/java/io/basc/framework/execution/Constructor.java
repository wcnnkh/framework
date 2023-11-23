package io.basc.framework.execution;

import io.basc.framework.mapper.ParameterDescriptor;
import io.basc.framework.util.element.Elements;

/**
 * 可以理解为具体的构造方法
 */
public interface Constructor extends Constructable, Executor {

	public static boolean test(Elements<? extends ParameterDescriptor> parameterDescriptors,
			Elements<Class<?>> parameterTypes) {
		return parameterDescriptors.map((e) -> e.getTypeDescriptor().getType()).equals(parameterTypes,
				Class::isAssignableFrom);
	}

	@Override
	default boolean canExecuted(Elements<Class<?>> parameterTypes) {
		return test(getParameterDescriptors(), parameterTypes);
	}

	@Override
	default Object execute(Elements<Class<?>> parameterTypes, Elements<Object> args) throws Throwable {
		if (!canExecuted(parameterTypes)) {
			throw new IllegalArgumentException("Parameter type mismatch");
		}

		return execute(args);
	}

	@Override
	default boolean canExecuted() {
		return Executor.super.canExecuted();
	}

	@Override
	default Object execute() throws Throwable {
		return Executor.super.execute();
	}

}
