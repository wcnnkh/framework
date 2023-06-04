package io.basc.framework.beans.factory.support;

import io.basc.framework.beans.BeansException;
import io.basc.framework.beans.factory.config.BeanDefinition;
import io.basc.framework.beans.factory.config.LifecycleFactoryBean;
import io.basc.framework.core.ResolvableType;
import io.basc.framework.execution.Executor;
import io.basc.framework.execution.parameter.ExecutionParametersExtractor;
import io.basc.framework.util.Elements;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class DefinitionFactoryBean implements LifecycleFactoryBean<Object> {
	private final BeanDefinition beanDefinition;
	private final ExecutionParametersExtractor executionParametersExtractor;
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
		// TODO 如果为空就说明不可以构造
		return executor;
	}

	@Override
	public ResolvableType getType() {
		return getExecutor().getTypeDescriptor().getResolvableType();
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
		return executor.execute(args);
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
		beanDefinition.dependence(getExecutor(), instance);
	}

	@Override
	public void init(Object instance) throws BeansException {
		beanDefinition.init(getExecutor(), instance);
	}

	@Override
	public void dependence(Object instance) throws BeansException {
		beanDefinition.dependence(getExecutor(), instance);
	}
}
