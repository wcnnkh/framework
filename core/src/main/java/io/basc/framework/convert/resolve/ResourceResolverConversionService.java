package io.basc.framework.convert.resolve;

import io.basc.framework.convert.ConversionException;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.convert.lang.ConditionalConversionService;
import io.basc.framework.convert.lang.ConvertiblePair;
import io.basc.framework.io.Resource;

import java.util.Collections;
import java.util.Set;

public class ResourceResolverConversionService extends ConditionalConversionService {
	private final ResourceResolver resourceResolver;

	public ResourceResolverConversionService(ResourceResolver resourceResolver) {
		this.resourceResolver = resourceResolver;
	}

	@SuppressWarnings("unchecked")
	public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws ConversionException {
		return resourceResolver.resolveResource((Resource) source, targetType);
	}

	public Set<ConvertiblePair> getConvertibleTypes() {
		return Collections.singleton(new ConvertiblePair(Resource.class, Object.class));
	}

}
