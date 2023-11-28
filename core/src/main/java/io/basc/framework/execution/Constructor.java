package io.basc.framework.execution;

import io.basc.framework.execution.param.Parameters;

/**
 * 构造器抽象定义
 */
public interface Constructor extends Constructable {
	@Override
	default boolean canExecuted() {
		return canExecuted(getParameters());
	}

	boolean canExecuted(Parameters parameters);

	@Override
	default java.lang.Object execute() throws Throwable {
		return execute(getParameters());
	}

	Object execute(Parameters parameters) throws Throwable;

	Parameters getParameters();

	void setParameters(Parameters parameters);
}
