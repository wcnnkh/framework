package io.basc.framework.execution.aop;

import io.basc.framework.execution.Executor;
import io.basc.framework.util.element.Elements;
import lombok.Data;

/**
 * @see SwitchableTargetExecutor
 * @author wcnnkh
 *
 */
@Data
public class SwitchableTargetExecutionInterceptor implements ExecutionInterceptor {
	private final Object target;

	@Override
	public Object intercept(Executor executor, Elements<? extends Object> args) throws Throwable {
		if (executor instanceof SwitchableTargetExecutor) {
			((SwitchableTargetExecutor) executor).setTarget(target);
		}
		return executor.execute(args);
	}

}
