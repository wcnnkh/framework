package io.basc.framework.beans.support;

import io.basc.framework.beans.BeanDefinition;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.execution.DefaultExecutors;
import io.basc.framework.factory.BeanPostProcessor;
import io.basc.framework.factory.BeansException;
import io.basc.framework.factory.ConfigurableServices;
import io.basc.framework.util.Assert;
import io.basc.framework.util.Elements;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class DefaultBeanDefinition extends DefaultExecutors implements BeanDefinition {
	private final ConfigurableServices<BeanPostProcessor> dependenceProcessors = new ConfigurableServices<BeanPostProcessor>(
			BeanPostProcessor.class);
	private final ConfigurableServices<BeanPostProcessor> destroyProcessors = new ConfigurableServices<BeanPostProcessor>(
			BeanPostProcessor.class);
	private final String id;
	private final ConfigurableServices<BeanPostProcessor> initProcessors = new ConfigurableServices<BeanPostProcessor>(
			BeanPostProcessor.class);
	private boolean isNew = true;
	private Elements<String> names;
	private boolean singleton;
	private boolean external;

	public DefaultBeanDefinition(TypeDescriptor typeDescriptor, String id) {
		super(typeDescriptor);
		Assert.requiredArgument(id != null, "id");
		this.id = id;
	}

	@Override
	public void dependence(Object instance) throws BeansException {
		for (BeanPostProcessor processor : dependenceProcessors.getServices()) {
			processor.processPostBean(instance, this);
		}
	}

	@Override
	public void destroy(Object instance) throws BeansException {
		for (BeanPostProcessor processor : destroyProcessors.getServices()) {
			processor.processPostBean(instance, this);
		}
	}

	public ConfigurableServices<BeanPostProcessor> getDependenceProcessors() {
		return dependenceProcessors;
	}

	public ConfigurableServices<BeanPostProcessor> getDestroyProcessors() {
		return destroyProcessors;
	}

	public ConfigurableServices<BeanPostProcessor> getInitProcessors() {
		return initProcessors;
	}

	public Elements<String> getNames() {
		return this.names == null ? Elements.empty() : names;
	}

	@Override
	public void init(Object instance) throws BeansException {
		for (BeanPostProcessor processor : initProcessors.getServices()) {
			processor.processPostBean(instance, this);
		}
	}
}
