package io.basc.framework.beans.factory.support;

import io.basc.framework.beans.BeansException;
import io.basc.framework.beans.FatalBeanException;
import io.basc.framework.beans.factory.config.BeanDefinition;
import io.basc.framework.beans.factory.config.LifecycleFactoryBean;
import io.basc.framework.core.ResolvableType;
import io.basc.framework.execution.Executor;
import io.basc.framework.execution.parameter.ExecutableParametersExtractor;
import io.basc.framework.util.Elements;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class DefinitionFactoryBean implements LifecycleFactoryBean<Object> {
	private final BeanDefinition beanDefinition;
	private final ExecutableParametersExtractor executionParametersExtractor;
	private volatile Elements<? extends Object> singletonConstructionParameters;
	private volatile Executor executor;
	private volatile Object singletonObject;

	public Executor getExecutor() {
		if (executor == null) {
			synchronized (this) {
				if (executor == null) {
					for (Executor executor : beanDefinition.getExecutors()) {
						if (executionParametersExtractor.canExtractExecutionParameters(executor)) {
							this.executor = executor;
							break;
						}
					}
				}
			}
		}

		if (executor == null) {
			throw new FatalBeanException("Unable to construct this bean");
		}
		return executor;
	}

	@Override
	public ResolvableType getType() {
		return getExecutor().getReturnType().getResolvableType();
	}

	@Override
	public boolean isSingleton() {
		return beanDefinition.isSingleton();
	}

	private Object createObject() {
		Executor executor = getExecutor();
		Elements<? extends Object> args = executionParametersExtractor.extractExecutionParameters(executor);
		if (isSingleton()) {
			this.singletonConstructionParameters = args;
		}

		try {
			return executor.execute(args);
		} catch (Throwable e) {
			throw new FatalBeanException("Unable to obtain this instance", e);
		}
	}

	@Override
	public Object get() {
		if (isSingleton()) {
			if (singletonObject == null) {
				synchronized (this) {
					if (singletonObject == null) {
						singletonObject = createObject();
					}
				}
			}
			return singletonObject;
		} else {
			return createObject();
		}
	}

	@Override
	public void destroy(Object instance) throws BeansException {
		beanDefinition.destroy(getExecutor(), instance);
	}

	@Override
	public void init(Object instance) throws BeansException {
		beanDefinition.init(getExecutor(), instance);
	}
}
