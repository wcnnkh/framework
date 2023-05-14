package io.basc.framework.exec.aop;

import io.basc.framework.exec.Executable;
import io.basc.framework.exec.ExecutionInterceptor;
import io.basc.framework.util.ClassUtils;
import io.basc.framework.util.Services;

public class Proxies extends Services<Proxy> implements Proxy {

	@Override
	public boolean canProxy(Class<?> sourceClass) {
		for (Proxy proxy : this) {
			if (proxy.canProxy(sourceClass)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isProxy(Class<?> proxyClass) {
		for (Proxy proxy : this) {
			if (proxy.isProxy(proxyClass)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Class<?> getUserClass(Class<?> proxyClass) {
		for (Proxy proxy : this) {
			if (proxy.isProxy(proxyClass)) {
				return proxy.getUserClass(proxyClass);
			}
		}
		return proxyClass;
	}

	@Override
	public Executable getProxy(Class<?> sourceClass, Class<?>[] interfaces, ExecutionInterceptor executionInterceptor) {
		for (Proxy proxy : this) {
			if (proxy.canProxy(sourceClass)) {
				return proxy.getProxy(sourceClass, interfaces, executionInterceptor);
			}
		}
		throw new UnsupportedOperationException(sourceClass.getName());
	}

	@Override
	public boolean isProxy(String proxyClassName, ClassLoader classLoader) throws ClassNotFoundException {
		for (Proxy proxy : this) {
			if (proxy.isProxy(proxyClassName, classLoader)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Class<?> getUserClass(String proxyClassName, ClassLoader classLoader) throws ClassNotFoundException {
		for (Proxy proxy : this) {
			if (proxy.isProxy(proxyClassName, classLoader)) {
				return proxy.getUserClass(proxyClassName, classLoader);
			}
		}
		return ClassUtils.forName(proxyClassName, classLoader);
	}

}
