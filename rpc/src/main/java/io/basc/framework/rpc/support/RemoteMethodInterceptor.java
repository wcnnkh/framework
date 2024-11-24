package io.basc.framework.rpc.support;

import io.basc.framework.aop.MethodInterceptor;
import io.basc.framework.rpc.CallableFactory;
import io.basc.framework.util.logging.Logger;
import io.basc.framework.util.reflect.MethodInvoker;
import io.basc.framework.util.logging.LogManager;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.concurrent.Callable;

public class RemoteMethodInterceptor implements MethodInterceptor{
	private static Logger logger = LogManager.getLogger(RemoteMethodInterceptor.class);
	private final CallableFactory callableFactory;

	public RemoteMethodInterceptor(CallableFactory callableFactory){
		this.callableFactory = callableFactory;
	}
	
	public Object intercept(MethodInvoker invoker, Object[] args)
			throws Throwable {
		Method method = invoker.getMethod();
		if (Modifier.isStatic(method.getModifiers()) || !Modifier.isAbstract(method.getModifiers())) {
			logger.trace("ignore method " + method);
			return invoker.invoke(args);
		}
		
		Callable<Object> callable = callableFactory.getCallable(invoker.getSourceClass(), method, args);
		if(callable == null){
			logger.debug("ignore");
			return invoker.invoke(args);
		}
		
		return callable.call();
	}

}
