package run.soeasy.framework.core.convert.resource;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;

import lombok.NonNull;
import run.soeasy.framework.core.convert.ConditionalConversionService;
import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.ConversionFailedException;
import run.soeasy.framework.core.convert.ConvertiblePair;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.convert.value.ValueAccessor;
import run.soeasy.framework.core.io.Resource;

public class ResourceResolverConversionService implements ConditionalConversionService {
	private final ResourceResolver resourceResolver;

	public ResourceResolverConversionService(ResourceResolver resourceResolver) {
		this.resourceResolver = resourceResolver;
	}

	public Set<ConvertiblePair> getConvertibleTypes() {
		return Collections.singleton(new ConvertiblePair(Resource.class, Object.class));
	}

	@Override
	public Object convert(@NonNull ValueAccessor value, @NonNull TypeDescriptor targetType) throws ConversionException {
		try {
			return resourceResolver.resolveResource((Resource) value.get(), targetType);
		} catch (IOException e) {
			throw new ConversionFailedException(value, targetType, e);
		}
	}

}
