package io.basc.framework.context;

import java.io.IOException;
import java.util.Collection;

import io.basc.framework.core.type.classreading.MetadataReader;
import io.basc.framework.core.type.classreading.MetadataReaderFactory;
import io.basc.framework.factory.BeanDefinition;
import io.basc.framework.factory.ConfigurableServices;
import io.basc.framework.mapper.ParameterDescriptor;
import io.basc.framework.util.JavaVersion;
import io.basc.framework.value.ValueFactory;

public class ConfigurableContextResolver extends ConfigurableServices<ContextResolverExtend>
		implements ContextResolver {
	private ContextResolver defaultResolver;
	private ValueFactory<String> propertyFactory;

	public ConfigurableContextResolver() {
		super(ContextResolverExtend.class);
	}

	public ContextResolver getDefaultResolver() {
		return defaultResolver;
	}

	public void setDefaultResolver(ContextResolver defaultResolver) {
		this.defaultResolver = defaultResolver;
	}

	public ValueFactory<String> getPropertyFactory() {
		return propertyFactory;
	}

	public void setPropertyFactory(ValueFactory<String> propertyFactory) {
		this.propertyFactory = propertyFactory;
	}

	@Override
	public ProviderDefinition getProviderDefinition(Class<?> clazz) {
		return ContextResolverChain.build(iterator(), getDefaultResolver()).getProviderDefinition(clazz);
	}

	@Override
	public boolean hasContext(ParameterDescriptor parameterDescriptor) {
		return ContextResolverChain.build(iterator(), getDefaultResolver()).hasContext(parameterDescriptor);
	}

	@Override
	public Collection<BeanDefinition> resolveBeanDefinitions(Class<?> clazz) {
		return ContextResolverChain.build(iterator(), getDefaultResolver()).resolveBeanDefinitions(clazz);
	}

	@Override
	public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory)
			throws IOException {
		boolean match = ContextResolverChain.build(iterator(), getDefaultResolver()).match(metadataReader,
				metadataReaderFactory);
		if (match) {
			return JavaVersion.isSupported(metadataReader.getAnnotationMetadata());
		}
		return match;
	}
}
