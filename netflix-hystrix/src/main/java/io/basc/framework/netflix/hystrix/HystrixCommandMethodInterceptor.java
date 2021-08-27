package io.basc.framework.netflix.hystrix;

import io.basc.framework.aop.MethodInterceptor;
import io.basc.framework.aop.MethodInterceptorAccept;
import io.basc.framework.context.annotation.Provider;
import io.basc.framework.core.reflect.MethodInvoker;
import io.basc.framework.netflix.hystrix.annotation.Hystrix;

import com.netflix.hystrix.HystrixCommand;

@Provider
public class HystrixCommandMethodInterceptor implements MethodInterceptor, MethodInterceptorAccept {
	private HystrixCommandFactory hystrixCommandFactory;

	public HystrixCommandMethodInterceptor(HystrixCommandFactory hystrixCommandFactory) {
		this.hystrixCommandFactory = hystrixCommandFactory;
	}
	
	public boolean isAccept(MethodInvoker invoker, Object[] args) {
		return invoker.getDeclaringClass().getAnnotation(Hystrix.class) != null;
	}

	public Object intercept(MethodInvoker invoker, Object[] args) throws Throwable {
		HystrixCommand<?> command = hystrixCommandFactory.getHystrixCommandFactory(invoker, args);
		if (command == null) {
			return invoker.invoke(args);
		}
		return command.execute();
	}
}
