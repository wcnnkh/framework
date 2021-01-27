package scw.convert.resolve;

import scw.convert.TypeDescriptor;
import scw.io.Resource;

public interface ResourceResolver {
	boolean canResolveResource(Resource resource, TypeDescriptor targetType);
	
	Object resolveResource(Resource resource, TypeDescriptor targetType);
}
