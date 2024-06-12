package io.basc.framework.execution;

import io.basc.framework.execution.param.Parameters;
import io.basc.framework.util.element.Elements;

public interface Invoker extends Executed {
	default Object execute(Object target) throws Throwable {
		return execute(target, Elements.empty(), Elements.empty());
	}

	Object execute(Object target, Elements<? extends Class<?>> parameterTypes, Elements<? extends Object> args)
			throws Throwable;

	default Object execute(Object target, Parameters parameters) throws Throwable {
		return execute(target, parameters.getTypes(), parameters.getArgs());
	}
}
