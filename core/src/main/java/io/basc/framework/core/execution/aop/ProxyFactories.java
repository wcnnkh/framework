package io.basc.framework.core.execution.aop;

import io.basc.framework.util.ClassUtils;
import io.basc.framework.util.spi.ConfigurableServices;
import lombok.NonNull;

public class ProxyFactories extends ConfigurableServices<ProxyFactory> implements ProxyFactory {

	public ProxyFactories() {
		setServiceClass(ProxyFactory.class);
	}

	@Override
	public boolean canProxy(Class<?> sourceClass) {
		for (ProxyFactory proxy : this) {
			if (proxy.canProxy(sourceClass)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isProxy(Class<?> proxyClass) {
		for (ProxyFactory proxy : this) {
			if (proxy.isProxy(proxyClass)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Class<?> getUserClass(Class<?> proxyClass) {
		for (ProxyFactory proxy : this) {
			if (proxy.isProxy(proxyClass)) {
				return proxy.getUserClass(proxyClass);
			}
		}
		return proxyClass;
	}

	@Override
	public Proxy getProxy(@NonNull Class<?> sourceClass, Class<?>[] interfaces,
			ExecutionInterceptor executionInterceptor) {
		for (ProxyFactory proxy : this) {
			if (proxy.canProxy(sourceClass)) {
				return proxy.getProxy(sourceClass, interfaces, executionInterceptor);
			}
		}
		throw new UnsupportedOperationException(sourceClass.getName());
	}

	@Override
	public boolean isProxy(@NonNull String proxyClassName, ClassLoader classLoader) throws ClassNotFoundException {
		for (ProxyFactory proxy : this) {
			if (proxy.isProxy(proxyClassName, classLoader)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Class<?> getUserClass(@NonNull String proxyClassName, ClassLoader classLoader)
			throws ClassNotFoundException {
		for (ProxyFactory proxy : this) {
			if (proxy.isProxy(proxyClassName, classLoader)) {
				return proxy.getUserClass(proxyClassName, classLoader);
			}
		}
		return ClassUtils.forName(proxyClassName, classLoader);
	}

	@Override
	public Class<?> getProxyClass(@NonNull Class<?> sourceClass, Class<?>[] interfaces) {
		for (ProxyFactory proxy : this) {
			if (proxy.canProxy(sourceClass)) {
				return proxy.getProxyClass(sourceClass, interfaces);
			}
		}
		throw new UnsupportedOperationException(sourceClass.getName());
	}

}
