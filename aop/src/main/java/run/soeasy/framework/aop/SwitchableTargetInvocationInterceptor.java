package run.soeasy.framework.aop;

import lombok.Data;
import run.soeasy.framework.core.execute.Invocation;

@Data
public class SwitchableTargetInvocationInterceptor implements InvocationInterceptor {
	private final Object target;

	@Override
	public Object intercept(Invocation executor) throws Throwable {
		executor.setTarget(target);
		return executor.execute();
	}

}
