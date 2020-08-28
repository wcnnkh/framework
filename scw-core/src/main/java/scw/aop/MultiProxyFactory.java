package scw.aop;

import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;

import scw.core.utils.ClassUtils;
import scw.lang.NotSupportedException;
import scw.util.Enumerable;

public class MultiProxyFactory implements ProxyFactory, Enumerable<ProxyFactory> {
	private final Collection<ProxyFactory> proxyFactories;

	public MultiProxyFactory(Collection<ProxyFactory> proxyFactories) {
		this.proxyFactories = proxyFactories;
	}

	public Enumeration<ProxyFactory> enumeration() {
		return Collections.enumeration(proxyFactories);
	}

	public boolean isSupport(Class<?> clazz) {
		for (ProxyFactory proxyFactory : proxyFactories) {
			if (proxyFactory.isSupport(clazz)) {
				return true;
			}
		}
		return false;
	}

	public Class<?> getProxyClass(Class<?> clazz, Class<?>[] interfaces) {
		for (ProxyFactory proxyFactory : proxyFactories) {
			if (proxyFactory.isSupport(clazz)) {
				return proxyFactory.getProxyClass(clazz, interfaces);
			}
		}
		throw new NotSupportedException(clazz.getName());
	}

	public boolean isProxy(Class<?> clazz) {
		for (ProxyFactory proxyFactory : proxyFactories) {
			if (proxyFactory.isProxy(clazz)) {
				return true;
			}
		}
		return false;
	}
	
	public Proxy getProxy(Class<?> clazz, Class<?>[] interfaces, Iterable<? extends MethodInterceptor> filters) {
		for (ProxyFactory proxyFactory : proxyFactories) {
			if (proxyFactory.isSupport(clazz)) {
				return proxyFactory.getProxy(clazz, interfaces, filters);
			}
		}
		throw new NotSupportedException(clazz.getName());
	}

	public Class<?> getUserClass(Class<?> proxyClass) {
		for (ProxyFactory proxyFactory : proxyFactories) {
			if (proxyFactory.isProxy(proxyClass)) {
				return proxyFactory.getUserClass(proxyClass);
			}
		}
		return proxyClass;
	}

	public boolean isProxy(String className, ClassLoader classLoader) {
		ClassLoader classLoaderToUse = classLoader == null ? ClassUtils.getDefaultClassLoader() : classLoader;
		for (ProxyFactory proxyFactory : proxyFactories) {
			if (proxyFactory.isProxy(className, classLoaderToUse)) {
				return true;
			}
		}
		return false;
	}

	public Class<?> getUserClass(String className, boolean initialize, ClassLoader classLoader)
			throws ClassNotFoundException {
		ClassLoader classLoaderToUse = classLoader == null ? ClassUtils.getDefaultClassLoader() : classLoader;
		for (ProxyFactory proxyFactory : proxyFactories) {
			if (proxyFactory.isProxy(className, classLoaderToUse)) {
				return proxyFactory.getUserClass(className, initialize, classLoaderToUse);
			}
		}
		return ClassUtils.forName(className, initialize, classLoaderToUse);
	}
}
