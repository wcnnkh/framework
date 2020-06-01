package scw.aop;

import java.util.LinkedList;

import scw.core.utils.ClassUtils;
import scw.lang.NotSupportedException;

public class MultipleProxyFactory extends LinkedList<ProxyFactory> implements ProxyFactory {
	private static final long serialVersionUID = 1L;

	public boolean isSupport(Class<?> clazz) {
		for (ProxyFactory proxyFactory : this) {
			if (proxyFactory.isSupport(clazz)) {
				return true;
			}
		}
		return false;
	}

	public Class<?> getProxyClass(Class<?> clazz, Class<?>[] interfaces) {
		for (ProxyFactory proxyFactory : this) {
			if (proxyFactory.isSupport(clazz)) {
				return proxyFactory.getProxyClass(clazz, interfaces);
			}
		}
		throw new NotSupportedException(clazz.getName());
	}

	public boolean isProxy(Class<?> clazz) {
		for (ProxyFactory proxyFactory : this) {
			if (proxyFactory.isProxy(clazz)) {
				return true;
			}
		}
		return false;
	}

	public Proxy getProxy(Class<?> clazz, Class<?>[] interfaces, Filter ...filters) {
		for (ProxyFactory proxyFactory : this) {
			if (proxyFactory.isSupport(clazz)) {
				return proxyFactory.getProxy(clazz, interfaces, filters);
			}
		}
		throw new NotSupportedException(clazz.getName());
	}

	public Class<?> getUserClass(Class<?> proxyClass) {
		for (ProxyFactory proxyFactory : this) {
			if (proxyFactory.isProxy(proxyClass)) {
				return proxyFactory.getUserClass(proxyClass);
			}
		}
		return proxyClass;
	}

	public boolean isProxy(String className, ClassLoader classLoader) {
		ClassLoader classLoaderToUse = classLoader == null ? ClassUtils.getDefaultClassLoader() : classLoader;
		for (ProxyFactory proxyFactory : this) {
			if (proxyFactory.isProxy(className, classLoaderToUse)) {
				return true;
			}
		}
		return false;
	}

	public Class<?> getUserClass(String className, boolean initialize, ClassLoader classLoader)
			throws ClassNotFoundException {
		ClassLoader classLoaderToUse = classLoader == null ? ClassUtils.getDefaultClassLoader() : classLoader;
		for (ProxyFactory proxyFactory : this) {
			if (proxyFactory.isProxy(className, classLoaderToUse)) {
				return proxyFactory.getUserClass(className, initialize, classLoaderToUse);
			}
		}
		return ClassUtils.forName(className, initialize, classLoaderToUse);
	}
}
