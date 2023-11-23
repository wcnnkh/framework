package io.basc.framework.execution.aop;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.execution.Executor;
import io.basc.framework.execution.Parameter;
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

	@Override
	public Elements<Parameter> getParameters() {
		return wrappedTarget.getParameters();
	}

	@Override
	public void setParameters(Elements<Parameter> parameters) {
		wrappedTarget.setParameters(parameters);
	}

	@Override
	public boolean test(Elements<Parameter> parameters) {
		return wrappedTarget.test(parameters);
	}

	@Override
	public Object process(Elements<Parameter> parameters) throws Throwable {
		return wrappedTarget.process(parameters);
	}

	@Override
	public Object execute() throws Throwable {
		return wrappedTarget.execute();
	}

	@Override
	public boolean canExecuted() {
		return wrappedTarget.canExecuted();
	}
}
