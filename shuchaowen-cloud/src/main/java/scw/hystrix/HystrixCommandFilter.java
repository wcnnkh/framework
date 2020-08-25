package scw.hystrix;

import com.netflix.hystrix.HystrixCommand;

import scw.aop.Filter;
import scw.aop.FilterChain;
import scw.aop.MethodInvoker;
import scw.core.instance.annotation.Configuration;

@Configuration(order = Integer.MAX_VALUE)
public class HystrixCommandFilter implements Filter {
	private HystrixCommandFactory hystrixCommandFactory;

	public HystrixCommandFilter(HystrixCommandFactory hystrixCommandFactory) {
		this.hystrixCommandFactory = hystrixCommandFactory;
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
