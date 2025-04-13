package run.soeasy.framework.aop;

import lombok.Data;
import lombok.NonNull;
import run.soeasy.framework.core.execution.Method;

@Data
public class SwitchableTargetExecutionInterceptor implements MethodExecutionInterceptor {
	private final Object target;

	@Override
	public Object intercept(Method executor, @NonNull Object... args) throws Throwable {
		executor.setTarget(target);
		return executor.execute(args);
	}

}
