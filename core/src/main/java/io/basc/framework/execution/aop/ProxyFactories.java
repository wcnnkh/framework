package io.basc.framework.execution.aop;

import io.basc.framework.beans.factory.config.ConfigurableServices;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.ClassUtils;

public class ProxyFactories extends ConfigurableServices<ProxyFactory> implements ProxyFactory {

	@Override
	public boolean canProxy(Class<?> sourceClass) {
		for (ProxyFactory proxy : getServices()) {
			if (proxy.canProxy(sourceClass)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isProxy(Class<?> proxyClass) {
		for (ProxyFactory proxy : getServices()) {
			if (proxy.isProxy(proxyClass)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Class<?> getUserClass(Class<?> proxyClass) {
		for (ProxyFactory proxy : getServices()) {
			if (proxy.isProxy(proxyClass)) {
				return proxy.getUserClass(proxyClass);
			}
		}
		return proxyClass;
	}

	@Override
	public Proxy getProxy(Class<?> sourceClass, Class<?>[] interfaces,
			@Nullable ExecutionInterceptor executionInterceptor) {
		for (ProxyFactory proxy : getServices()) {
			if (proxy.canProxy(sourceClass)) {
				return proxy.getProxy(sourceClass, interfaces, executionInterceptor);
			}
		}
		throw new UnsupportedOperationException(sourceClass.getName());
	}

	@Override
	public boolean isProxy(String proxyClassName, ClassLoader classLoader) throws ClassNotFoundException {
		for (ProxyFactory proxy : getServices()) {
			if (proxy.isProxy(proxyClassName, classLoader)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Class<?> getUserClass(String proxyClassName, ClassLoader classLoader) throws ClassNotFoundException {
		for (ProxyFactory proxy : getServices()) {
			if (proxy.isProxy(proxyClassName, classLoader)) {
				return proxy.getUserClass(proxyClassName, classLoader);
			}
		}
		return ClassUtils.forName(proxyClassName, classLoader);
	}

	@Override
	public Class<?> getProxyClass(Class<?> sourceClass, Class<?>[] interfaces) {
		for (ProxyFactory proxy : getServices()) {
			if (proxy.canProxy(sourceClass)) {
				return proxy.getProxyClass(sourceClass, interfaces);
			}
		}
		throw new UnsupportedOperationException(sourceClass.getName());
	}

}
