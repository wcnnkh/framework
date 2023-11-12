package io.basc.framework.beans.factory.config.support;

import io.basc.framework.beans.BeansException;
import io.basc.framework.beans.FatalBeanException;
import io.basc.framework.beans.factory.config.BeanPostProcessor;
import io.basc.framework.execution.Executor;
import io.basc.framework.execution.param.ParameterExtractor;
import io.basc.framework.util.element.Elements;
import lombok.Data;

@Data
public abstract class HookBeanPostProcessor implements BeanPostProcessor {
	private final ParameterExtractor parameterExtractor;

	protected abstract Elements<? extends Executor> getInitializeExecutors(Object bean, String beanName);

	protected abstract Elements<? extends Executor> getDestroyExecutors(Object bean, String beanName);

	@Override
	public void postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		for (Executor executor : getInitializeExecutors(bean, beanName)) {
			if (!parameterExtractor.canExtractParameters(executor)) {
				throw new FatalBeanException("Unable to obtain the required parameters for this actuator " + executor);
			}

			Elements<Object> args = parameterExtractor.extractParameters(executor);
			try {
				executor.execute(args);
			} catch (Throwable e) {
				throw new FatalBeanException("Execution failed in '" + beanName + "'", e);
			}
		}
	}

	@Override
	public void postProcessBeforeDestroy(Object bean, String beanName) throws BeansException {
		for (Executor executor : getDestroyExecutors(bean, beanName)) {
			if (!parameterExtractor.canExtractParameters(executor)) {
				throw new FatalBeanException("Unable to obtain the required parameters for this actuator " + executor);
			}

			Elements<Object> args = parameterExtractor.extractParameters(executor);
			try {
				executor.execute(args);
			} catch (Throwable e) {
				throw new FatalBeanException("Execution failed in '" + beanName + "'", e);
			}
		}
	}
}
