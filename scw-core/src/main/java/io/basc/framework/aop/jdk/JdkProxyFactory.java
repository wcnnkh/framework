package io.basc.framework.aop.jdk;

import io.basc.framework.aop.MethodInterceptor;
import io.basc.framework.aop.Proxy;
import io.basc.framework.aop.ProxyFactory;
import io.basc.framework.core.utils.ArrayUtils;
import io.basc.framework.core.utils.ClassUtils;

import java.util.Arrays;

public class JdkProxyFactory implements ProxyFactory {
	public boolean canProxy(Class<?> clazz) {
		return clazz.isInterface();
	}

	public boolean isProxy(Class<?> clazz) {
		return java.lang.reflect.Proxy.isProxyClass(clazz);
	}

	private final Class<?>[] mergeInterfaces(Class<?> clazz, Class<?>[] interfaces) {
		if (ArrayUtils.isEmpty(interfaces)) {
			if (clazz.isInterface()) {
				return new Class<?>[] { clazz };
			} else {
				return new Class<?>[0];
			}
		} else {
			Class<?>[] array = new Class<?>[1 + interfaces.length];
			int index = 0;
			array[index++] = clazz;
			for (int i = 0; i < interfaces.length; i++) {
				if (interfaces[i] == clazz) {
					continue;
				}

				array[index++] = interfaces[i];
			}

			if (index <= interfaces.length) {
				return Arrays.copyOfRange(array, 0, index);
			} else {
				return array;
			}
		}
	}

	public Class<?> getProxyClass(Class<?> clazz, Class<?>[] interfaces) {
		return java.lang.reflect.Proxy.getProxyClass(clazz.getClassLoader(), mergeInterfaces(clazz, interfaces));
	}

	public Proxy getProxy(Class<?> clazz, Class<?>[] interfaces, MethodInterceptor methodInterceptor) {
		return new JdkProxy(clazz, mergeInterfaces(clazz, interfaces), methodInterceptor);
	}

	public Class<?> getUserClass(Class<?> clazz) {
		return clazz.getInterfaces()[0];
	}
	
	public static final String PROXY_NAME_PREFIX = "java.lang.reflect.Proxy";

	public boolean isProxy(String className, ClassLoader classLoader) throws ClassNotFoundException{
		return className.startsWith(PROXY_NAME_PREFIX);
	}

	public Class<?> getUserClass(String className, ClassLoader classLoader)
			throws ClassNotFoundException {
		return getUserClass(ClassUtils.forName(className, classLoader));
	}
}
