package scw.configure.convert;

import java.util.Collections;
import java.util.Set;

import scw.configure.resolver.ResourceResolver;
import scw.convert.TypeDescriptor;
import scw.convert.support.ConditionalConversionService;
import scw.convert.support.ConvertiblePair;
import scw.io.Resource;
import scw.logger.Logger;
import scw.logger.LoggerUtils;

public class ResourceConversionService extends ConditionalConversionService {
	private static Logger logger = LoggerUtils
			.getLogger(ResourceConversionService.class);
	private final ResourceResolver resourceResolver;

	public ResourceConversionService(ResourceResolver resourceResolver) {
		this.resourceResolver = resourceResolver;
	}

	public Set<ConvertiblePair> getConvertibleTypes() {
		return Collections.singleton(new ConvertiblePair(Resource.class,
				Object.class));
	}

	public Object convert(Object source, TypeDescriptor sourceType,
			TypeDescriptor targetType) {
		if (source == null) {
			return null;
		}

		Resource resource = (Resource) source;
		if (!resource.exists()) {
			return null;
		}

		Object sourceToUse = null;
		if (resourceResolver.matches(resource, targetType)) {
			sourceToUse = resourceResolver.resolve(resource, targetType);
		} else {
			logger.error("{} not support convert to {}", resource, targetType);
		}
		return sourceToUse;
	}
}
