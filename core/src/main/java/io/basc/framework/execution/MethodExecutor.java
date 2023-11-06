package io.basc.framework.execution;

public interface MethodExecutor extends Method, Executor {
	Object getTarget();

	void setTarget(Object target);

	@Override
	default Object execute(Object[] args) throws Throwable {
		return execute(getTarget(), args);
	}
}