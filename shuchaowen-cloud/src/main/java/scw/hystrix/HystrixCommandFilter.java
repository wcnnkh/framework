package scw.hystrix;

import com.netflix.hystrix.HystrixCommand;

import scw.aop.Filter;
import scw.aop.FilterAccept;
import scw.aop.FilterChain;
import scw.aop.MethodInvoker;
import scw.core.instance.annotation.Configuration;
import scw.hystrix.annotation.Hystrix;

@Configuration(order = Integer.MAX_VALUE)
public class HystrixCommandFilter implements Filter, FilterAccept {
	private HystrixCommandFactory hystrixCommandFactory;

	public HystrixCommandFilter(HystrixCommandFactory hystrixCommandFactory) {
		this.hystrixCommandFactory = hystrixCommandFactory;
	}
	
	@Override
	public boolean isAccept(MethodInvoker invoker, Object[] args) {
		return invoker.getSourceClass().getAnnotation(Hystrix.class) != null;
	}

	@Override
	public Object doFilter(MethodInvoker invoker, Object[] args, FilterChain filterChain) throws Throwable {
		HystrixCommand<?> command = hystrixCommandFactory.getHystrixCommandFactory(invoker, args, filterChain);
		if (command == null) {
			return invoker.invoke(args);
		}
		return command.execute();
	}
}
