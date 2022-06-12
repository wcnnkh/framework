package io.basc.framework.factory.support;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.factory.ConfigurableServices;
import io.basc.framework.factory.FactoryResolver;
import io.basc.framework.factory.annotation.AnnotationFactoryInstanceResolverExtend;

public class FactoryResolvers extends ConfigurableServices<FactoryResolverExtend> implements FactoryResolver {
	private FactoryResolverConfiguration factoryResolverConfiguration = new FactoryResolverConfiguration();

	public FactoryResolvers() {
		super(FactoryResolverExtend.class);
		addService(new AnnotationFactoryInstanceResolverExtend());
	}

	public FactoryResolverConfiguration getFactoryResolverConfiguration() {
		return factoryResolverConfiguration;
	}

	public void setFactoryResolverConfiguration(FactoryResolverConfiguration factoryResolverConfiguration) {
		this.factoryResolverConfiguration = factoryResolverConfiguration;
	}

	@Override
	public boolean isSingleton(TypeDescriptor type) {
		return FactoryResolverExtendChain.build(iterator(), getFactoryResolverConfiguration()).isSingleton(type);
	}
}
