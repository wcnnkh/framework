package io.basc.framework.core.execution.aop;

import java.io.Serializable;

import io.basc.framework.core.execution.Method;
import lombok.Data;
import lombok.NonNull;

@Data
class DelegatedObjectExecutionInterceptor implements MethodExecutionInterceptor, Serializable {
	private static final long serialVersionUID = 1L;
	private final String id;

	@Override
	public Object intercept(@NonNull Method executor, @NonNull Object... args) throws Throwable {
		if (executor.getParameterDescriptors().isEmpty()
				&& executor.getName().equals(DelegatedObject.PROXY_CONTAINER_ID_METHOD_NAME)) {
			return id;
		}
		return executor.execute(args);
	}

}
