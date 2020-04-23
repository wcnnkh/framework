package scw.aop;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import scw.cglib.proxy.Enhancer;
import scw.cglib.proxy.MethodInterceptor;
import scw.cglib.proxy.MethodProxy;
import scw.core.instance.annotation.Configuration;
import scw.lang.NestedExceptionUtils;
import scw.util.SimpleResult;

@Configuration(order = Integer.MIN_VALUE)
public class CglibProxyAdapter extends AbstractProxyAdapter {
	public boolean isSupport(Class<?> clazz) {
		return !Modifier.isFinal(clazz.getModifiers());
	}

	public boolean isProxy(Class<?> clazz) {
		return Enhancer.isEnhanced(clazz);
	}

	public Class<?> getClass(Class<?> clazz, Class<?>[] interfaces) {
		return CglibProxy.createEnhancer(clazz,
				mergeInterfaces(clazz, interfaces)).createClass();
	}

	public Proxy proxy(Class<?> clazz, Class<?>[] interfaces,
			FilterChain filterChain) {
		return new CglibProxy(clazz, mergeInterfaces(clazz, interfaces),
				new FiltersConvertCglibMethodInterceptor(clazz, filterChain));
	}

	private static final class FiltersConvertCglibMethodInterceptor implements
			MethodInterceptor, Serializable {
		private static final long serialVersionUID = 1L;
		private final Class<?> targetClass;
		private final FilterChain filterChain;

		public FiltersConvertCglibMethodInterceptor(Class<?> targetClass,
				FilterChain filterChain) {
			this.targetClass = targetClass;
			this.filterChain = filterChain;
		}

		public Object intercept(Object obj, Method method, Object[] args,
				MethodProxy proxy) throws Throwable {
			SimpleResult<Object> ignoreResult = ProxyUtils.ignoreMethod(obj,
					method, args);
			if (ignoreResult.isSuccess()) {
				return ignoreResult.getData();
			}

			Context context = new Context(obj, targetClass, method, args);
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

		public CglibProxy(Class<?> clazz, Class<?>[] interfaces,
				MethodInterceptor methodInterceptor) {
			super(clazz);
			this.enhancer = createEnhancer(clazz, interfaces);
			this.enhancer.setCallback(methodInterceptor);
		}

		public Object create() {
			return enhancer.create();
		}

		public Object createInternal(Class<?>[] parameterTypes,
				Object[] arguments) {
			return enhancer.create(parameterTypes, arguments);
		}

		public static Enhancer createEnhancer(Class<?> clazz,
				Class<?>[] interfaces) {
			Enhancer enhancer = new Enhancer();
			if (Serializable.class.isAssignableFrom(clazz)) {
				enhancer.setSerialVersionUID(1L);
			}
			if (interfaces != null) {
				enhancer.setInterfaces(interfaces);
			}
			enhancer.setSuperclass(clazz);
			return enhancer;
		}
	}
}
