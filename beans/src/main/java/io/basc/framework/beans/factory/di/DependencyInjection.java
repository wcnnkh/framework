package io.basc.framework.beans.factory.di;

import io.basc.framework.beans.factory.BeanFactory;
import io.basc.framework.core.execution.Executable;
import io.basc.framework.core.execution.Parameter;
import io.basc.framework.core.execution.ParameterDescriptorTemplate;
import io.basc.framework.core.execution.Parameters;
import io.basc.framework.core.mapping.Property;
import io.basc.framework.core.mapping.PropertyDescriptor;
import lombok.Data;

@Data
public class DependencyInjection implements BeanLifecycleResolver, BeanParameterResolver, BeanPropertyResolver {
	private final ConfigurableBeanLifecycleResolver configurableBeanLifecycleResolver = new ConfigurableBeanLifecycleResolver();
	private final ConfigurableBeanParameterResolver configurableBeanParameterResolver = new ConfigurableBeanParameterResolver();
	private final ConfigurableBeanPropertyResolver configurableBeanPropertyResolver = new ConfigurableBeanPropertyResolver();

	@Override
	public boolean canResolveProperty(BeanFactory beanFactory, PropertyDescriptor propertyDescriptor) {
		return configurableBeanPropertyResolver.canResolveProperty(beanFactory, propertyDescriptor);
	}

	@Override
	public Property resolveProperty(BeanFactory beanFactory, PropertyDescriptor propertyDescriptor) {
		return configurableBeanPropertyResolver.resolveProperty(beanFactory, propertyDescriptor);
	}

	@Override
	public boolean canResolveParameters(BeanFactory beanFactory, ParameterDescriptorTemplate template) {
		return configurableBeanParameterResolver.canResolveParameters(beanFactory, template)
				|| template.getParameterDescriptors().allMatch((e) -> canResolveProperty(beanFactory, e));
	}

	@Override
	public Parameters resolveParameters(BeanFactory beanFactory, ParameterDescriptorTemplate template) {
		if (configurableBeanParameterResolver.canResolveParameters(beanFactory, template)) {
			return configurableBeanParameterResolver.resolveParameters(beanFactory, template);
		}

		Parameter[] args = template.getParameterDescriptors().map((e) -> {
			Property property = resolveProperty(beanFactory, e);
			return Parameter.of(e.getIndex(), property);
		}).toArray(Parameter[]::new);
		return Parameters.completed(args);
	}

	@Override
	public boolean isStartupExecute(BeanFactory beanFactory, Executable executable) {
		return configurableBeanLifecycleResolver.isStartupExecute(beanFactory, executable);
	}

	@Override
	public boolean isStoppedExecute(BeanFactory beanFactory, Executable executable) {
		return configurableBeanLifecycleResolver.isStoppedExecute(beanFactory, executable);
	}
}
