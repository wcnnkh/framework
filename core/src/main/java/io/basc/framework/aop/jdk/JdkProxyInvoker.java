package io.basc.framework.aop.jdk;

import io.basc.framework.aop.support.ProxyUtils;
import io.basc.framework.reflect.DefaultMethodInvoker;

import java.lang.reflect.Method;

public class JdkProxyInvoker extends DefaultMethodInvoker {
	private static final long serialVersionUID = 1L;

	public JdkProxyInvoker(Object proxy, Class<?> targetClass, Method method) {
		super(proxy, targetClass, method);
	}

	public Object invoke(Object... args) throws Throwable {
		// 如果filter中没有拦截这些方法，那么使用默认的调用
		if (ProxyUtils.isIgnoreMethod(getMethod())) {
			return ProxyUtils.invokeIgnoreMethod(this, args);
		}

		throw new UnsupportedOperationException(toString());
	}
}
