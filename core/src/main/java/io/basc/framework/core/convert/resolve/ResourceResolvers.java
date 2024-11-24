package io.basc.framework.core.convert.resolve;

import java.nio.charset.Charset;
import java.util.Properties;
import java.util.function.Supplier;

import io.basc.framework.core.convert.ConversionService;
import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.config.ConversionServiceAware;
import io.basc.framework.io.Resource;
import io.basc.framework.io.resolver.PropertiesResolvers;
import io.basc.framework.util.Receipt;
import io.basc.framework.util.Registration;
import io.basc.framework.util.function.StaticSupplier;
import io.basc.framework.util.spi.ConfigurableServices;
import io.basc.framework.util.spi.ServiceLoaderDiscovery;

public class ResourceResolvers extends ConfigurableServices<ResourceResolver> implements ResourceResolver {
	private static final TypeDescriptor PROPERTIES_TYPE = TypeDescriptor.valueOf(Properties.class);
	private final PropertiesResolvers propertiesResolvers;
	private final Supplier<Charset> charset;
	private final ConversionService conversionService;

	public ResourceResolvers(ConversionService conversionService) {
		this(conversionService, (Charset) null);
	}

	public ResourceResolvers(ConversionService conversionService, Charset charset) {
		this(conversionService, charset == null ? null : new StaticSupplier<>(charset));
	}

	public ResourceResolvers(ConversionService conversionService, Supplier<Charset> charset) {
		this(new PropertiesResolvers(), conversionService, charset);
	}

	public ResourceResolvers(PropertiesResolvers propertiesResolvers, ConversionService conversionService,
			Supplier<Charset> charset) {
		setServiceClass(ResourceResolver.class);
		this.propertiesResolvers = propertiesResolvers;
		this.conversionService = conversionService;
		this.charset = charset;
		getInjectors().register((service) -> {
			if (service instanceof ConversionServiceAware) {
				((ConversionServiceAware) service).setConversionService(getConversionService());
			}
			return Registration.SUCCESS;
		});
	}
	
	@Override
	public Receipt doConfigure(ServiceLoaderDiscovery discovery) {
		propertiesResolvers.doConfigure(discovery);
		return super.doConfigure(discovery);
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

		if (getPropertiesResolvers().canResolveProperties(resource)) {
			Properties properties = new Properties();
			getPropertiesResolvers().resolveProperties(properties, resource, charset == null ? null : charset.get());
			return getConversionService().convert(properties, PROPERTIES_TYPE, targetType);
		}

		return conversionService.convert(resource, TypeDescriptor.forObject(resource), targetType);
	}
}
