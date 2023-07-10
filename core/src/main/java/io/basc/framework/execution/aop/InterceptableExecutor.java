package io.basc.framework.execution.aop;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.execution.Executor;
import io.basc.framework.mapper.ParameterDescriptor;
import io.basc.framework.util.Elements;
import lombok.Data;

@Data
public class InterceptableExecutor implements Executor {
	private final Executor executor;
	private final ExecutionInterceptor executionInterceptor;

	@Override
	public TypeDescriptor getSource() {
		return executor.getSource();
	}

	@Override
	public String getName() {
		return executor.getName();
	}

	@Override
	public TypeDescriptor getReturnTypeDescriptor() {
		return executor.getReturnTypeDescriptor();
	}

	@Override
	public Elements<? extends ParameterDescriptor> getParameterDescriptors() {
		return executor.getParameterDescriptors();
	}

	@Override
	public Object execute(Elements<? extends Object> args) throws Throwable {
		return executionInterceptor.intercept(executor, args);
	}

}
