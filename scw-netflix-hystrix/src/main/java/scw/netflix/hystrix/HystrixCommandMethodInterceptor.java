package scw.netflix.hystrix;

import scw.aop.MethodInterceptor;
import scw.aop.MethodInterceptorAccept;
import scw.context.annotation.Provider;
import scw.core.reflect.MethodInvoker;
import scw.netflix.hystrix.annotation.Hystrix;

import com.netflix.hystrix.HystrixCommand;

@Provider(order = Integer.MAX_VALUE)
public class HystrixCommandMethodInterceptor implements MethodInterceptor, MethodInterceptorAccept {
	private HystrixCommandFactory hystrixCommandFactory;

	public HystrixCommandMethodInterceptor(HystrixCommandFactory hystrixCommandFactory) {
		this.hystrixCommandFactory = hystrixCommandFactory;
	}
	
	public boolean isAccept(MethodInvoker invoker, Object[] args) {
		return invoker.getSourceClass().getAnnotation(Hystrix.class) != null;
	}

	public Object intercept(MethodInvoker invoker, Object[] args) throws Throwable {
		HystrixCommand<?> command = hystrixCommandFactory.getHystrixCommandFactory(invoker, args);
		if (command == null) {
			return invoker.invoke(args);
		}
		return command.execute();
	}
}
