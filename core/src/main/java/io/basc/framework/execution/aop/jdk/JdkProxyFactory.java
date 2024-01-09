package io.basc.framework.execution.aop.jdk;

import java.util.Arrays;

import io.basc.framework.execution.aop.ExecutionInterceptor;
import io.basc.framework.execution.aop.Proxy;
import io.basc.framework.execution.aop.ProxyFactories;
import io.basc.framework.util.ArrayUtils;
import io.basc.framework.util.ClassUtils;

public class JdkProxyFactory extends ProxyFactories {

	@Override
	public boolean canProxy(Class<?> clazz) {
		return super.canProxy(clazz) || clazz.isInterface();
	}

	@Override
	public boolean isProxy(Class<?> clazz) {
		return super.isProxy(clazz) || java.lang.reflect.Proxy.isProxyClass(clazz);
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

	@Override
	public Proxy getProxy(Class<?> clazz, Class<?>[] interfaces, ExecutionInterceptor executionInterceptor) {
		if (super.canProxy(clazz)) {
			return super.getProxy(clazz, interfaces, executionInterceptor);
		}
		return new JdkProxy(clazz, mergeInterfaces(clazz, interfaces), executionInterceptor);
	}

	@Override
	public Class<?> getProxyClass(Class<?> sourceClass, Class<?>[] interfaces) {
		if (super.canProxy(sourceClass)) {
			return super.getProxyClass(sourceClass, interfaces);
		}

		return java.lang.reflect.Proxy.getProxyClass(sourceClass.getClassLoader(),
				mergeInterfaces(sourceClass, interfaces));
	}

	@Override
	public Class<?> getUserClass(Class<?> clazz) {
		if (super.isProxy(clazz)) {
			return super.getUserClass(clazz);
		}

		return clazz.getInterfaces()[0];
	}

	public static final String PROXY_NAME_PREFIX = "java.lang.reflect.Proxy";

	@Override
	public boolean isProxy(String className, ClassLoader classLoader) throws ClassNotFoundException {
		return super.isProxy(className, classLoader) || className.startsWith(PROXY_NAME_PREFIX);
	}

	@Override
	public Class<?> getUserClass(String className, ClassLoader classLoader) throws ClassNotFoundException {
		if (super.isProxy(className, classLoader)) {
			return super.getUserClass(className, classLoader);
		}

		return getUserClass(ClassUtils.forName(className, classLoader));
	}
}
