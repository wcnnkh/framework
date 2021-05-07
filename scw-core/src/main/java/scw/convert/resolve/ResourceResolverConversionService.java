package scw.convert.resolve;

import java.util.Collections;
import java.util.Set;

import scw.convert.ConversionException;
import scw.convert.TypeDescriptor;
import scw.convert.lang.ConditionalConversionService;
import scw.convert.lang.ConvertiblePair;
import scw.io.Resource;

public class ResourceResolverConversionService extends ConditionalConversionService{
	private final ResourceResolver resourceResolver;
	
	public ResourceResolverConversionService(ResourceResolver resourceResolver){
		this.resourceResolver = resourceResolver;
	}
	
	public Object convert(Object source, TypeDescriptor sourceType,
			TypeDescriptor targetType) throws ConversionException {
		return resourceResolver.resolveResource((Resource)source, targetType);
	}
	
	public Set<ConvertiblePair> getConvertibleTypes() {
		return Collections.singleton(new ConvertiblePair(Resource.class, Object.class));
	}

}
