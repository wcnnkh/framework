package scw.convert.resolve.support;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;

import scw.convert.ConversionService;
import scw.convert.ConversionServiceAware;
import scw.convert.TypeDescriptor;
import scw.convert.resolve.ConfigurableResourceResolver;
import scw.convert.resolve.ResourceResolver;
import scw.core.OrderComparator;
import scw.core.utils.CollectionUtils;
import scw.io.Resource;
import scw.io.resolver.PropertiesResolver;
import scw.lang.NotSupportedException;

public class DefaultResourceResolver extends PropertiesResourceResolver
		implements ConfigurableResourceResolver {
	protected List<ResourceResolver> resourceResolvers;
	private final ConversionService conversionService;

	public DefaultResourceResolver(ConversionService conversionService,
			PropertiesResolver propertiesResolver, Supplier<Charset> charset) {
		super(conversionService, propertiesResolver, charset);
		this.conversionService = conversionService;
		addResourceResolver(new DocumentResourceResolver(conversionService));
	}

	@Override
	public Iterator<ResourceResolver> iterator() {
		if (resourceResolvers == null) {
			return Collections.emptyIterator();
		}
		return CollectionUtils.getIterator(resourceResolvers, true);
	}

	public void addResourceResolver(ResourceResolver resourceResolver) {
		if(resourceResolver == null){
			return ;
		}
		
		synchronized (this) {
			if (resourceResolvers == null) {
				resourceResolvers = new ArrayList<ResourceResolver>(8);
			}

			if (resourceResolver instanceof ConversionServiceAware) {
				((ConversionServiceAware) resourceResolver)
						.setConversionService(conversionService);
			}
			
			resourceResolvers.add(resourceResolver);
			Collections.sort(resourceResolvers,
					OrderComparator.INSTANCE.reversed());
		}
	}

	public boolean canResolveResource(Resource resource,
			TypeDescriptor targetType) {
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
}
