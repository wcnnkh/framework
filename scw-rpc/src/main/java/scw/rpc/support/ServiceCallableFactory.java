package scw.rpc.support;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

import scw.instance.NoArgsInstanceFactory;
import scw.rpc.CallableFactory;

public class ServiceCallableFactory implements CallableFactory {
	private final NoArgsInstanceFactory instanceFactory;

	public ServiceCallableFactory(NoArgsInstanceFactory instanceFactory) {
		this.instanceFactory = instanceFactory;
	}

	public Callable<Object> getCallable(Class<?> clazz, Method method,
			Object[] args) {
		Object instance = instanceFactory.getInstance(clazz);
		return new ServiceCallable(instance, method, args);
	}

}
