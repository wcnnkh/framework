package scw.configure.resolver;

import java.lang.reflect.Type;
import java.util.SortedSet;
import java.util.TreeSet;

import scw.convert.TypeDescriptor;
import scw.io.Resource;
import scw.lang.NotSupportedException;
import scw.lang.Nullable;
import scw.util.XUtils;
import scw.util.comparator.ComparableComparator;

public class ResourceResolverFactory implements ResourceResolver {
	
	protected final TreeSet<ResourceResolver> resourceResolvers = new TreeSet<ResourceResolver>(
			ComparableComparator.INSTANCE);
	
	public SortedSet<ResourceResolver> getResourceResolvers() {
		return XUtils.synchronizedProxy(resourceResolvers, this);
	}

	@Nullable
	public ResourceResolver getResourceResolver(Resource resource, TypeDescriptor targetType) {
		for (ResourceResolver resolver : resourceResolvers) {
			if (resolver.matches(resource, targetType)) {
				return resolver;
			}
		}
		return null;
	}

	public boolean matches(Resource resource, TypeDescriptor targetType) {
		for (ResourceResolver resolver : resourceResolvers) {
			if (resolver.matches(resource, targetType)) {
				return true;
			}
		}
		return false;
	}

	public Object resolve(Resource resource, TypeDescriptor targetType) {
		for (ResourceResolver resolver : resourceResolvers) {
			if (resolver.matches(resource, targetType)) {
				return resolver.resolve(resource, targetType);
			}
		}
		throw new NotSupportedException(resource.getDescription());
	}
	
	public final Object resolve(Resource resource, Type targetType){
		return resolve(resource, TypeDescriptor.valueOf(targetType));
	}
	
	@SuppressWarnings("unchecked")
	public final <T> T resolve(Resource resource, Class<T> targetType) {
		return (T) resolve(resource, TypeDescriptor.valueOf(targetType));
	}
}
