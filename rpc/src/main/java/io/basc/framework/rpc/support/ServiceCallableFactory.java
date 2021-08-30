package io.basc.framework.rpc.support;

import io.basc.framework.factory.NoArgsInstanceFactory;
import io.basc.framework.rpc.CallableFactory;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

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
