package io.basc.framework.execution.aop;

import io.basc.framework.execution.MethodExecutor;
import io.basc.framework.util.element.Elements;
import lombok.Data;

@Data
public class SwitchableTargetExecutionInterceptor implements MethodExecutionInterceptor {
	private final Object target;

	@Override
	public Object intercept(MethodExecutor executor, Elements<Object> args) throws Throwable {
		executor.setTarget(target);
		return executor.execute(args);
	}

}
