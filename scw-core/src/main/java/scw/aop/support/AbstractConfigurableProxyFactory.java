package scw.aop.support;

import scw.aop.MethodInterceptor;
import scw.aop.Proxy;
import scw.aop.ProxyFactory;
import scw.aop.cglib.CglibProxyFactory;
import scw.core.utils.ClassUtils;
import scw.lang.NotSupportedException;

public abstract class AbstractConfigurableProxyFactory extends CglibProxyFactory implements ProxyFactory,
		Iterable<ProxyFactory> {

	public boolean canProxy(Class<?> clazz) {
		for (ProxyFactory proxyFactory : this) {
			if (proxyFactory.canProxy(clazz)) {
				return true;
			}
		}
		return super.canProxy(clazz);
	}

	public Class<?> getProxyClass(Class<?> clazz, Class<?>[] interfaces) {
		for (ProxyFactory proxyFactory : this) {
			if (proxyFactory.canProxy(clazz)) {
				return proxyFactory.getProxyClass(clazz, interfaces);
			}
		}
		
		if(super.canProxy(clazz)){
			return super.getProxyClass(clazz, interfaces);
		}
		throw new NotSupportedException(clazz.getName());
	}

	public boolean isProxy(Class<?> clazz) {
		for (ProxyFactory proxyFactory : this) {
			if (proxyFactory.isProxy(clazz)) {
				return true;
			}
		}
		return super.isProxy(clazz);
	}

	public Proxy getProxy(Class<?> clazz, Class<?>[] interfaces, MethodInterceptor methodInterceptor) {
		for (ProxyFactory proxyFactory : this) {
			if (proxyFactory.canProxy(clazz)) {
				return proxyFactory.getProxy(clazz, interfaces, methodInterceptor);
			}
		}
		
		if(super.canProxy(clazz)){
			return super.getProxy(clazz, interfaces, methodInterceptor);
		}
		
		throw new NotSupportedException(clazz.getName());
	}

	public Class<?> getUserClass(Class<?> proxyClass) {
		for (ProxyFactory proxyFactory : this) {
			if (proxyFactory.isProxy(proxyClass)) {
				return proxyFactory.getUserClass(proxyClass);
			}
		}
		
		if(super.isProxy(proxyClass)){
			return super.getUserClass(proxyClass);
		}
		
		return proxyClass;
	}
	
	@Override
	public boolean isProxy(String className, ClassLoader classLoader) throws ClassNotFoundException {
		for (ProxyFactory proxyFactory : this) {
			if(proxyFactory.isProxy(className, classLoader)){
				return true;
			}
		}
		return super.isProxy(className, classLoader);
	}
	
	@Override
	public Class<?> getUserClass(String className, ClassLoader classLoader)
			throws ClassNotFoundException {
		for (ProxyFactory proxyFactory : this) {
			if (proxyFactory.isProxy(className, classLoader)) {
				return proxyFactory.getUserClass(className, classLoader);
			}
		}
		
		if(super.isProxy(className, classLoader)){
			return super.getUserClass(className, classLoader);
		}
		
		return ClassUtils.forName(className, classLoader);
	}
}
