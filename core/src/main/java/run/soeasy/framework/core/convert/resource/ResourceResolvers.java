package run.soeasy.framework.core.convert.resource;

import java.io.IOException;
import java.util.Properties;

import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.ConversionService;
import run.soeasy.framework.core.convert.ConversionServiceAware;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.util.exchange.Receipt;
import run.soeasy.framework.util.exchange.Registration;
import run.soeasy.framework.util.io.Resource;
import run.soeasy.framework.util.io.resolver.ConfigurablePropertiesResolver;
import run.soeasy.framework.util.spi.ConfigurableServices;
import run.soeasy.framework.util.spi.ProviderFactory;

public class ResourceResolvers extends ConfigurableServices<ResourceResolver> implements ResourceResolver {
	private static final TypeDescriptor PROPERTIES_TYPE = TypeDescriptor.valueOf(Properties.class);
	private final ConfigurablePropertiesResolver propertiesResolvers;
	private final ConversionService conversionService;

	public ResourceResolvers(ConversionService conversionService) {
		this(new ConfigurablePropertiesResolver(), conversionService);
	}

	public ResourceResolvers(ConfigurablePropertiesResolver propertiesResolvers, ConversionService conversionService) {
		setServiceClass(ResourceResolver.class);
		this.propertiesResolvers = propertiesResolvers;
		this.conversionService = conversionService;
		getInjectors().register((service) -> {
			if (service instanceof ConversionServiceAware) {
				((ConversionServiceAware) service).setConversionService(getConversionService());
			}
			return Registration.SUCCESS;
		});
	}

	@Override
	public Receipt configure(ProviderFactory discovery) {
		propertiesResolvers.configure(discovery);
		return super.configure(discovery);
	}

	public ConfigurablePropertiesResolver getPropertiesResolvers() {
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

	public Object resolveResource(Resource resource, TypeDescriptor targetType) throws IOException {
		for (ResourceResolver resolver : this) {
			if (resolver.canResolveResource(resource, targetType)) {
				return resolver.resolveResource(resource, targetType);
			}
		}

		if (getPropertiesResolvers().canResolveProperties(resource)) {
			Properties properties = new Properties();
			try {
				getPropertiesResolvers().resolveProperties(properties, resource);
			} catch (IOException e) {
				throw new ConversionException(resource.getDescription(), e);
			}
			return getConversionService().convert(properties, PROPERTIES_TYPE, targetType);
		}

		return conversionService.convert(resource, TypeDescriptor.forObject(resource), targetType);
	}
}
