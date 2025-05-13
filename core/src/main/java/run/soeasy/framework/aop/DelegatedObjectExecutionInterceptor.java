package run.soeasy.framework.aop;

import java.io.Serializable;

import lombok.Data;
import lombok.NonNull;
import run.soeasy.framework.core.invoke.Invocation;
import run.soeasy.framework.core.invoke.intercept.InvocationInterceptor;

@Data
class DelegatedObjectExecutionInterceptor implements InvocationInterceptor, Serializable {
	private static final long serialVersionUID = 1L;
	private final String id;

	@Override
	public Object intercept(@NonNull Invocation executor, @NonNull Object... args) throws Throwable {
		if (executor.getParameterTemplate().isEmpty()
				&& executor.getName().equals(DelegatedObject.PROXY_CONTAINER_ID_METHOD_NAME)) {
			return id;
		}
		return executor.execute(args);
	}

}
