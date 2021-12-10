package io.basc.framework.convert.resolve;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.io.Resource;

public interface ResourceResolver {
	static final TypeDescriptor RESOURCE_TYPE = TypeDescriptor.valueOf(Resource.class);

	boolean canResolveResource(Resource resource, TypeDescriptor targetType);

	Object resolveResource(Resource resource, TypeDescriptor targetType);
}
