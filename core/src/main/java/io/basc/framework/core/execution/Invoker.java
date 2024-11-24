package io.basc.framework.core.execution;

import io.basc.framework.core.execution.param.Parameters;
import io.basc.framework.util.Elements;

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
