package scw.aop;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;

import scw.aop.ProxyInvoker.AbstractInstanceProxyInvoker;
import scw.cglib.proxy.Enhancer;
import scw.cglib.proxy.Factory;
import scw.cglib.proxy.MethodInterceptor;
import scw.cglib.proxy.MethodProxy;
import scw.core.instance.annotation.Configuration;
import scw.core.utils.ClassUtils;
import scw.lang.NestedExceptionUtils;

@Configuration(order = Integer.MIN_VALUE)
public class CglibProxyFactory implements ProxyFactory {
	public boolean isSupport(Class<?> clazz) {
		return !Modifier.isFinal(clazz.getModifiers());
	}

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
		Enhancer enhancer = createEnhancer(clazz, getInterfaces(clazz, interfaces));
		enhancer.setCallbackType(CglibMethodInterceptor.class);
		return enhancer.createClass();
	}

	public Proxy getProxy(Class<?> clazz, Class<?>[] interfaces, Filter ...filters) {
		return new CglibProxy(clazz, getInterfaces(clazz, interfaces),
				new CglibMethodInterceptor(clazz, filters));
	}

	private static Enhancer createEnhancer(Class<?> clazz, Class<?>[] interfaces) {
		Enhancer enhancer = new Enhancer();
		if (Serializable.class.isAssignableFrom(clazz)) {
			enhancer.setSerialVersionUID(1L);
		}
		if (interfaces != null) {
			enhancer.setInterfaces(interfaces);
		}
		enhancer.setSuperclass(clazz);
		enhancer.setUseCache(true);
		return enhancer;
	}

	private static final class CglibMethodInterceptor implements MethodInterceptor, Serializable {
		private static final long serialVersionUID = 1L;
		private final Class<?> targetClass;
		private final Filter[] filters;

		public CglibMethodInterceptor(Class<?> targetClass, Filter[] filters) {
			this.targetClass = targetClass;
			this.filters = filters;
		}

		public Object intercept(Object obj, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
			Enumeration<Filter> enumeration = filters == null? null:Collections.enumeration(Arrays.asList(filters));
			return new CglibProxyInvoker(obj, targetClass, method, methodProxy, enumeration).invoke(args);
		}
	}

	private static final class CglibProxyInvoker extends AbstractInstanceProxyInvoker {
		private final MethodProxy methodProxy;
		private final Enumeration<Filter> filters;
		
		public CglibProxyInvoker(Object proxy, Class<?> targetClass,
				Method method, MethodProxy methodProxy, Enumeration<Filter> filters) {
			super(proxy, targetClass, method);
			this.methodProxy = methodProxy;
			this.filters = filters;
		}
		
		public Object invoke(Object... args) throws Throwable {
			if(filters.hasMoreElements()){
				return filters.nextElement().doFilter(this, args);
			}
			
			try {
				return methodProxy.invokeSuper(getProxy(), args);
			} catch (Throwable e) {
				throw NestedExceptionUtils.excludeInvalidNestedExcpetion(e);
			}
		}
	}

	public Class<?> getUserClass(Class<?> clazz) {
		Class<?> clz = clazz.getSuperclass();
		if (clz == null || clz == Object.class) {
			return clazz;
		}
		return clz;
	}

	public static final class CglibProxy extends AbstractProxy {
		private Enhancer enhancer;

		public CglibProxy(Class<?> clazz, Class<?>[] interfaces, MethodInterceptor methodInterceptor) {
			super(clazz);
			this.enhancer = createEnhancer(clazz, interfaces);
			this.enhancer.setCallback(methodInterceptor);
		}

		public Object create() {
			return enhancer.create();
		}

		public Object createInternal(Class<?>[] parameterTypes, Object[] arguments) {
			return enhancer.create(parameterTypes, arguments);
		}
	}

	/** The CGLIB class separator: "$$" */
	public static final String CGLIB_CLASS_SEPARATOR = "$$";

	public boolean isProxy(String className, ClassLoader classLoader) {
		return (className != null && className.contains(CGLIB_CLASS_SEPARATOR));
	}

	public Class<?> getUserClass(String className, boolean initialize, ClassLoader classLoader)
			throws ClassNotFoundException {
		return ClassUtils.forName(className.substring(0, className.indexOf(CGLIB_CLASS_SEPARATOR)), initialize,
				classLoader);
	}
}
