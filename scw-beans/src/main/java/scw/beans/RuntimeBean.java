package scw.beans;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.concurrent.atomic.AtomicBoolean;

import scw.aop.MethodInterceptor;
import scw.core.reflect.MethodInvoker;
import scw.core.utils.ArrayUtils;

public interface RuntimeBean {
	static final Class<?>[] PROXY_INTERFACES = new Class<?>[] { RuntimeBean.class };

	BeanDefinition getBeanDefinition();

	boolean _dependence();

	boolean _init();

	boolean _destroy();
	
	static class RuntimeBeanMethodInterceptor implements MethodInterceptor {
		private final AtomicBoolean _dependence = new AtomicBoolean();
		private final AtomicBoolean _init = new AtomicBoolean();
		private final AtomicBoolean _destroy = new AtomicBoolean();
		private final BeanDefinition beanDefinition;

		public RuntimeBeanMethodInterceptor(BeanDefinition beanDefinition) {
			this.beanDefinition = beanDefinition;
		}

		public Object intercept(MethodInvoker invoker, Object[] args) throws Throwable {
			if (ArrayUtils.isEmpty(args)) {
				Method method = invoker.getMethod();
				if ((Modifier.isAbstract(method.getModifiers()) || Modifier.isInterface(method.getModifiers()))) {
					if (method.getName().equals("getBeanDefinition")) {
						return beanDefinition;
					}

					if (method.getName().equals("_dependence")) {
						if (_dependence.get()) {
							return false;
						}

						return _dependence.compareAndSet(false, true);
					}

					if (method.getName().equals("_init")) {
						if (_init.get()) {
							return false;
						}

						return _init.compareAndSet(false, true);
					}

					if (method.getName().equals("_destroy")) {
						if (_destroy.get()) {
							return false;
						}

						return _destroy.compareAndSet(false, true);
					}
				}
			}
			return invoker.invoke(args);
		}
	}
}
