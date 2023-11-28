package io.basc.framework.execution;

import io.basc.framework.execution.param.Parameters;
import io.basc.framework.util.element.Elements;

public interface Service<E extends Constructor> extends Constructor {
	Elements<E> getConstructors();

	@Override
	default boolean canExecuted(Elements<Class<?>> parameterTypes) {
		for (E executor : getConstructors()) {
			if (executor.canExecuted(parameterTypes)) {
				return true;
			}
		}
		return false;
	}

	@Override
	default Object execute(Elements<Class<?>> parameterTypes, Elements<Object> args)
			throws Throwable, NoSuchConstructorException {
		for (E executor : getConstructors()) {
			if (executor.canExecuted(parameterTypes)) {
				return executor.execute(parameterTypes, args);
			}
		}
		throw new NoSuchConstructorException(parameterTypes);
	}

	default boolean canExecuted(Parameters parameters) {
		for (E executor : getConstructors()) {
			if (executor.canExecuted(parameters)) {
				return true;
			}
		}
		return false;
	}

	default Object execute(Parameters parameters) throws Throwable, NoSuchConstructorException {
		for (E executor : getConstructors()) {
			if (executor.canExecuted(parameters)) {
				return executor.execute(parameters);
			}
		}
		throw new NoSuchConstructorException(parameters);
	}

	@Override
	default Object execute() throws Throwable, NoSuchConstructorException {
		return Constructor.super.execute();
	}

}
