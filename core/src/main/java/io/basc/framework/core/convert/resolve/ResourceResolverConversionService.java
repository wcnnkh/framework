package io.basc.framework.core.convert.resolve;

import java.util.Collections;
import java.util.Set;

import io.basc.framework.core.convert.ConversionException;
import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.Value;
import io.basc.framework.core.convert.service.ConditionalConversionService;
import io.basc.framework.core.convert.service.ConvertiblePair;
import io.basc.framework.util.io.Resource;
import lombok.NonNull;

public class ResourceResolverConversionService implements ConditionalConversionService {
	private final ResourceResolver resourceResolver;

	public ResourceResolverConversionService(ResourceResolver resourceResolver) {
		this.resourceResolver = resourceResolver;
	}

	public Set<ConvertiblePair> getConvertibleTypes() {
		return Collections.singleton(new ConvertiblePair(Resource.class, Object.class));
	}

	@Override
	public Object convert(@NonNull Value value, @NonNull TypeDescriptor targetType) throws ConversionException {
		return resourceResolver.resolveResource((Resource) value.get(), targetType);
	}

}
