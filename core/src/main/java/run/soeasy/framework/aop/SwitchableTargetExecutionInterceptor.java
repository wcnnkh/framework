package run.soeasy.framework.aop;

import lombok.Data;
import lombok.NonNull;
import run.soeasy.framework.core.exe.Invocation;

@Data
public class SwitchableTargetExecutionInterceptor implements MethodExecutionInterceptor {
	private final Object target;

	@Override
	public Object intercept(Invocation executor, @NonNull Object... args) throws Throwable {
		executor.setTarget(target);
		return executor.execute(args);
	}

}
