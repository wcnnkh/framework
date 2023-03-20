package io.basc.framework.context;

import java.io.IOException;
import java.util.Collection;

import io.basc.framework.core.type.AnnotationMetadata;
import io.basc.framework.core.type.ClassMetadata;
import io.basc.framework.core.type.classreading.MetadataReader;
import io.basc.framework.core.type.classreading.MetadataReaderFactory;
import io.basc.framework.core.type.filter.TypeFilter;
import io.basc.framework.factory.BeanDefinition;
import io.basc.framework.factory.ConfigurableServices;
import io.basc.framework.lang.Ignore;
import io.basc.framework.mapper.ParameterDescriptor;
import io.basc.framework.util.JavaVersion;

public class ConfigurableContextResolver extends ConfigurableServices<ContextResolverExtend>
		implements ContextResolver {
	public static final TypeFilter DEFAULT_TYPE_FILTER = (metadataReader, factory) -> {
		ClassMetadata classMetadata = metadataReader.getClassMetadata();
		if (classMetadata.isEnum() || classMetadata.isAnnotation()) {
			return false;
		}

		AnnotationMetadata annotationMetadata = metadataReader.getAnnotationMetadata();
		if (annotationMetadata.hasAnnotation(Ignore.class.getName())) {
			return false;
		}
		return classMetadata.isPublic();
	};

	private ContextResolver defaultResolver;
	private TypeFilter typeFilter = DEFAULT_TYPE_FILTER;

	public ConfigurableContextResolver() {
		super(ContextResolverExtend.class);
	}

	public ContextResolver getDefaultResolver() {
		return defaultResolver;
	}

	public void setDefaultResolver(ContextResolver defaultResolver) {
		this.defaultResolver = defaultResolver;
	}

	public TypeFilter getTypeFilter() {
		return typeFilter;
	}

	public void setTypeFilter(TypeFilter typeFilter) {
		this.typeFilter = typeFilter;
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
		return (typeFilter == null || typeFilter.match(metadataReader, metadataReaderFactory))
				&& JavaVersion.isSupported(metadataReader.getAnnotationMetadata()) && ContextResolverChain
						.build(iterator(), getDefaultResolver()).match(metadataReader, metadataReaderFactory);
	}
}
