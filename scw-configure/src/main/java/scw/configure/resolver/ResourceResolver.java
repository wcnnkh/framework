package scw.configure.resolver;

import scw.convert.TypeDescriptor;
import scw.io.Resource;

public interface ResourceResolver {
	boolean matches(Resource resource, TypeDescriptor targetType);

	Object resolve(Resource resource, TypeDescriptor targetType);
}