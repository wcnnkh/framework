package io.basc.framework.execution.aop;

import java.io.Serializable;

import io.basc.framework.execution.Executor;
import io.basc.framework.util.element.Elements;
import lombok.Data;

@Data
public class DelegatedObjectExecutionInterceptor implements ExecutionInterceptor, Serializable {
	private static final long serialVersionUID = 1L;
	private final String id;

	@Override
	public Object intercept(Executor executor, Elements<? extends Object> args) throws Throwable {
		if (executor.getParameterDescriptors().isEmpty()
				&& executor.getName().equals(DelegatedObject.PROXY_CONTAINER_ID_METHOD_NAME)) {
			return id;
		}
		return executor.execute(args);
	}

}
