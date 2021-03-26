package scw.rpc.support;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

public class ServiceCallable implements Callable<Object> {
	private final Object instance;
	private final Method method;
	private final Object[] args;

	public ServiceCallable(Object instance, Method method, Object[] args) {
		this.method = method;
		this.instance = instance;
		this.args = args;
	}

	public Object call() throws Exception {
		return method.invoke(instance, args);
	}

}
