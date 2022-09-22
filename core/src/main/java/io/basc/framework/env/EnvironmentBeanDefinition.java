package io.basc.framework.env;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.factory.support.FactoryBeanDefinition;

public class EnvironmentBeanDefinition extends FactoryBeanDefinition {
	private final Environment environment;

	public EnvironmentBeanDefinition(Environment environment, Class<?> type) {
		this(environment, TypeDescriptor.valueOf(type));
	}

	public EnvironmentBeanDefinition(Environment environment, TypeDescriptor typeDescriptor) {
		super(environment, typeDescriptor);
		this.environment = environment;
	}

	public Environment getEnvironment() {
		return this.environment;
	}
}
