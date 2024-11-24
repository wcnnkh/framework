package io.basc.framework.core.execution;

import io.basc.framework.core.execution.param.Parameters;
import io.basc.framework.util.Elements;

public interface Executor extends Executed {
	default Object execute() throws Throwable {
		return execute(Elements.empty(), Elements.empty());
	}

	Object execute(Elements<? extends Class<?>> parameterTypes, Elements<? extends Object> args) throws Throwable;

	default Object execute(Parameters parameters) throws Throwable{
		return execute(parameters.getTypes(), parameters.getArgs());
	}
}