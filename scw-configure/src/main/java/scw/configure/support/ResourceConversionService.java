package scw.configure.support;

import java.io.IOException;

import scw.configure.resolver.ResourceResolver;
import scw.convert.TypeDescriptor;
import scw.convert.support.AbstractConversionService;
import scw.io.Resource;
import scw.logger.Logger;
import scw.logger.LoggerUtils;

public class ResourceConversionService extends AbstractConversionService {
	private static Logger logger = LoggerUtils
			.getLogger(ResourceConversionService.class);
	private final ResourceResolver resourceResolver;

	public ResourceConversionService(ResourceResolver resourceResolver) {
		this.resourceResolver = resourceResolver;
	}

	public boolean canConvert(Class<?> sourceType, Class<?> targetType) {
		if (Resource.class.isAssignableFrom(sourceType)) {
			return true;
		}
		return false;
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
		try {
			if (resourceResolver.matches(resource, targetType)) {
				sourceToUse = resourceResolver.resolve(resource, targetType);
			} else {
				logger.error("{} not support convert to {}", resource,
						targetType);
			}
		} catch (IOException e) {
			logger.error(e, resource);
		}
		return sourceToUse;
	}
}
