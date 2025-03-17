package run.soeasy.framework.beans.factory.config.support;

import lombok.Data;
import run.soeasy.framework.beans.BeanMapping;
import run.soeasy.framework.beans.factory.Scope;
import run.soeasy.framework.beans.factory.config.BeanDefinition;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.convert.transform.stereotype.Properties;
import run.soeasy.framework.core.env.config.DefaultPropertyResolver;
import run.soeasy.framework.core.execution.ExecutionStrategy;
import run.soeasy.framework.core.execution.Function;
import run.soeasy.framework.core.execution.Method;
import run.soeasy.framework.util.collections.Elements;

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
