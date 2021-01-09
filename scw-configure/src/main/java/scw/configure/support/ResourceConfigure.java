package scw.configure.support;

import java.util.Collections;
import java.util.Set;

import scw.configure.Configure;
import scw.configure.resolver.ResourceResolver;
import scw.convert.TypeDescriptor;
import scw.convert.support.ConvertiblePair;
import scw.io.Resource;

public class ResourceConfigure extends ConditionalConfigure {
	private final ResourceResolver resourceResolver;
	private final Configure configure;

	public ResourceConfigure(ResourceResolver resourceResolver,
			Configure configure) {
		this.resourceResolver = resourceResolver;
		this.configure = configure;
	}

	public Set<ConvertiblePair> getConvertibleTypes() {
		return Collections.singleton(new ConvertiblePair(Resource.class,
				Object.class));
	}

	public void configuration(Object source, TypeDescriptor sourceType,
			Object target, TypeDescriptor targetType) {
		Resource resource = (Resource) source;
		if (!resource.exists()) {
			return;
		}

		Object sourceToUse = resourceResolver.resolve(resource,
				TypeDescriptor.valueOf(Object.class));
		configure.configuration(sourceToUse,
				TypeDescriptor.forObject(sourceType), target, targetType);
	}
}
