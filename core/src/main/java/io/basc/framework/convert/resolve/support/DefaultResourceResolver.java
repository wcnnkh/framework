package io.basc.framework.convert.resolve.support;

import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.ConversionServiceAware;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.convert.resolve.ConfigurableResourceResolver;
import io.basc.framework.convert.resolve.ResourceResolver;
import io.basc.framework.factory.Configurable;
import io.basc.framework.factory.ConfigurableServices;
import io.basc.framework.factory.ServiceLoaderFactory;
import io.basc.framework.io.Resource;
import io.basc.framework.io.resolver.PropertiesResolver;
import io.basc.framework.lang.NotSupportedException;

import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.function.Supplier;

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
