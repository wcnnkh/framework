package io.basc.framework.beans.factory.config.support;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.reflect.ReflectionUtils;
import io.basc.framework.execution.Executor;
import io.basc.framework.execution.parameter.ExecutionParametersExtractor;
import io.basc.framework.execution.reflect.MethodExecutor;
import io.basc.framework.util.Elements;

public abstract class MethodHookBeanPostProcessor extends HookBeanPostProcessor {

	public MethodHookBeanPostProcessor(ExecutionParametersExtractor executionParametersExtractor) {
		super(executionParametersExtractor);
	}

	protected Elements<? extends Executor> getExecutors(Object bean, String beanName) {
		TypeDescriptor source = TypeDescriptor.forObject(bean);
		return ReflectionUtils.getDeclaredMethods(bean.getClass()).all().getElements()
				.map((method) -> new MethodExecutor(source, method, bean));
	}

	@Override
	protected Elements<? extends Executor> getInitializeExecutors(Object bean, String beanName) {
		return getExecutors(bean, beanName).filter((e) -> isInitializeExecutor(e, beanName));
	}

	@Override
	protected Elements<? extends Executor> getDestroyExecutors(Object bean, String beanName) {
		return getExecutors(bean, beanName).filter((e) -> isDestoryExecutor(e, beanName));
	}

	protected abstract boolean isInitializeExecutor(Executor executor, String beanName);

	protected abstract boolean isDestoryExecutor(Executor executor, String beanName);
}
