package io.basc.framework.core.execution.aop.cglib;

import java.lang.reflect.Modifier;
import java.util.Arrays;

import io.basc.framework.core.execution.aop.ExecutionInterceptor;
import io.basc.framework.core.execution.aop.Proxy;
import io.basc.framework.core.execution.aop.jdk.JdkProxyFactory;
import io.basc.framework.util.ClassUtils;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.Factory;

public class CglibProxyFactory extends JdkProxyFactory {

	/** The CGLIB class separator: "$$" */
	public static final String CGLIB_CLASS_SEPARATOR = "$$";

	@Override
	public boolean canProxy(Class<?> clazz) {
		return super.canProxy(clazz) || !Modifier.isFinal(clazz.getModifiers());
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

	@Override
	public Proxy getProxy(Class<?> clazz, Class<?>[] interfaces, ExecutionInterceptor executionInterceptor) {
		return new CglibProxy(clazz, getInterfaces(clazz, interfaces), executionInterceptor);
	}

	public Class<?> getProxyClass(Class<?> clazz, Class<?>[] interfaces) {
		if (super.canProxy(clazz)) {
			return super.getProxyClass(clazz, interfaces);
		}
		Enhancer enhancer = CglibUtils.createEnhancer(clazz, getInterfaces(clazz, interfaces));
		enhancer.setCallbackType(ExecutionInterceptorToMethodInterceptor.class);
		return enhancer.createClass();
	}

	@Override
	public Class<?> getUserClass(Class<?> clazz) {
		if (super.isProxy(clazz)) {
			return super.getUserClass(clazz);
		}
		Class<?> clz = clazz.getSuperclass();
		if (clz == null || clz == Object.class) {
			return clazz;
		}
		return clz;
	}

	@Override
	public Class<?> getUserClass(String className, ClassLoader classLoader) throws ClassNotFoundException {
		if (isProxy(className, classLoader)) {
			return super.getUserClass(className, classLoader);
		}
		return ClassUtils.forName(className.substring(0, className.indexOf(CGLIB_CLASS_SEPARATOR)), classLoader);
	}

	@Override
	public boolean isProxy(Class<?> clazz) {
		return super.isProxy(clazz) || Enhancer.isEnhanced(clazz);
	}

	@Override
	public boolean isProxy(String className, ClassLoader classLoader) throws ClassNotFoundException {
		if (className == null) {
			return false;
		}

		return super.isProxy(className, classLoader) || className.contains(CGLIB_CLASS_SEPARATOR);
	}
}
