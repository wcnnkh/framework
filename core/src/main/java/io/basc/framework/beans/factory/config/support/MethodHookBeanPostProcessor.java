package io.basc.framework.beans.factory.config.support;

import io.basc.framework.beans.factory.config.AutowireCapableBeanFactory;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.reflect.ReflectionUtils;
import io.basc.framework.execution.Executor;
import io.basc.framework.execution.Method;
import io.basc.framework.execution.reflect.ReflectionMethod;
import io.basc.framework.util.element.Elements;

public abstract class MethodHookBeanPostProcessor extends HookBeanPostProcessor {

	public MethodHookBeanPostProcessor(AutowireCapableBeanFactory autowireCapableBeanFactory) {
		super(autowireCapableBeanFactory);
	}

	protected Elements<? extends Method> getExecutors(Object bean, String beanName) {
		TypeDescriptor source = TypeDescriptor.forObject(bean);
		return ReflectionUtils.getDeclaredMethods(bean.getClass()).all().getElements().map((method) -> {
			ReflectionMethod executor = new ReflectionMethod(method, source);
			executor.setTarget(bean);
			return executor;
		});
	}

	@Override
	protected Elements<? extends Method> getInitializeExecutors(Object bean, String beanName) {
		return getExecutors(bean, beanName).filter((e) -> isInitializeExecutor(e, beanName));
	}

	@Override
	protected Elements<? extends Executor> getDestroyExecutors(Object bean, String beanName) {
		return getExecutors(bean, beanName).filter((e) -> isDestoryExecutor(e, beanName));
	}

	protected abstract boolean isInitializeExecutor(Method executor, String beanName);

	protected abstract boolean isDestoryExecutor(Method executor, String beanName);
}
