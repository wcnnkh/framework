package io.basc.framework.context;

import java.io.IOException;
import java.lang.reflect.Executable;

import io.basc.framework.core.type.classreading.MetadataReader;
import io.basc.framework.core.type.classreading.MetadataReaderFactory;
import io.basc.framework.factory.BeanDefinition;
import io.basc.framework.mapper.ParameterDescriptor;

public class ContextResolverConfiguration implements ContextResolver {

	@Override
	public ProviderDefinition getProviderDefinition(Class<?> clazz) {
		return null;
	}

	@Override
	public boolean hasContext(ParameterDescriptor parameterDescriptor) {
		return false;
	}

	@Override
	public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory)
			throws IOException {
		return false;
	}

	@Override
	public BeanDefinition resolveBeanDefinition(Class<?> sourceClass) {
		return null;
	}

	@Override
	public BeanDefinition resolveBeanDefinition(Class<?> sourceClass, Executable executable) {
		return null;
	}

	@Override
	public boolean canResolveExecutable(Class<?> sourceClass) {
		return false;
	}
}
