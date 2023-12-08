package io.basc.framework.beans.factory.config.support;

import io.basc.framework.beans.BeansException;
import io.basc.framework.beans.FatalBeanException;
import io.basc.framework.beans.factory.config.AutowireCapableBeanFactory;
import io.basc.framework.beans.factory.config.BeanPostProcessor;
import io.basc.framework.execution.Executor;
import io.basc.framework.execution.param.Parameters;
import io.basc.framework.util.element.Elements;
import lombok.Data;

@Data
public abstract class HookBeanPostProcessor implements BeanPostProcessor {
	private final AutowireCapableBeanFactory autowireCapableBeanFactory;

	protected abstract Elements<? extends Executor> getInitializeExecutors(Object bean, String beanName);

	protected abstract Elements<? extends Executor> getDestroyExecutors(Object bean, String beanName);

	@Override
	public void postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		for (Executor executor : getInitializeExecutors(bean, beanName)) {
			if (!autowireCapableBeanFactory.canExtractParameters(executor)) {
				throw new FatalBeanException("Unable to obtain the required parameters for this actuator " + executor);
			}

			Parameters parameters = autowireCapableBeanFactory.extractParameters(executor);
			try {
				executor.execute(parameters.getArgs());
			} catch (Throwable e) {
				throw new FatalBeanException("Execution failed in '" + beanName + "'", e);
			}
		}
	}

	@Override
	public void postProcessBeforeDestroy(Object bean, String beanName) throws BeansException {
		for (Executor executor : getDestroyExecutors(bean, beanName)) {
			if (!autowireCapableBeanFactory.canExtractParameters(executor)) {
				throw new FatalBeanException("Unable to obtain the required parameters for this actuator " + executor);
			}

			Parameters parameters = autowireCapableBeanFactory.extractParameters(executor);
			try {
				executor.execute(parameters.getArgs());
			} catch (Throwable e) {
				throw new FatalBeanException("Execution failed in '" + beanName + "'", e);
			}
		}
	}
}
