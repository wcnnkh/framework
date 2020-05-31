package scw.hystrix;

import scw.aop.Filter;
import scw.aop.FilterChain;
import scw.aop.ProxyInvoker;
import scw.core.instance.annotation.Configuration;

import com.netflix.hystrix.HystrixCommand;

@Configuration(order = Integer.MAX_VALUE)
public class HystrixCommandFilter implements Filter {
	private HystrixCommandFactory hystrixCommandFactory;

	public HystrixCommandFilter(HystrixCommandFactory hystrixCommandFactory) {
		this.hystrixCommandFactory = hystrixCommandFactory;
	}

	@Override
	public Object doFilter(ProxyInvoker invoker, Object[] args, FilterChain filterChain) throws Throwable {
		HystrixCommand<?> command = hystrixCommandFactory.getHystrixCommandFactory(invoker, args, filterChain);
		if (command == null) {
			return filterChain.doFilter(invoker, args);
		}
		return command.execute();
	}
}
