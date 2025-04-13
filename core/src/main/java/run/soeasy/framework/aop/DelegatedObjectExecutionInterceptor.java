package run.soeasy.framework.aop;

import java.io.Serializable;

import lombok.Data;
import lombok.NonNull;
import run.soeasy.framework.core.execution.Method;

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
