package io.basc.framework.beans.factory.config.support;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.reflect.ReflectionUtils;
import io.basc.framework.execution.Executor;
import io.basc.framework.execution.MethodExecutor;
import io.basc.framework.execution.param.ParameterExtractor;
import io.basc.framework.execution.reflect.ReflectionMethodExecutor;
import io.basc.framework.util.element.Elements;

public abstract class MethodHookBeanPostProcessor extends HookBeanPostProcessor {

	public MethodHookBeanPostProcessor(ParameterExtractor parameterExtractor) {
		super(parameterExtractor);
	}

	protected Elements<? extends MethodExecutor> getExecutors(Object bean, String beanName) {
		TypeDescriptor source = TypeDescriptor.forObject(bean);
		return ReflectionUtils.getDeclaredMethods(bean.getClass()).all().getElements().map((method) -> {
			ReflectionMethodExecutor executor = new ReflectionMethodExecutor(method, source);
			executor.setTarget(bean);
			return executor;
		});
	}

	@Override
	protected Elements<? extends MethodExecutor> getInitializeExecutors(Object bean, String beanName) {
		return getExecutors(bean, beanName).filter((e) -> isInitializeExecutor(e, beanName));
	}

	@Override
	protected Elements<? extends Executor> getDestroyExecutors(Object bean, String beanName) {
		return getExecutors(bean, beanName).filter((e) -> isDestoryExecutor(e, beanName));
	}

	protected abstract boolean isInitializeExecutor(MethodExecutor executor, String beanName);

	protected abstract boolean isDestoryExecutor(MethodExecutor executor, String beanName);
}
