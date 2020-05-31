package scw.hystrix;

import scw.aop.ProxyInvoker;
import scw.lang.NestedRuntimeException;

import com.netflix.hystrix.HystrixCommand;

public class HystrixFilterCommand extends HystrixCommand<Object> {
	private Object fallback;
	private ProxyInvoker invoker;
	private Object[] args;

	protected HystrixFilterCommand(Setter setter, Object fallback, ProxyInvoker invoker, Object[] args) {
		super(setter);
		this.fallback = fallback;
		this.invoker = invoker;
		this.args = args;
	}

	@Override
	protected Object run() throws Exception {
		try {
			return invoker.invoke(args);
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
