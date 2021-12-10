package io.basc.framework.aop.support;

import io.basc.framework.aop.MethodInterceptor;
import io.basc.framework.aop.Proxy;
import io.basc.framework.aop.ProxyFactory;
import io.basc.framework.aop.cglib.CglibProxyFactory;
import io.basc.framework.lang.NotSupportedException;
import io.basc.framework.util.ClassUtils;

public abstract class AbstractConfigurableProxyFactory extends CglibProxyFactory
		implements ProxyFactory, Iterable<ProxyFactory> {

	@Override
	public boolean canProxy(Class<?> clazz) {
		for (ProxyFactory proxyFactory : this) {
			if (proxyFactory.canProxy(clazz)) {
				return true;
			}
		}
		return super.canProxy(clazz);
	}

	@Override
	public boolean isProxy(Class<?> clazz) {
		for (ProxyFactory proxyFactory : this) {
			if (proxyFactory.isProxy(clazz)) {
				return true;
			}
		}
		return super.isProxy(clazz);
	}

	@Override
	public Proxy getProxy(Class<?> clazz, Class<?>[] interfaces, MethodInterceptor methodInterceptor) {
		for (ProxyFactory proxyFactory : this) {
			if (proxyFactory.canProxy(clazz)) {
				return proxyFactory.getProxy(clazz, interfaces, methodInterceptor);
			}
		}

		if (super.canProxy(clazz)) {
			return super.getProxy(clazz, interfaces, methodInterceptor);
		}

		throw new NotSupportedException(clazz.getName());
	}

	@Override
	public Class<?> getUserClass(Class<?> proxyClass) {
		for (ProxyFactory proxyFactory : this) {
			if (proxyFactory.isProxy(proxyClass)) {
				return proxyFactory.getUserClass(proxyClass);
			}
		}

		if (super.isProxy(proxyClass)) {
			return super.getUserClass(proxyClass);
		}

		return proxyClass;
	}

	@Override
	public boolean isProxy(String className, ClassLoader classLoader) throws ClassNotFoundException {
		for (ProxyFactory proxyFactory : this) {
			if (proxyFactory.isProxy(className, classLoader)) {
				return true;
			}
		}
		return super.isProxy(className, classLoader);
	}

	@Override
	public Class<?> getUserClass(String className, ClassLoader classLoader) throws ClassNotFoundException {
		for (ProxyFactory proxyFactory : this) {
			if (proxyFactory.isProxy(className, classLoader)) {
				return proxyFactory.getUserClass(className, classLoader);
			}
		}

		if (super.isProxy(className, classLoader)) {
			return super.getUserClass(className, classLoader);
		}

		return ClassUtils.forName(className, classLoader);
	}
}
