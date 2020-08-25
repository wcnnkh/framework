package scw.hystrix;

import com.netflix.hystrix.HystrixCommand;

import scw.aop.FilterChain;
import scw.aop.MethodInvoker;
import scw.lang.NestedRuntimeException;

public class HystrixFilterCommand extends HystrixCommand<Object> {
	private Object fallback;
	private MethodInvoker invoker;
	private Object[] args;
	private FilterChain filterChain;

	protected HystrixFilterCommand(Setter setter, Object fallback, MethodInvoker invoker, Object[] args, FilterChain filterChain) {
		super(setter);
		this.fallback = fallback;
		this.invoker = invoker;
		this.args = args;
		this.filterChain = filterChain;
	}

	@Override
	protected Object run() throws Exception {
		try {
			return filterChain.doFilter(invoker, args);
		} catch (Throwable e) {
			throw new NestedRuntimeException(e);
		}
	}

	@Override
	protected Object getFallback() {
		if (fallback == null) {
			return super.getFallback();
		}

		try {
			return invoker.getMethod().invoke(fallback, args);
		} catch (Exception e) {
			throw new NestedRuntimeException(e);
		}
	}
}
