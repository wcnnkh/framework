package io.basc.framework.mvc.action;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.function.Supplier;

import io.basc.framework.beans.factory.BeanFactory;
import io.basc.framework.beans.factory.NameInstanceSupplier;
import io.basc.framework.beans.factory.support.InstanceIterable;
import io.basc.framework.util.function.SingletonSupplier;
import io.basc.framework.util.reflect.MethodInvoker;
import io.basc.framework.web.pattern.HttpPatternResolver;

public class BeanAction extends AbstractAction {
	private final BeanFactory beanFactory;
	private final MethodInvoker invoker;
	private Iterable<ActionInterceptor> actionInterceptors;

	public BeanAction(BeanFactory beanFactory, Class<?> targetClass, Method method,
			HttpPatternResolver httpPatternResolver, String controllerId, Collection<String> actionInterceptorNames) {
		super(targetClass, method, httpPatternResolver);
		this.beanFactory = beanFactory;
		Supplier<Object> instanceSupplier;

		if (Modifier.isStatic(method.getModifiers())) {
			// 静态方法不需要实例
			instanceSupplier = new SingletonSupplier<Object>(null);
		} else if (beanFactory.isSingleton(controllerId)) {
			// 提高一丢丢性能
			instanceSupplier = new SingletonSupplier<Object>(beanFactory.getInstance(controllerId));
		} else {
			instanceSupplier = new NameInstanceSupplier<Object>(beanFactory, controllerId);
		}
		this.invoker = beanFactory.getAop().getProxyMethod(targetClass, instanceSupplier, method);
		this.actionInterceptors = new InstanceIterable<ActionInterceptor>(beanFactory, actionInterceptorNames);
	}

	public BeanFactory getBeanFactory() {
		return beanFactory;
	}

	public Iterable<ActionInterceptor> getActionInterceptors() {
		return actionInterceptors;
	}

	public Object invoke(Object... args) throws Throwable {
		return invoker.invoke(args);
	}

	public Object getInstance() {
		return invoker.getInstance();
	}
}
