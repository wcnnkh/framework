package io.basc.framework.convert.resolve;

import java.nio.charset.Charset;
import java.util.Properties;
import java.util.function.Supplier;

import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.ConversionServiceAware;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.factory.ConfigurableServices;
import io.basc.framework.factory.ServiceLoaderFactory;
import io.basc.framework.io.Resource;
import io.basc.framework.io.resolver.PropertiesResolvers;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.StaticSupplier;

public class ResourceResolvers extends ConfigurableServices<ResourceResolver> implements ResourceResolver {
	private static final TypeDescriptor PROPERTIES_TYPE = TypeDescriptor.valueOf(Properties.class);
	private final PropertiesResolvers propertiesResolvers;
	private final Supplier<Charset> charset;
	private final ConversionService conversionService;

	public ResourceResolvers(ConversionService conversionService) {
		this(conversionService, (Charset) null);
	}

	public ResourceResolvers(ConversionService conversionService, @Nullable Charset charset) {
		this(conversionService, charset == null ? null : new StaticSupplier<>(charset));
	}

	public ResourceResolvers(ConversionService conversionService, @Nullable Supplier<Charset> charset) {
		this(new PropertiesResolvers(), conversionService, charset);
	}

	public ResourceResolvers(PropertiesResolvers propertiesResolvers, ConversionService conversionService,
			@Nullable Supplier<Charset> charset) {
		super(ResourceResolver.class);
		this.propertiesResolvers = propertiesResolvers;
		this.conversionService = conversionService;
		this.charset = charset;
	}

	@Override
	protected void aware(ResourceResolver service) {
		if (service instanceof ConversionServiceAware) {
			((ConversionServiceAware) service).setConversionService(getConversionService());
		}
		super.aware(service);
	}

	@Override
	public void configure(ServiceLoaderFactory serviceLoaderFactory) {
		propertiesResolvers.configure(serviceLoaderFactory);
		super.configure(serviceLoaderFactory);
	}

	public PropertiesResolvers getPropertiesResolvers() {
		return propertiesResolvers;
	}

	public ConversionService getConversionService() {
		return conversionService;
	}

	public boolean canResolveResource(Resource resource, TypeDescriptor targetType) {
		for (ResourceResolver resolver : this) {
			if (resolver.canResolveResource(resource, targetType)) {
				return true;
			}
		}
		return getPropertiesResolvers().canResolveProperties(resource)
				&& getConversionService().canConvert(PROPERTIES_TYPE, targetType);
	}

	public Object resolveResource(Resource resource, TypeDescriptor targetType) {
		for (ResourceResolver resolver : this) {
			if (resolver.canResolveResource(resource, targetType)) {
				return resolver.resolveResource(resource, targetType);
			}
		}

		Properties properties = new Properties();
		getPropertiesResolvers().resolveProperties(properties, resource, charset == null ? null : charset.get());
		return getConversionService().convert(properties, PROPERTIES_TYPE, targetType);
	}
}
