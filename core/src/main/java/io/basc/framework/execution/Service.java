package io.basc.framework.execution;

import java.util.NoSuchElementException;

import io.basc.framework.execution.param.Parameters;
import io.basc.framework.util.element.Elements;

//TODO 自定义一个NoSuchElementException
public interface Service<E extends Executor> extends Constructable, Invocation {
	Elements<E> getElements();

	@Override
	default boolean canExecuted(Elements<Class<?>> parameterTypes) {
		for (Executor executor : getElements()) {
			if (executor.canExecuted(parameterTypes)) {
				return true;
			}
		}
		return false;
	}

	@Override
	default Object execute(Elements<Class<?>> parameterTypes, Elements<Object> args)
			throws Throwable, NoSuchElementException {
		for (Executor executor : getElements()) {
			if (executor.canExecuted(parameterTypes)) {
				return executor.execute(parameterTypes, args);
			}
		}
		throw new NoSuchElementException("Unable to match to executor");
	}

	default boolean canExecuted(Parameters parameters) {
		for (Executor executor : getElements()) {
			if (executor.canExecuted(parameters)) {
				return true;
			}
		}
		return false;
	}

	default Object execute(Parameters parameters) throws Throwable, NoSuchElementException {
		for (Executor executor : getElements()) {
			if (executor.canExecuted(parameters)) {
				return executor.execute(parameters);
			}
		}
		throw new NoSuchElementException("Unable to match to executor");
	}

	@Override
	default boolean canExecuted() {
		return Invocation.super.canExecuted();
	}

	@Override
	default Object execute() throws Throwable {
		return Invocation.super.execute();
	}
}
