package io.basc.framework.execution;

import io.basc.framework.util.element.Elements;

public interface Service extends Executable {
	@Override
	default boolean canExecuted() {
		return canExecuted(Elements.empty());
	}

	@Override
	default Object execute() throws Throwable {
		return execute(Elements.empty(), Elements.empty());
	}

	boolean canExecuted(Elements<Class<?>> parameterTypes);

	Object execute(Elements<Class<?>> parameterTypes, Elements<Object> args) throws Throwable;
}
