package scw.hystrix;

import com.netflix.hystrix.HystrixCommand;

import scw.aop.FilterChain;
import scw.aop.Invoker;
import scw.aop.ProxyContext;
import scw.lang.NestedRuntimeException;

public class HystrixFilterCommand extends HystrixCommand<Object> {
	private Object fallback;
	private ProxyContext proxyContext;
	private Invoker invoker;
	private FilterChain filterChain;

	protected HystrixFilterCommand(Setter setter, Object fallback, ProxyContext proxyContext, Invoker invoker,
			FilterChain filterChain) {
		super(setter);
		this.fallback = fallback;
		this.proxyContext = proxyContext;
		this.invoker = invoker;
		this.filterChain = filterChain;
	}

	@Override
	protected Object run() throws Exception {
		try {
			return filterChain.doFilter(invoker, proxyContext);
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
			return proxyContext.getMethod().invoke(fallback, proxyContext.getArgs());
		} catch (Exception e) {
			throw new NestedRuntimeException(e);
		}
	}
}
