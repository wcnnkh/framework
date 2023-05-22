package io.basc.framework.execution.aop;

import io.basc.framework.execution.ExecutionInterceptor;
import io.basc.framework.execution.Executors;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.ClassUtils;
import io.basc.framework.util.ServiceRegistry;

public class Proxies extends ServiceRegistry<Proxy> implements Proxy {

	@Override
	public boolean canProxy(Class<?> sourceClass) {
		for (Proxy proxy : getServices()) {
			if (proxy.canProxy(sourceClass)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isProxy(Class<?> proxyClass) {
		for (Proxy proxy : getServices()) {
			if (proxy.isProxy(proxyClass)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Class<?> getUserClass(Class<?> proxyClass) {
		for (Proxy proxy : getServices()) {
			if (proxy.isProxy(proxyClass)) {
				return proxy.getUserClass(proxyClass);
			}
		}
		return proxyClass;
	}

	@Override
	public Executors getProxy(Class<?> sourceClass, Class<?>[] interfaces,
			@Nullable ExecutionInterceptor executionInterceptor) {
		for (Proxy proxy : getServices()) {
			if (proxy.canProxy(sourceClass)) {
				return proxy.getProxy(sourceClass, interfaces, executionInterceptor);
			}
		}
		throw new UnsupportedOperationException(sourceClass.getName());
	}

	@Override
	public boolean isProxy(String proxyClassName, ClassLoader classLoader) throws ClassNotFoundException {
		for (Proxy proxy : getServices()) {
			if (proxy.isProxy(proxyClassName, classLoader)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Class<?> getUserClass(String proxyClassName, ClassLoader classLoader) throws ClassNotFoundException {
		for (Proxy proxy : getServices()) {
			if (proxy.isProxy(proxyClassName, classLoader)) {
				return proxy.getUserClass(proxyClassName, classLoader);
			}
		}
		return ClassUtils.forName(proxyClassName, classLoader);
	}

}
