package io.basc.framework.beans.factory.config.support;

import java.util.Map;

import io.basc.framework.beans.BeanMapping;
import io.basc.framework.beans.factory.Scope;
import io.basc.framework.beans.factory.config.BeanDefinition;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.convert.lang.Value;
import io.basc.framework.execution.ExecutorRegistry;
import io.basc.framework.execution.Function;
import io.basc.framework.execution.Method;
import io.basc.framework.execution.param.Parameters;
import io.basc.framework.util.element.Elements;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

@Data
@EqualsAndHashCode(callSuper = true)
public class DefaultBeanDefinition extends ExecutorRegistry<Function> implements BeanDefinition {
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
	@NonNull
	private Parameters parameters = Parameters.empty();

	public DefaultBeanDefinition(TypeDescriptor returnTypeDescriptor) {
		super(returnTypeDescriptor);
	}

	@Override
	public String toString() {
		if (resourceDescription != null) {
			return resourceDescription;
		}
		return super.toString();
	}
}
