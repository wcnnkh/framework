package io.basc.framework.context;

import java.io.IOException;
import java.util.Collection;

import io.basc.framework.core.type.classreading.MetadataReader;
import io.basc.framework.core.type.classreading.MetadataReaderFactory;
import io.basc.framework.factory.BeanDefinition;
import io.basc.framework.mapper.ParameterDescriptor;

public interface ContextResolverExtend {
	default ProviderDefinition getProviderDefinition(Class<?> clazz, ContextResolver chain) {
		return chain.getProviderDefinition(clazz);
	}

	default boolean hasContext(ParameterDescriptor parameterDescriptor, ContextResolver chain) {
		return chain.hasContext(parameterDescriptor);
	}

	default Collection<BeanDefinition> resolveBeanDefinitions(Class<?> clazz, ContextResolver chain) {
		return chain.resolveBeanDefinitions(clazz);
	}

	default boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory,
			ContextResolver chain) throws IOException {
		return chain.match(metadataReader, metadataReaderFactory);
	}
}
