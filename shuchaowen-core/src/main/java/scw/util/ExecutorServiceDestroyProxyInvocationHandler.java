package scw.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.ExecutorService;

import scw.beans.Destroy;

public abstract class ExecutorServiceDestroyProxyInvocationHandler<T extends ExecutorService>
		implements InvocationHandler, Destroy {
	private T executorService;

	public ExecutorServiceDestroyProxyInvocationHandler(T executorService) {
		this.executorService = executorService;
	}

	public T getTargetExecutorService() {
		return executorService;
	}

	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		if (args == null || args.length == 0) {
			if (method.getName().equals("getTargetExecutorService")) {
				return executorService;
			} else if (method.getName().equals("destroy")) {
				destroy();
				return null;
			}
		}
		return method.invoke(executorService, args);
	}
}
