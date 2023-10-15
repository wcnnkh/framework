package io.basc.framework.rmi.beans;

import java.rmi.Naming;
import java.util.HashMap;
import java.util.Map;

import io.basc.framework.context.annotation.Component;
import io.basc.framework.core.annotation.AnnotatedElementUtils;
import io.basc.framework.execution.Executor;
import io.basc.framework.execution.aop.ExecutionInterceptor;
import io.basc.framework.execution.reflect.ReflectionMethodExecutor;
import io.basc.framework.util.element.Elements;

@Component
public class RmiClientExecutionInterceptor implements ExecutionInterceptor {
	private volatile Map<String, Object> clientMap = new HashMap<>();

	@Override
	public Object intercept(Executor executor, Elements<? extends Object> args) throws Throwable {
		if (executor instanceof ReflectionMethodExecutor) {
			return execute((ReflectionMethodExecutor) executor, args);
		}

		return executor.execute(args);
	}

	protected Object execute(ReflectionMethodExecutor executor, Elements<? extends Object> args) throws Throwable {
		RmiClient rmiClient = AnnotatedElementUtils.getMergedAnnotation(executor.getReturnTypeDescriptor(),
				RmiClient.class);
		if (rmiClient == null) {
			return executor.execute(args);
		}

		String name = "rmi:" + rmiClient.host() + "/" + executor.getName();
		Object client = clientMap.get(name);
		if (client == null) {
			synchronized (clientMap) {
				client = clientMap.get(name);
				if (client == null) {
					client = Naming.lookup(name);
					clientMap.put(name, client);
				}
			}
		}
		return executor.execute(client, args);
	}
}