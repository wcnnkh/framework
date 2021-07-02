package scw.convert.resolve.support;

import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.function.Supplier;

import scw.convert.ConversionService;
import scw.convert.ConversionServiceAware;
import scw.convert.TypeDescriptor;
import scw.convert.resolve.ConfigurableResourceResolver;
import scw.convert.resolve.ResourceResolver;
import scw.instance.ConfigurableServices;
import scw.instance.ServiceLoaderFactory;
import scw.instance.Configurable;
import scw.io.Resource;
import scw.io.resolver.PropertiesResolver;
import scw.lang.NotSupportedException;

public class DefaultResourceResolver extends PropertiesResourceResolver
		implements ConfigurableResourceResolver, Configurable {
	private ConfigurableServices<ResourceResolver> resourceResolvers = new ConfigurableServices<>(
			ResourceResolver.class, (s) -> aware(s));

	public DefaultResourceResolver(ConversionService conversionService, PropertiesResolver propertiesResolver,
			Supplier<Charset> charset) {
		super(conversionService, propertiesResolver, charset);
	}

	@Override
	public Iterator<ResourceResolver> iterator() {
		return resourceResolvers.iterator();
	}

	protected void aware(ResourceResolver resourceResolver) {
		if (resourceResolver instanceof ConversionServiceAware) {
			((ConversionServiceAware) resourceResolver).setConversionService(getConversionService());
		}
	}

	public void addResourceResolver(ResourceResolver resourceResolver) {
		if (resourceResolver == null) {
			return;
		}

		resourceResolvers.addService(resourceResolver);
	}

	public boolean canResolveResource(Resource resource, TypeDescriptor targetType) {
		for (ResourceResolver resolver : this) {
			if (resolver.canResolveResource(resource, targetType)) {
				return true;
			}
		}
		return super.canResolveResource(resource, targetType);
	}

	public Object resolveResource(Resource resource, TypeDescriptor targetType) {
		for (ResourceResolver resolver : this) {
			if (resolver.canResolveResource(resource, targetType)) {
				return resolver.resolveResource(resource, targetType);
			}
		}

		if (super.canResolveResource(resource, targetType)) {
			return super.resolveResource(resource, targetType);
		}
		throw new NotSupportedException(resource.getDescription());
	}

	@Override
	public void configure(ServiceLoaderFactory serviceLoaderFactory) {
		resourceResolvers.configure(serviceLoaderFactory);
	}
}
