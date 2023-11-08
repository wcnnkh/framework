package io.basc.framework.execution.aop;

import java.io.Serializable;

import io.basc.framework.execution.MethodExecutor;
import io.basc.framework.util.element.Elements;
import lombok.Data;

@Data
public class DelegatedObjectExecutionInterceptor implements MethodExecutionInterceptor, Serializable {
	private static final long serialVersionUID = 1L;
	private final String id;

	@Override
	public Object intercept(MethodExecutor executor, Elements<Object> args) throws Throwable {
		if (executor.getParameterDescriptors().isEmpty()
				&& executor.getName().equals(DelegatedObject.PROXY_CONTAINER_ID_METHOD_NAME)) {
			return id;
		}
		return executor.execute(args);
	}

}
