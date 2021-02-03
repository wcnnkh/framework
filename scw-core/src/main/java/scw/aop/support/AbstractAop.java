package scw.aop.support;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import scw.aop.Aop;
import scw.aop.MethodInterceptor;
import scw.aop.Proxy;
import scw.aop.ProxyFactory;
import scw.aop.ProxyInstanceTarget;
import scw.core.reflect.MethodInvoker;
import scw.core.utils.ArrayUtils;
import scw.instance.InstanceException;
import scw.util.StaticSupplier;
import scw.util.Supplier;

public abstract class AbstractAop implements Aop{
	
	public abstract ProxyFactory getProxyFactory();
	
	public boolean isProxy(Object instance) {
		if(instance == null){
			return false;
		}
		
		return getProxyFactory().isProxy(instance.getClass());
	}

	public Proxy getProxy(Class<?> clazz, Class<?>[] interfaces,
			MethodInterceptor methodInterceptor) {
		if(methodInterceptor == null){
			return getProxyFactory().getProxy(clazz, interfaces, getMethodInterceptor());
		}
		
		ConfigurableMethodInterceptor interceptors = new ConfigurableMethodInterceptor();
		interceptors.addMethodInterceptor(getMethodInterceptor());
		interceptors.addMethodInterceptor(methodInterceptor);
		return getProxyFactory().getProxy(clazz, interfaces, interceptors);
	}
	
	public final Proxy getProxy(Class<?> clazz) {
		return getProxy(clazz, null, null);
	}
	
	public <T> Proxy getProxy(Class<? extends T> clazz,
			Supplier<T> instanceSupplier, Class<?>[] interfaces,
			MethodInterceptor methodInterceptor) {
		return new InstanceProxy(clazz, interfaces, methodInterceptor, instanceSupplier);
	}
	
	public final <T> Proxy getProxy(Class<? extends T> clazz, Supplier<T> instanceSupplier) {
		return getProxy(clazz, instanceSupplier, null, null);
	}
	
	public final <T> Proxy getProxy(Class<? extends T> clazz, T instance) {
		return getProxy(clazz, instance, null, null);
	}
	
	public <T> Proxy getProxy(Class<? extends T> clazz, T instance,
			Class<?>[] interfaces, MethodInterceptor methodInterceptor) {
		ConfigurableMethodInterceptor interceptors = new ConfigurableMethodInterceptor();
		interceptors.setInstance(instance);
		if (!isProxy(instance)) {
			interceptors.addMethodInterceptor(getMethodInterceptor());
		}
		interceptors.addMethodInterceptor(methodInterceptor);
		return getProxyFactory().getProxy(clazz,
				ArrayUtils.merge(interfaces, ProxyInstanceTarget.CLASSES),
				interceptors);
	}

	protected boolean isProxyMethod(Object instance, Method method) {
		boolean isProxy = !(Modifier.isPrivate(method.getModifiers())
				|| Modifier.isStatic(method.getModifiers())
				|| Modifier.isFinal(method.getModifiers()) || Modifier
				.isNative(method.getModifiers()));
		if (isProxy) {
			isProxy = instance != null && isProxy(instance);
		}
		return isProxy;
	}

	public final <T> MethodInvoker getProxyMethod(Class<? extends T> targetClass, T instance,
			Method method) {
		return getProxyMethod(targetClass, instance, method, null);
	}
	
	public final <T> MethodInvoker getProxyMethod(Class<? extends T> targetClass,
			Supplier<T> instanceSupplier, Method method) {
		return getProxyMethod(targetClass, instanceSupplier, method, null);
	}

	public <T> MethodInvoker getProxyMethod(Class<? extends T> targetClass, T instance,
			Method method, MethodInterceptor methodInterceptor) {
		return getProxyMethod(targetClass, new StaticSupplier<T>(instance),
				method, methodInterceptor);
	}

	public <T> MethodInvoker getProxyMethod(Class<? extends T> targetClass,
			Supplier<T> instanceSupplier, Method method,
			MethodInterceptor methodInterceptor) {
		return new ProxyMethod(instanceSupplier, targetClass, method,
				methodInterceptor);
	}

	protected final class InstanceProxy extends AbstractProxy {
		private final Supplier<?> instanceSupplier;
		
		public InstanceProxy(Class<?> targetClass, Class<?>[] interfaces,
				MethodInterceptor methodInterceptor, Supplier<?> instanceSupplier) {
			super(targetClass, interfaces, methodInterceptor);
			this.instanceSupplier = instanceSupplier;
		}
		
		private Proxy getProxy(){
			Object instance = instanceSupplier.get();
			ConfigurableMethodInterceptor interceptors = new ConfigurableMethodInterceptor();
			interceptors.setInstance(instance);
			if (!isProxy(instance)) {
				interceptors.addMethodInterceptor(AbstractAop.this.getMethodInterceptor());
			}
			interceptors.addMethodInterceptor(getMethodInterceptor());
			return getProxyFactory().getProxy(getTargetClass(),
					ArrayUtils.merge(getInterfaces(), ProxyInstanceTarget.CLASSES),
					interceptors);
		}

		public Object create() throws InstanceException {
			return getProxy().create();
		}

		@Override
		protected Object createInternal(Class<?>[] parameterTypes,
				Object[] params) {
			return getProxy().create(parameterTypes, params);
		}
	}

	protected final class ProxyMethod extends AbstractProxyMethodInvoker {
		private static final long serialVersionUID = 1L;
		private final MethodInterceptor methodInterceptor;

		public ProxyMethod(Supplier<?> instanceSupplier, Class<?> sourceClass,
				Method method, MethodInterceptor methodInterceptor) {
			super(instanceSupplier, sourceClass, method);
			this.methodInterceptor = methodInterceptor;
		}

		@Override
		protected MethodInterceptor getMethodInterceptor(MethodInvoker invoker) {
			if (isProxyMethod(invoker.getInstance(), invoker.getMethod())) {
				return this.methodInterceptor;
			}

			if (this.methodInterceptor == null) {
				return AbstractAop.this.getMethodInterceptor();
			}

			ConfigurableMethodInterceptor interceptors = new ConfigurableMethodInterceptor();
			interceptors.addMethodInterceptor(AbstractAop.this.getMethodInterceptor());
			interceptors.addMethodInterceptor(this.methodInterceptor);
			return interceptors;
		}
	}
}
