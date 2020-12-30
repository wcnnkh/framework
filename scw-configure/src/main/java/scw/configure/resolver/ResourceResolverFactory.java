package scw.configure.resolver;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;

import scw.convert.ConversionService;
import scw.convert.TypeDescriptor;
import scw.core.instance.InstanceUtils;
import scw.core.utils.ClassUtils;
import scw.io.Resource;
import scw.lang.NotSupportedException;
import scw.lang.Nullable;
import scw.util.comparator.ComparableComparator;

public class ResourceResolverFactory implements ResourceResolver {
	private static final String YAML_RESOLVER_NAME = "scw.configure.resolver.YamlResourceResolver";
	
	private final TreeSet<ResourceResolver> resourceResolvers = new TreeSet<ResourceResolver>(
			ComparableComparator.INSTANCE);
	
	public ResourceResolverFactory(){
	}

	public ResourceResolverFactory(ConversionService conversionService, String charsetName) {
		resourceResolvers.add(new PropertiesResourceResolver(conversionService, charsetName));
		resourceResolvers.add(new XmlResourceResolver(conversionService));
		if(ClassUtils.isPresent(YAML_RESOLVER_NAME)){
			resourceResolvers.add((ResourceResolver)InstanceUtils.INSTANCE_FACTORY.getInstance(YAML_RESOLVER_NAME, conversionService));
		}
	}
	
	public SortedSet<ResourceResolver> getResourceResolvers() {
		return Collections.synchronizedSortedSet(resourceResolvers);
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

	public Object resolve(Resource resource, TypeDescriptor targetType)
			throws IOException {
		for (ResourceResolver resolver : resourceResolvers) {
			if (resolver.matches(resource, targetType)) {
				return resolver.resolve(resource, targetType);
			}
		}
		throw new NotSupportedException(resource.getDescription());
	}
	
	@SuppressWarnings("unchecked")
	public <T> T resolve(Resource resource, Class<T> targetType)
			throws IOException {
		return (T) resolve(resource, TypeDescriptor.valueOf(targetType));
	}
	
	public Object resolve(Resource resource, Type targetType)
			throws IOException {
		return resolve(resource, TypeDescriptor.valueOf(targetType));
	}
}
