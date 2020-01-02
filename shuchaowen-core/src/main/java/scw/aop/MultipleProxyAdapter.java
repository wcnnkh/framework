package scw.aop;

import java.util.Collection;
import java.util.LinkedList;

import scw.lang.NotSupportException;

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

	public Proxy proxy(Class<?> clazz, Class<?>[] interfaces, Collection<? extends Filter> filters) {
		for (ProxyAdapter proxyAdapter : this) {
			if (proxyAdapter.isSupport(clazz)) {
				return proxyAdapter.proxy(clazz, interfaces, filters);
			}
		}
		throw new NotSupportException(clazz.getName());
	}

	public Class<?> getClass(Class<?> clazz, Class<?>[] interfaces) {
		for (ProxyAdapter proxyAdapter : this) {
			if (proxyAdapter.isSupport(clazz)) {
				return proxyAdapter.getClass(clazz, interfaces);
			}
		}
		throw new NotSupportException(clazz.getName());
	}

	public boolean isProxy(Class<?> clazz) {
		for (ProxyAdapter proxyAdapter : this) {
			if (proxyAdapter.isProxy(clazz)) {
				return true;
			}
		}
		return false;
	}

	public Proxy proxy(Class<?> clazz, Class<?>[] interfaces, Collection<? extends Filter> filters,
			FilterChain filterChain) {
		for (ProxyAdapter proxyAdapter : this) {
			if (proxyAdapter.isSupport(clazz)) {
				return proxyAdapter.proxy(clazz, interfaces, filters, filterChain);
			}
		}
		throw new NotSupportException(clazz.getName());
	}

	public Class<?> getUserClass(Class<?> proxyClass) {
		for (ProxyAdapter proxyAdapter : this) {
			if (proxyAdapter.isProxy(proxyClass)) {
				return proxyAdapter.getUserClass(proxyClass);
			}
		}
		return proxyClass;
	}
}
