package scw.aop;

import java.lang.reflect.Method;

public interface ProxyInvoker extends MethodInvoker {
	Object getProxy();

	public abstract class AbstractProxyInvoker implements ProxyInvoker {
		private final Class<?> targetClass;
		private final Method method;

		public AbstractProxyInvoker(Class<?> targetClass, Method method) {
			this.method = method;
			this.targetClass = targetClass;
		}

		public Method getMethod() {
			return method;
		}

		public Class<?> getTargetClass() {
			return targetClass;
		}
		
		@Override
		public String toString() {
			return getMethod().toString();
		}
	}

	public abstract class AbstractInstanceProxyInvoker extends
			AbstractProxyInvoker {
		private final Object proxy;

		public AbstractInstanceProxyInvoker(Object proxy, Class<?> targetClass,
				Method method) {
			super(targetClass, method);
			this.proxy = proxy;
		}

		public Object getProxy() {
			return proxy;
		}
	}

	public static class ProxyInvokerWrapper implements ProxyInvoker {
		private ProxyInvoker invoker;

		public ProxyInvokerWrapper(ProxyInvoker invoker) {
			this.invoker = invoker;
		}

		public Method getMethod() {
			return invoker.getMethod();
		}

		public Class<?> getTargetClass() {
			return invoker.getTargetClass();
		}

		public Object invoke(Object... args) throws Throwable {
			return invoker.invoke(args);
		}

		public Object getProxy() {
			return invoker.getProxy();
		}
		
		@Override
		public String toString() {
			return invoker.toString();
		}
		
		@Override
		public boolean equals(Object obj) {
			return invoker.equals(obj);
		}
		
		@Override
		public int hashCode() {
			return invoker.hashCode();
		}
	}
}
