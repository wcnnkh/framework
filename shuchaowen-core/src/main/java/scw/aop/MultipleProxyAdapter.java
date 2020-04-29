package scw.aop;

import java.util.LinkedList;

import scw.core.utils.ClassUtils;
import scw.lang.NotSupportedException;

public class MultipleProxyAdapter extends LinkedList<ProxyAdapter> implements ProxyAdapter {
	private static final long serialVersionUID = 1L;

	public boolean isSupport(Class<?> clazz) {
		for (ProxyAdapter proxyAdapter : this) {
			if (proxyAdapter.isSupport(clazz)) {
				return true;
			}
		}
		return false;
	}

	public Class<?> getClass(Class<?> clazz, Class<?>[] interfaces) {
		for (ProxyAdapter proxyAdapter : this) {
			if (proxyAdapter.isSupport(clazz)) {
				return proxyAdapter.getClass(clazz, interfaces);
			}
		}
		throw new NotSupportedException(clazz.getName());
	}

	public boolean isProxy(Class<?> clazz) {
		for (ProxyAdapter proxyAdapter : this) {
			if (proxyAdapter.isProxy(clazz)) {
				return true;
			}
		}
		return false;
	}

	public Proxy proxy(Class<?> clazz, Class<?>[] interfaces, FilterChain filterChain) {
		for (ProxyAdapter proxyAdapter : this) {
			if (proxyAdapter.isSupport(clazz)) {
				return proxyAdapter.proxy(clazz, interfaces, filterChain);
			}
		}
		throw new NotSupportedException(clazz.getName());
	}

	public Class<?> getUserClass(Class<?> proxyClass) {
		for (ProxyAdapter proxyAdapter : this) {
			if (proxyAdapter.isProxy(proxyClass)) {
				return proxyAdapter.getUserClass(proxyClass);
			}
		}
		return proxyClass;
	}

	public boolean isProxy(String className, ClassLoader classLoader) {
		ClassLoader classLoaderToUse = classLoader == null ? ClassUtils.getDefaultClassLoader() : classLoader;
		for (ProxyAdapter proxyAdapter : this) {
			if (proxyAdapter.isProxy(className, classLoaderToUse)) {
				return true;
			}
		}
		return false;
	}

	public Class<?> getUserClass(String className, boolean initialize, ClassLoader classLoader)
			throws ClassNotFoundException {
		ClassLoader classLoaderToUse = classLoader == null ? ClassUtils.getDefaultClassLoader() : classLoader;
		for (ProxyAdapter proxyAdapter : this) {
			if (proxyAdapter.isProxy(className, classLoaderToUse)) {
				return proxyAdapter.getUserClass(className, initialize, classLoaderToUse);
			}
		}
		return ClassUtils.forName(className, initialize, classLoaderToUse);
	}
}
