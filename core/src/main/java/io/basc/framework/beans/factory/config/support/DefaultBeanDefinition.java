package io.basc.framework.beans.factory.config.support;

import java.util.Map;

import io.basc.framework.beans.BeanMapping;
import io.basc.framework.beans.factory.Scope;
import io.basc.framework.beans.factory.config.BeanDefinition;
import io.basc.framework.execution.Executor;
import io.basc.framework.execution.Method;
import io.basc.framework.execution.support.DefaultService;
import io.basc.framework.util.element.Elements;
import io.basc.framework.value.Value;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class DefaultBeanDefinition extends DefaultService<Executor> implements BeanDefinition {
	private String resourceDescription;
	// 默认使用单例
	private boolean singleton = true;
	private BeanDefinition originatingBeanDefinition;
	private Scope scope = Scope.DEFAULT;
	private BeanMapping beanMapping;
	private String name;
	private Map<String, Value> properties;
	private Elements<Method> initMethods;
	private Elements<Method> destroyMethods;

	@Override
	public String toString() {
		if (resourceDescription != null) {
			return resourceDescription;
		}
		return super.toString();
	}
}
