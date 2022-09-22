package io.basc.framework.rpc.support;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

import io.basc.framework.factory.InstanceFactory;
import io.basc.framework.rpc.CallableFactory;

public class ServiceCallableFactory implements CallableFactory {
	private final InstanceFactory instanceFactory;

	public ServiceCallableFactory(InstanceFactory instanceFactory) {
		this.instanceFactory = instanceFactory;
	}

	public Callable<Object> getCallable(Class<?> clazz, Method method,
			Object[] args) {
		Object instance = instanceFactory.getInstance(clazz);
		return new ServiceCallable(instance, method, args);
	}

}
