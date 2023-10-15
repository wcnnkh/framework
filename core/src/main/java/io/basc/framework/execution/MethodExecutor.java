package io.basc.framework.execution;

import io.basc.framework.util.element.Elements;

public interface MethodExecutor extends Method, Executor {
	Object getTarget();

	void setTarget(Object target);

	@Override
	default Object execute(Elements<? extends Object> args) throws Throwable {
		return execute(getTarget(), args);
	}
}