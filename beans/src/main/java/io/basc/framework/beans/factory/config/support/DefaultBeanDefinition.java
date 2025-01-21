package io.basc.framework.beans.factory.config.support;

import io.basc.framework.beans.BeanMapping;
import io.basc.framework.beans.factory.Scope;
import io.basc.framework.beans.factory.config.BeanDefinition;
import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.transform.Properties;
import io.basc.framework.core.env.config.DefaultPropertyResolver;
import io.basc.framework.core.execution.ExecutionStrategy;
import io.basc.framework.core.execution.Function;
import io.basc.framework.core.execution.Method;
import io.basc.framework.util.collections.Elements;
import lombok.Data;

@Data
public class DefaultBeanDefinition implements BeanDefinition {
	private String resourceDescription;
	// 默认使用单例
	private boolean singleton = true;
	private BeanDefinition originatingBeanDefinition;
	private Scope scope = Scope.DEFAULT;
	private final ExecutionStrategy<Function> executionStrategy;
	private BeanMapping beanMappingDescriptor;
	private String name;
	private final Properties properties = new DefaultPropertyResolver();
	private Elements<Method> initMethods;
	private Elements<Method> destroyMethods;

	public DefaultBeanDefinition(TypeDescriptor returnTypeDescriptor) {
		this.executionStrategy = new ExecutionStrategy<>(returnTypeDescriptor);
	}

	@Override
	public String toString() {
		if (resourceDescription != null) {
			return resourceDescription;
		}
		return super.toString();
	}
}
