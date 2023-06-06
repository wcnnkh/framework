package io.basc.framework.execution.aop.cglib;

import java.lang.reflect.Modifier;
import java.util.Arrays;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.execution.ExecutionInterceptor;
import io.basc.framework.execution.Executors;
import io.basc.framework.execution.aop.Proxy;
import io.basc.framework.util.ClassUtils;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.Factory;

public class CglibProxy implements Proxy {

	@Override
	public boolean canProxy(Class<?> clazz) {
		return !Modifier.isFinal(clazz.getModifiers());
	}

	@Override
	public boolean isProxy(Class<?> clazz) {
		return Enhancer.isEnhanced(clazz);
	}

	private Class<?>[] getInterfaces(Class<?> clazz, Class<?>[] interfaces) {
		if (interfaces == null || interfaces.length == 0) {
			return new Class<?>[0];
		}

		Class<?>[] interfacesToUse = new Class<?>[interfaces.length];
		int index = 0;
		for (Class<?> i : interfaces) {
			if (i.isAssignableFrom(clazz) || Factory.class.isAssignableFrom(i)) {
				continue;
			}

			interfacesToUse[index++] = i;
		}
		return Arrays.copyOf(interfacesToUse, index);
	}

	public Class<?> getProxyClass(Class<?> clazz, Class<?>[] interfaces) {
		Enhancer enhancer = CglibUtils.createEnhancer(clazz, getInterfaces(clazz, interfaces));
		enhancer.setCallbackType(ExecutionInterceptorToMethodInterceptor.class);
		return enhancer.createClass();
	}

	@Override
	public Executors getProxy(Class<?> clazz, Class<?>[] interfaces, ExecutionInterceptor executionInterceptor) {
		return new CglibProxyExecutors(TypeDescriptor.valueOf(clazz), getInterfaces(clazz, interfaces),
				executionInterceptor);
	}

	@Override
	public Class<?> getUserClass(Class<?> clazz) {
		Class<?> clz = clazz.getSuperclass();
		if (clz == null || clz == Object.class) {
			return clazz;
		}
		return clz;
	}

	/** The CGLIB class separator: "$$" */
	public static final String CGLIB_CLASS_SEPARATOR = "$$";

	@Override
	public boolean isProxy(String className, ClassLoader classLoader) throws ClassNotFoundException {
		if (className == null) {
			return false;
		}

		return className.contains(CGLIB_CLASS_SEPARATOR);
	}

	@Override
	public Class<?> getUserClass(String className, ClassLoader classLoader) throws ClassNotFoundException {
		return ClassUtils.forName(className.substring(0, className.indexOf(CGLIB_CLASS_SEPARATOR)), classLoader);
	}
}
