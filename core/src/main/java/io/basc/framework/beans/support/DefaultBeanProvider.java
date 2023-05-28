package io.basc.framework.beans.support;

import java.util.concurrent.atomic.AtomicBoolean;

import io.basc.framework.beans.FactoryBean;
import io.basc.framework.beans.Scope;
import io.basc.framework.beans.config.BeanDefinition;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.execution.Executor;
import io.basc.framework.execution.parameter.ExecutionParametersExtractor;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DefaultBeanProvider<T> implements FactoryBean<T> {
	private final String name;
	private final BeanDefinition beanDefinition;
	private final ExecutionParametersExtractor executionParametersExtractor;
	private volatile Executor executor;
	private volatile Object singleton;
	private AtomicBoolean singletonCreated = new AtomicBoolean();
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public T orElse(T other) {
		Executor executor = getExecutor();
		if(executor == null) {
			return other;
		}
		
		if(isSingleton()) {
			if(singleton == null && !singletonCreated.get()) {
				synchronized (this) {
					if(singleton == null && !singletonCreated.get()) {
						
					}
				}
			}
		}
	}

	@Override
	public TypeDescriptor getTypeDescriptor() {
		return getExecutor().getTypeDescriptor();
	}

	@Override
	public Scope getScope() {
		return beanDefinition.getScope();
	}

	@Override
	public Executor getExecutor() {
		if(executor == null) {
			synchronized (this) {
				if(executor == null) {
					for(Executor executor : beanDefinition.getExecutors()) {
						if(executionParametersExtractor.canExtractExecutionParameters(executor)) {
							this.executor = executor;
							break;
						}
					}
				}
			}
		}
		return executor;
	}

	@Override
	public boolean isSingleton() {
		// TODO Auto-generated method stub
		return false;
	}

}
