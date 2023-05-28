package io.basc.framework.beans.support;

import io.basc.framework.beans.FactoryBean;
import io.basc.framework.beans.Scope;
import io.basc.framework.beans.config.BeanDefinition;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.execution.Executor;
import io.basc.framework.execution.parameter.ExecutionParametersExtractor;
import io.basc.framework.util.Elements;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
public class BeanDefinitionFactoryBean<T> implements FactoryBean<T> {
	private final String name;
	private final BeanDefinition beanDefinition;
	private final ExecutionParametersExtractor executionParametersExtractor;
	private volatile Executor executor;
	private volatile Object singleton;
	private volatile TypeDescriptor typeDescriptor;

	public Executor getExecutor() {
		if (executor == null) {
			synchronized (this) {
				if (executor == null) {
					for (Executor executor : beanDefinition.getExecutors()) {
						if (executionParametersExtractor.canExtractExecutionParameters(executor)) {
							this.executor = executor;
						}
					}
				}
			}
		}
		return executor;
	}

	@Override
	public boolean isPresent() {
		return singleton != null || executor != null || getExecutor() != null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T orElse(T other) {
		Executor executor = getExecutor();
		if (executor == null) {
			return other;
		}

		if (isSingleton()) {
			if (singleton == null) {
				synchronized (this) {
					if (singleton == null) {
						Elements<? extends Object> args = executionParametersExtractor
								.extractExecutionParameters(executor);
						singleton = executor.execute(args);
					}
				}
			}
			return (T) singleton;
		}

		Elements<? extends Object> args = executionParametersExtractor.extractExecutionParameters(executor);
		return (T) executor.execute(args);
	}

	@Override
	public TypeDescriptor getTypeDescriptor() {
		if (typeDescriptor == null) {
			synchronized (this) {
				if (typeDescriptor == null) {
					Executor executor = getExecutor();
					if (executor == null) {
						return TypeDescriptor.valueOf(Object.class);
					}

					this.typeDescriptor = executor.getTypeDescriptor();
				}
			}
		}
		return typeDescriptor;
	}

	@Override
	public Scope getScope() {
		return beanDefinition.getScope();
	}

	@Override
	public boolean isSingleton() {
		return singleton != null || beanDefinition.isSingleton();
	}

}
