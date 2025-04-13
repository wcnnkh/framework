package run.soeasy.framework.core.convert.resource;

import java.io.IOException;

import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.io.Resource;

public interface ResourceResolver {
	static final TypeDescriptor RESOURCE_TYPE = TypeDescriptor.valueOf(Resource.class);

	boolean canResolveResource(Resource resource, TypeDescriptor targetType);

	Object resolveResource(Resource resource, TypeDescriptor targetType) throws IOException;
}
