package scw.aop.cglib;

import java.lang.reflect.Method;

import net.sf.cglib.proxy.MethodProxy;
import scw.core.reflect.DefaultMethodInvoker;
import scw.lang.NestedExceptionUtils;

public class CglibProxyInvoker extends DefaultMethodInvoker {
	private static final long serialVersionUID = 1L;
	private final MethodProxy methodProxy;

	public CglibProxyInvoker(Object proxy, Class<?> targetClass, Method method, MethodProxy methodProxy) {
		super(proxy, targetClass, method);
		this.methodProxy = methodProxy;
	}

	public Object invoke(Object... args) throws Throwable {
		try {
			return methodProxy.invokeSuper(getInstance(), args);
		} catch (Throwable e) {
			throw NestedExceptionUtils.excludeInvalidNestedExcpetion(e);
		}
	}
}
