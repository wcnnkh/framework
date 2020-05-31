package scw.hystrix;

import scw.aop.Filter;
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
	public Object doFilter(ProxyInvoker invoker, Object[] args) throws Throwable {
		HystrixCommand<?> command = hystrixCommandFactory.getHystrixCommandFactory(invoker, args);
		if (command == null) {
			return invoker.invoke(args);
		}
		return command.execute();
	}
}
