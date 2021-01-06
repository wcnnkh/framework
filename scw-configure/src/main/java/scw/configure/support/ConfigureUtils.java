package scw.configure.support;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import scw.configure.Configure;
import scw.configure.convert.CollectionToMapConversionService;
import scw.configure.convert.ConfigureConversionServiceFactory;
import scw.configure.convert.PrimaryKeyGetter;
import scw.configure.convert.PrimaryKeyGetterFactory;
import scw.configure.resolver.PropertiesResourceResolver;
import scw.configure.resolver.ResourceResolver;
import scw.configure.resolver.ResourceResolverFactory;
import scw.configure.resolver.XmlResourceResolver;
import scw.convert.ConversionService;
import scw.convert.TypeDescriptor;
import scw.convert.support.ConversionServiceFactory;
import scw.core.Constants;
import scw.core.instance.InstanceUtils;
import scw.core.utils.ClassUtils;
import scw.io.Resource;
import scw.io.ResourceUtils;
import scw.mapper.MapperUtils;

public final class ConfigureUtils {
	private static final ConfigureFactory CONFIGURE_FACTORY = new ConfigureFactory();
	private static final ResourceResolverFactory RESOURCE_RESOLVER_FACTORY = new ResourceResolverFactory();
	private static final ConversionServiceFactory CONVERSION_SERVICE_FACTORY = new ConfigureConversionServiceFactory(CONFIGURE_FACTORY, RESOURCE_RESOLVER_FACTORY, InstanceUtils.INSTANCE_FACTORY);
	private static final PrimaryKeyGetterFactory PRIMARY_KEY_GETTER_FACTORY = new PrimaryKeyGetterFactory();
	
	static {
		PRIMARY_KEY_GETTER_FACTORY.getPrimaryKeyGetters().addAll(InstanceUtils.loadAllService(PrimaryKeyGetter.class));
		PRIMARY_KEY_GETTER_FACTORY.getPrimaryKeyGetters().add(PrimaryKeyGetter.ANNOTATION);
		PRIMARY_KEY_GETTER_FACTORY.getPrimaryKeyGetters().add(PrimaryKeyGetter.FIRST_FIELD);
		
		CONFIGURE_FACTORY.getConfigures().add(new MapConfigure(CONVERSION_SERVICE_FACTORY));
		CONFIGURE_FACTORY.getConfigures().add(new PropertyFactoryConfigure(CONVERSION_SERVICE_FACTORY));
		CONFIGURE_FACTORY.getConfigures().add(new ResourceConfigure(RESOURCE_RESOLVER_FACTORY, CONFIGURE_FACTORY));

		RESOURCE_RESOLVER_FACTORY.getResourceResolvers().add(new PropertiesResourceResolver(CONVERSION_SERVICE_FACTORY, Constants.UTF_8_NAME));
		RESOURCE_RESOLVER_FACTORY.getResourceResolvers().add(new XmlResourceResolver(CONVERSION_SERVICE_FACTORY));
		
		String yamlResolverName = "scw.configure.resolver.YamlResourceResolver";
		if(ClassUtils.isPresent(yamlResolverName)){
			RESOURCE_RESOLVER_FACTORY.getResourceResolvers().add((ResourceResolver)InstanceUtils.INSTANCE_FACTORY.getInstance(yamlResolverName, CONVERSION_SERVICE_FACTORY));
		}
		
		CONVERSION_SERVICE_FACTORY.getConversionServices().addAll(
				InstanceUtils.loadAllService(ConversionService.class));
		CONFIGURE_FACTORY.getConfigures().addAll(
				InstanceUtils.loadAllService(Configure.class));
		RESOURCE_RESOLVER_FACTORY.getResourceResolvers().addAll(
				InstanceUtils.loadAllService(ResourceResolver.class));
	}

	private ConfigureUtils() {
	};
	
	public static ConversionServiceFactory getConversionServiceFactory() {
		return CONVERSION_SERVICE_FACTORY;
	}

	public static ConfigureFactory getConfigureFactory() {
		return CONFIGURE_FACTORY;
	}

	public static ResourceResolverFactory getResourceResolverFactory() {
		return RESOURCE_RESOLVER_FACTORY;
	}

	public static PrimaryKeyGetterFactory getPrimaryKeyGetterFactory() {
		return PRIMARY_KEY_GETTER_FACTORY;
	}

	@SuppressWarnings("unchecked")
	public static <K, V> Map<K, V> resolveToMap(Resource resource, Class<?> mapType, TypeDescriptor keyType, TypeDescriptor valueType, PrimaryKeyGetter primaryKeyGetter){
		CollectionToMapConversionService service = new CollectionToMapConversionService(getConversionServiceFactory(), primaryKeyGetter);
		return (Map<K, V>) service.convert(resource, TypeDescriptor.forObject(resource), TypeDescriptor.map(mapType, keyType, valueType));
	}
	
	public static <K, V> Map<K, V> resolveToMap(Resource resource, Class<K> keyType, Class<V> valueType){
		return resolveToMap(resource, Map.class, TypeDescriptor.valueOf(keyType), TypeDescriptor.valueOf(valueType), getPrimaryKeyGetterFactory());
	}
	
	public static <K, V> Map<K, V> resolveToMap(String resource, Class<K> keyType, Class<V> valueType){
		return resolveToMap(ResourceUtils.getResourceOperations().getResource(resource), Map.class, TypeDescriptor.valueOf(keyType), TypeDescriptor.valueOf(valueType), getPrimaryKeyGetterFactory());
	}
	
	@SuppressWarnings("unchecked")
	public static <E> Collection<E> resolveToCollection(Resource resource, Class<?> collectionType, TypeDescriptor elementType){
		return (Collection<E>) getResourceResolverFactory().resolve(resource, TypeDescriptor.collection(collectionType, elementType));
	}
	
	public static <E> Collection<E> resolveToCollection(Resource resource, Class<E> elementType){
		return resolveToCollection(resource, List.class, TypeDescriptor.valueOf(elementType));
	}
	
	public static <E> Collection<E> resolveToCollection(String resource, Class<E> elementType){
		return resolveToCollection(ResourceUtils.getResourceOperations().getResource(resource), elementType);
	}

	public static void setValue(Object instance, scw.mapper.Field field, Object value){
		MapperUtils.setValue(getConversionServiceFactory(), instance, field, value);
	}
}
