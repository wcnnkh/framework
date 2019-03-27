package scw.aop.cglib;

import net.sf.cglib.proxy.MethodProxy;
import scw.aop.Invoker;

public final class CglibInvoker implements Invoker {
	private final MethodProxy proxy;
	private final Object obj;
	private final Object[] args;

	public CglibInvoker(MethodProxy proxy, Object obj, Object[] args) {
		this.proxy = proxy;
		this.obj = obj;
		this.args = args;
	}

	public Object invoke() throws Throwable {
		return proxy.invokeSuper(obj, args);
	}
}
