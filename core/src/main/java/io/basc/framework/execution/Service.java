package io.basc.framework.execution;

import java.util.NoSuchElementException;

import io.basc.framework.util.element.Elements;

public interface Service<E extends Executor> extends Constructable, Invocation {
	Elements<E> getElements();

	@Override
	default boolean canExecuted() {
		return Invocation.super.canExecuted();
	}

	@Override
	default Object execute() throws Throwable {
		return Invocation.super.execute();
	}

	@Override
	default boolean test(Elements<Parameter> parameters) {
		for (E executor : getElements()) {
			if (executor.test(parameters)) {
				return true;
			}
		}
		return false;
	}

	@Override
	default Object process(Elements<Parameter> parameters) throws Throwable, NoSuchElementException {
		for (E executor : getElements()) {
			if (executor.test(parameters)) {
				return executor.process(parameters);
			}
		}
		throw new NoSuchElementException("Unable to match to executor");
	}

	@Override
	default boolean canExecuted(Elements<Class<?>> parameterTypes) {
		for (E executor : getElements()) {
			if (Constructor.test(executor.getParameterDescriptors(), parameterTypes)) {
				return true;
			}
		}
		return false;
	}

	@Override
	default Object execute(Elements<Class<?>> parameterTypes, Elements<Object> args)
			throws Throwable, NoSuchElementException {
		for (E executor : getElements()) {
			if (Constructor.test(executor.getParameterDescriptors(), parameterTypes)) {
				return executor.execute(args);
			}
		}
		throw new NoSuchElementException("Unable to match to executor");
	}
}
