package io.basc.framework.beans.factory.di;

import io.basc.framework.beans.factory.BeanFactory;
import io.basc.framework.core.execution.ParameterDescriptorTemplate;
import io.basc.framework.core.execution.Parameters;
import io.basc.framework.util.spi.ConfigurableServices;

public class ConfigurableBeanParameterResolver extends
		ConfigurableServices<BeanParameterResolver> implements BeanParameterResolver {

	public ConfigurableBeanParameterResolver() {
		setServiceClass(BeanParameterResolver.class);
	}

	@Override
	public boolean canResolveParameters(BeanFactory beanFactory, ParameterDescriptorTemplate template) {
		return anyMatch((e) -> e.canResolveParameters(beanFactory, template));
	}

	@Override
	public Parameters resolveParameters(BeanFactory beanFactory, ParameterDescriptorTemplate template) {
		for (BeanParameterResolver resolver : this) {
			if (resolver.canResolveParameters(beanFactory, template)) {
				return resolver.resolveParameters(beanFactory, template);
			}
		}
		throw new UnsupportedOperationException(template.toString());
	}

}
