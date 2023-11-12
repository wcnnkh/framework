package io.basc.framework.execution.aop;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.execution.Executor;
import io.basc.framework.mapper.ParameterDescriptor;
import io.basc.framework.util.Wrapper;
import io.basc.framework.util.element.Elements;

public class ExecutorWrapper extends Wrapper<Executor> implements Executor {

	public ExecutorWrapper(Executor wrappedTarget) {
		super(wrappedTarget);
	}

	@Override
	public TypeDescriptor getReturnTypeDescriptor() {
		return wrappedTarget.getReturnTypeDescriptor();
	}

	@Override
	public Elements<ParameterDescriptor> getParameterDescriptors() {
		return wrappedTarget.getParameterDescriptors();
	}

	@Override
	public Object execute(Elements<Object> args) throws Throwable {
		return wrappedTarget.execute(args);
	}
}
