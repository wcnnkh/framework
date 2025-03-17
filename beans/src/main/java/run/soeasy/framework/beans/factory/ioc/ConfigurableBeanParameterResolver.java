package run.soeasy.framework.beans.factory.ioc;

import run.soeasy.framework.beans.factory.BeanFactory;
import run.soeasy.framework.core.execution.ParameterDescriptorTemplate;
import run.soeasy.framework.core.execution.Parameters;
import run.soeasy.framework.util.spi.ConfigurableServices;

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
