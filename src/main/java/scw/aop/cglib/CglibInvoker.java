package scw.aop.cglib;

import net.sf.cglib.proxy.MethodProxy;
import scw.aop.Invoker;

public final class CglibInvoker implements Invoker {
	private final MethodProxy proxy;
	private final Object obj;

	public CglibInvoker(MethodProxy proxy, Object obj) {
		this.proxy = proxy;
		this.obj = obj;
	}

	public Object invoke(Object... args) throws Throwable {
		return proxy.invokeSuper(obj, args);
	}
}
