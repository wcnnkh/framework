package run.soeasy.framework.core.invoke.intercept;

import lombok.Data;
import lombok.NonNull;
import run.soeasy.framework.core.invoke.Invocation;

@Data
public class SwitchableTargetInvocationInterceptor implements InvocationInterceptor {
	private final Object target;

	@Override
	public Object intercept(Invocation executor, @NonNull Object... args) throws Throwable {
		executor.setTarget(target);
		return executor.execute(args);
	}

}
