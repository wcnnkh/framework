package io.basc.framework.core.execution.aop;

import io.basc.framework.core.execution.Method;
import lombok.Data;
import lombok.NonNull;

@Data
public class SwitchableTargetExecutionInterceptor implements MethodExecutionInterceptor {
	private final Object target;

	@Override
	public Object intercept(Method executor, @NonNull Object... args) throws Throwable {
		executor.setTarget(target);
		return executor.execute(args);
	}

}
