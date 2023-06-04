package io.basc.framework.context;

import java.io.IOException;
import java.lang.reflect.Executable;

import io.basc.framework.beans.factory.config.BeanDefinition;
import io.basc.framework.core.type.classreading.MetadataReader;
import io.basc.framework.core.type.classreading.MetadataReaderFactory;
import io.basc.framework.mapper.ParameterDescriptor;

public interface ContextResolverExtend {
	default ProviderDefinition getProviderDefinition(Class<?> clazz, ContextResolver chain) {
		return chain.getProviderDefinition(clazz);
	}

	default boolean hasContext(ParameterDescriptor parameterDescriptor, ContextResolver chain) {
		return chain.hasContext(parameterDescriptor);
	}

	default boolean canResolveExecutable(Class<?> sourceClass, ContextResolver chain) {
		return chain.canResolveExecutable(sourceClass);
	}

	default boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory,
			ContextResolver chain) throws IOException {
		return chain.match(metadataReader, metadataReaderFactory);
	}

	default BeanDefinition resolveBeanDefinition(Class<?> sourceClass, Executable executable, ContextResolver chain) {
		return chain.resolveBeanDefinition(sourceClass, executable);
	}

	default BeanDefinition resolveBeanDefinition(Class<?> sourceClass, ContextResolver chain) {
		return chain.resolveBeanDefinition(sourceClass);
	}
}
