package io.basc.framework.core.execution.resolver;

import io.basc.framework.core.convert.Source;
import io.basc.framework.core.convert.transform.stereotype.PropertyDescriptor;
import io.basc.framework.core.convert.transform.stereotype.PropertyFactory;
import io.basc.framework.core.convert.transform.stereotype.PropertyPropertyFactories;
import io.basc.framework.core.execution.Parameter;
import io.basc.framework.core.execution.ParameterDescriptorTemplate;
import io.basc.framework.core.execution.Parameters;
import io.basc.framework.util.collections.Elements;
import io.basc.framework.util.exchange.Receipt;
import io.basc.framework.util.exchange.Receipts;
import io.basc.framework.util.spi.ConfigurableServices;
import io.basc.framework.util.spi.ServiceLoaderDiscovery;

public class ParameterFactories extends ConfigurableServices<ParameterFactory>
		implements ParameterFactory, PropertyFactory {
	private final PropertyPropertyFactories configurablePropertyResolver = new PropertyPropertyFactories();
	private final DefaultValueFactories configurableDefaultValueResolver = new DefaultValueFactories();

	public ParameterFactories() {
		setServiceClass(ParameterFactory.class);
	}

	@Override
	public Receipt doConfigure(ServiceLoaderDiscovery discovery) {
		return Receipts
				.of(Elements.forArray(super.doConfigure(discovery), configurablePropertyResolver.doConfigure(discovery),
						configurableDefaultValueResolver.doConfigure(discovery)));
	}

	public PropertyPropertyFactories getConfigurablePropertyResolver() {
		return configurablePropertyResolver;
	}

	public DefaultValueFactories getConfigurableDefaultValueResolver() {
		return configurableDefaultValueResolver;
	}

	@Override
	public boolean hasProperty(PropertyDescriptor propertyDescriptor) {
		return configurablePropertyResolver.hasProperty(propertyDescriptor)
				|| configurableDefaultValueResolver.hasDefaultValue(propertyDescriptor);
	}

	@Override
	public Source getProperty(PropertyDescriptor propertyDescriptor) {
		if (configurablePropertyResolver.hasProperty(propertyDescriptor)) {
			return configurablePropertyResolver.getProperty(propertyDescriptor);
		}
		return configurableDefaultValueResolver.getDefaultValue(propertyDescriptor);
	}

	@Override
	public boolean hasParameters(ParameterDescriptorTemplate parameterTemplate) {
		return anyMatch((e) -> e.hasParameters(parameterTemplate))
				|| parameterTemplate.getParameterDescriptors().allMatch((e) -> hasProperty(e));
	}

	@Override
	public Parameters getParameters(ParameterDescriptorTemplate parameterTemplate) {
		for (ParameterFactory resolver : this) {
			if (resolver.hasParameters(parameterTemplate)) {
				return resolver.getParameters(parameterTemplate);
			}
		}

		Elements<Parameter> elements = parameterTemplate.getParameterDescriptors().map((e) -> {
			Source value = getProperty(e);
			Parameter parameter = Parameter.of(e);
			parameter.set(value.get());
			return parameter;
		});
		return Parameters.completed(elements.toArray(Parameter[]::new));
	}

}
