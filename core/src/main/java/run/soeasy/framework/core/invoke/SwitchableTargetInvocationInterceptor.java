package run.soeasy.framework.core.invoke;

import lombok.Data;

@Data
public class SwitchableTargetInvocationInterceptor implements InvocationInterceptor {
	private final Object target;

	@Override
	public Object intercept(Invocation executor) throws Throwable {
		executor.setTarget(target);
		return executor.execute();
	}

}
