package scw.hystrix;

import com.netflix.hystrix.HystrixCommand;

import scw.aop.Filter;
import scw.aop.FilterChain;
import scw.aop.Invoker;
import scw.aop.ProxyContext;
import scw.core.instance.annotation.Configuration;

@Configuration(order = Integer.MAX_VALUE)
public class HystrixCommandFilter implements Filter {
	private HystrixCommandFactory hystrixCommandFactory;

	public HystrixCommandFilter(HystrixCommandFactory hystrixCommandFactory) {
		this.hystrixCommandFactory = hystrixCommandFactory;
	}

	@Override
	public Object doFilter(Invoker invoker, ProxyContext context, FilterChain filterChain) throws Throwable {
		HystrixCommand<?> command = hystrixCommandFactory.getHystrixCommandFactory(context, invoker, filterChain);
		if (command == null) {
			return filterChain.doFilter(invoker, context);
		}
		return command.execute();
	}
}
