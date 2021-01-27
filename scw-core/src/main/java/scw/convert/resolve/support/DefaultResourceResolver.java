package scw.convert.resolve.support;

import java.nio.charset.Charset;
import java.util.TreeSet;

import scw.convert.ConversionService;
import scw.convert.ConversionServiceAware;
import scw.convert.TypeDescriptor;
import scw.convert.resolve.ConfigurableResourceResolver;
import scw.convert.resolve.ResourceResolver;
import scw.io.Resource;
import scw.io.resolver.PropertiesResolver;
import scw.lang.NotSupportedException;
import scw.util.Supplier;
import scw.util.comparator.ComparableComparator;

public class DefaultResourceResolver extends PropertiesResourceResolver implements ConfigurableResourceResolver{
	protected final TreeSet<ResourceResolver> resourceResolvers = new TreeSet<ResourceResolver>(
			ComparableComparator.INSTANCE);
	private final ConversionService conversionService;
	
	public DefaultResourceResolver(ConversionService conversionService, PropertiesResolver propertiesResolver, Supplier<Charset> charset){
		super(conversionService, propertiesResolver, charset);
		this.conversionService = conversionService;
		resourceResolvers.add(new DocumentResourceResolver(conversionService));
	}

	public boolean canResolveResource(Resource resource,
			TypeDescriptor targetType) {
		for(ResourceResolver resolver : resourceResolvers){
			if(resolver.canResolveResource(resource, targetType)){
				return true;
			}
		}
		return super.canResolveResource(resource, targetType);
	}

	public Object resolveResource(Resource resource, TypeDescriptor targetType) {
		for(ResourceResolver resolver : resourceResolvers){
			if(resolver.canResolveResource(resource, targetType)){
				return resolver.resolveResource(resource, targetType);
			}
		}
		
		if(super.canResolveResource(resource, targetType)){
			return super.resolveResource(resource, targetType);
		}
		throw new NotSupportedException(resource.getDescription());
	}

	public void addResourceResolver(ResourceResolver resourceResolver) {
		if(resourceResolver instanceof ConversionServiceAware){
			((ConversionServiceAware) resourceResolver).setConversionService(conversionService);
		}
		resourceResolvers.add(resourceResolver);
	}
}
