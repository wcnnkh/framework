package io.basc.framework.core.execution.aop;

import io.basc.framework.core.execution.Method;
import io.basc.framework.util.Elements;
import lombok.Data;

@Data
public class SwitchableTargetExecutionInterceptor implements MethodExecutionInterceptor {
	private final Object target;

	@Override
	public Object intercept(Method executor, Elements<? extends Object> args) throws Throwable {
		executor.setTarget(target);
		return executor.execute(args);
	}

}
