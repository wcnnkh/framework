package io.basc.framework.core.execution.aop;

import io.basc.framework.core.execution.Executor;
import io.basc.framework.util.Elements;

public interface Proxy extends Executor {
	@Override
	default Object execute() {
		return execute(Elements.empty(), Elements.empty());
	}

	@Override
	Object execute(Elements<? extends Class<?>> parameterTypes, Elements<? extends Object> args);
}
