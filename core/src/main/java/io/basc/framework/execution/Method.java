package io.basc.framework.execution;

import io.basc.framework.util.element.Elements;

/**
 * 方法的定义
 * 
 * @author wcnnkh
 *
 */
public interface Method extends Function, Invoker {
	@Override
	default Object execute(Elements<? extends Object> args) throws Throwable {
		return execute(getTarget(), args);
	}

	default Object execute(Object target) throws Throwable {
		return execute(target, Elements.empty(), Elements.empty());
	}

	default Object execute(Object target, Elements<? extends Class<?>> parameterTypes, Elements<? extends Object> args)
			throws Throwable {
		if (!canExecuted(parameterTypes)) {
			throw new IllegalArgumentException("Parameter type mismatch");
		}

		return execute(target, args);
	}

	Object execute(Object target, Elements<? extends Object> args) throws Throwable;

	default Object execute(Object target, Parameters parameters) throws Throwable {
		return execute(target, parameters.getTypes(), parameters.getArgs());
	}

	Object getTarget();

	void setTarget(Object target);
}
