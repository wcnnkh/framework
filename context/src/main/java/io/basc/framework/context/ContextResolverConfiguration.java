package io.basc.framework.context;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

import io.basc.framework.core.type.classreading.MetadataReader;
import io.basc.framework.core.type.classreading.MetadataReaderFactory;
import io.basc.framework.factory.BeanDefinition;
import io.basc.framework.mapper.ParameterDescriptor;

public class ContextResolverConfiguration implements ContextResolver {
	private boolean matchAll;

	@Override
	public ProviderDefinition getProviderDefinition(Class<?> clazz) {
		return null;
	}

	@Override
	public boolean hasContext(ParameterDescriptor parameterDescriptor) {
		return false;
	}

	@Override
	public Collection<BeanDefinition> resolveBeanDefinitions(Class<?> clazz) {
		return Collections.emptySet();
	}

	@Override
	public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory)
			throws IOException {
		return matchAll;
	}

	public boolean isMatchAll() {
		return matchAll;
	}

	public void setMatchAll(boolean matchAll) {
		this.matchAll = matchAll;
	}
}
