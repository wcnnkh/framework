package scw.aop;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

import scw.cglib.proxy.Enhancer;
import scw.cglib.proxy.Factory;
import scw.cglib.proxy.MethodInterceptor;
import scw.cglib.proxy.MethodProxy;
import scw.core.instance.annotation.Configuration;
import scw.core.utils.ClassUtils;
import scw.lang.NestedExceptionUtils;
import scw.util.result.SimpleResult;

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
		enhancer.setCallbackType(FiltersConvertCglibMethodInterceptor.class);
		return enhancer.createClass();
	}

	public Proxy getProxy(Class<?> clazz, Class<?>[] interfaces, FilterChain filterChain) {
		return new CglibProxy(clazz, getInterfaces(clazz, interfaces),
				new FiltersConvertCglibMethodInterceptor(clazz, filterChain));
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

	private static final class FiltersConvertCglibMethodInterceptor implements MethodInterceptor, Serializable {
		private static final long serialVersionUID = 1L;
		private final Class<?> targetClass;
		private final FilterChain filterChain;

		public FiltersConvertCglibMethodInterceptor(Class<?> targetClass, FilterChain filterChain) {
			this.targetClass = targetClass;
			this.filterChain = filterChain;
		}

		public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
			SimpleResult<Object> ignoreResult = ProxyUtils.ignoreMethod(obj, method, args);
			if (ignoreResult.isSuccess()) {
				return ignoreResult.getData();
			}

			if (filterChain == null) {
				return proxy.invokeSuper(obj, args);
			}

			ProxyContext context = new ProxyContext(obj, targetClass, method, args, null);
			Invoker invoker = new CglibInvoker(proxy, obj);
			return filterChain.doFilter(invoker, context);
		}
	}

	private static final class CglibInvoker implements Invoker {
		private final MethodProxy proxy;
		private final Object obj;

		public CglibInvoker(MethodProxy proxy, Object obj) {
			this.proxy = proxy;
			this.obj = obj;
		}

		public Object invoke(Object... args) throws Throwable {
			try {
				return proxy.invokeSuper(obj, args);
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
