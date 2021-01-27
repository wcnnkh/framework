package scw.env.support;

import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

import scw.convert.ConfigurableConversionService;
import scw.convert.ConversionService;
import scw.convert.TypeDescriptor;
import scw.convert.resolve.ConfigurableResourceResolver;
import scw.convert.resolve.ResourceResolver;
import scw.convert.resolve.ResourceResolverConversionService;
import scw.convert.resolve.support.DefaultResourceResolver;
import scw.convert.support.DefaultConversionService;
import scw.core.Constants;
import scw.core.utils.ArrayUtils;
import scw.core.utils.ClassUtils;
import scw.env.ConfigurableEnvironment;
import scw.env.EnvironmentAware;
import scw.event.EmptyObservable;
import scw.event.Observable;
import scw.instance.ServiceLoaderFactory;
import scw.io.ProtocolResolver;
import scw.io.Resource;
import scw.io.ResourceLoader;
import scw.io.ResourceUtils;
import scw.io.event.ObservableProperties;
import scw.io.resolver.PropertiesResolver;
import scw.lang.Nullable;
import scw.value.Value;
import scw.value.factory.PropertyFactory;

public class DefaultEnvironment extends DefaultPropertyManager implements ConfigurableEnvironment {
	private final DefaultEnvironmentResourceLoader resourceLoader = new DefaultEnvironmentResourceLoader(this, this);
	private final ConfigurableConversionService configurableConversionService = new DefaultConversionService(this, getObservableCharset());
	
	private final ConfigurableResourceResolver configurableResourceResolver = new DefaultResourceResolver(this, this, getObservableCharset());
	
	public DefaultEnvironment(boolean concurrent){
		super(concurrent);
		setConversionService(this);
		addConversionService(new ResourceResolverConversionService(this));
	}
	
	protected void aware(Object instance){
		if(instance instanceof EnvironmentAware){
			((EnvironmentAware) instance).setEnvironment(this);
		}
	}
	
	@Override
	public void addPropertyFactory(PropertyFactory propertyFactory) {
		aware(propertyFactory);
		super.addPropertyFactory(propertyFactory);
	}
	
	public void addResourceResolver(ResourceResolver resourceResolver) {
		aware(resourceResolver);
		configurableResourceResolver.addResourceResolver(resourceResolver);
	}
	
	public boolean canResolveResource(Resource resource,
			TypeDescriptor targetType) {
		return configurableResourceResolver.canResolveResource(resource, targetType);
	}
	
	public Object resolveResource(Resource resource, TypeDescriptor targetType) {
		return configurableResourceResolver.resolveResource(resource, targetType);
	}
	
	public Object resolveResource(String location, TypeDescriptor targetType) {
		Resource resource = getResource(location);
		if(resource == null || !resource.exists()){
			return null;
		}
		
		return resolveResource(resource, targetType);
	}
	
	public boolean canConvert(TypeDescriptor sourceType,
			TypeDescriptor targetType) {
		return configurableConversionService.canConvert(sourceType, targetType);
	}
	
	public Object convert(Object source, TypeDescriptor sourceType,
			TypeDescriptor targetType) {
		return configurableConversionService.convert(source, sourceType, targetType);
	}
	
	public void addConversionService(ConversionService conversionService) {
		aware(conversionService);
		configurableConversionService.addConversionService(conversionService);
	}
	
	public boolean canResolveProperties(Resource resource) {
		return resourceLoader.canResolveProperties(resource);
	};
	
	public void addProtocolResolver(ProtocolResolver protocolResolver) {
		aware(protocolResolver);
		resourceLoader.addProtocolResolver(protocolResolver);
	};
	
	public void addResourceLoader(ResourceLoader resourceLoader) {
		aware(resourceLoader);
		this.resourceLoader.addResourceLoader(resourceLoader);
	}
	
	public void resolveProperties(Properties properties, Resource resource,
			Charset charset) {
		resourceLoader.resolveProperties(properties, resource, charset);
	}
	
	public void addPropertiesResolver(PropertiesResolver propertiesResolver) {
		aware(propertiesResolver);
		resourceLoader.addPropertiesResolver(propertiesResolver);
	}
	
	public Observable<Properties> getProperties(String location) {
		return resourceLoader.getProperties(this, location, getCharsetName());
	}
	
	public Observable<Properties> getProperties(String location,
			@Nullable String charsetName) {
		return resourceLoader.getProperties(this, location, charsetName == null? getCharsetName():charsetName);
	}
	
	public Observable<Properties> getProperties(String location,
			@Nullable Charset charset) {
		return resourceLoader.getProperties(this, location, charset == null? getCharset():charset);
	}
	
	public Observable<Properties> getProperties(PropertiesResolver propertiesResolver, String location) {
		return resourceLoader.getProperties(propertiesResolver, location, getCharsetName());
	}
	
	public Observable<Properties> getProperties(PropertiesResolver propertiesResolver, String location,
			@Nullable String charsetName) {
		return resourceLoader.getProperties(propertiesResolver, location, charsetName == null? getCharset():Charset.forName(charsetName));
	}
	
	public Observable<Properties> getProperties(PropertiesResolver propertiesResolver, String location,
			@Nullable Charset charset) {
		Resource[] resources = resourceLoader.getResources(location);
		if (ArrayUtils.isEmpty(resources)) {
			return new EmptyObservable<Properties>();
		}
		//颠倒一下，优先级高的覆盖优先级低的
		return new ObservableProperties(propertiesResolver, (Resource[])ArrayUtils.reversal(resources), charset == null? getCharset():charset);
	}
	
	public boolean exists(String location){
		return ResourceUtils.exists(this, location);
	}
	
	public ClassLoader getClassLoader() {
		return ClassUtils.getDefaultClassLoader();
	}

	public Resource getResource(String location) {
		return resourceLoader.getResource(location);
	}

	public Resource[] getResources(String locationPattern) {
		return resourceLoader.getResources(locationPattern);
	}
	
	public final Observable<Map<String, Value>> loadProperties(String resource) {
		return loadProperties((String) resource, (String) null);
	}

	public final Observable<Map<String, Value>> loadProperties(String resource, String charsetName) {
		return loadProperties(null, resource, charsetName);
	}

	public final Observable<Map<String, Value>> loadProperties(String keyPrefix, String location, String charsetName) {
		Observable<Properties> observable = getProperties(location, charsetName);
		return loadProperties(keyPrefix, observable);
	}

	public final Observable<Map<String, Value>> loadProperties(
			String keyPrefix, Charset charset,
			Resource... resources) {
		return loadProperties(keyPrefix, new ObservableProperties(this, resources, charset));
	}

	public final Observable<Map<String, Value>> loadProperties(
			String keyPrefix, Charset charset,
			Collection<Resource> resources) {
		return loadProperties(keyPrefix, new ObservableProperties(this, resources, charset));
	}

	public final void loadProperties(Properties properties) {
		loadProperties(null, properties);
	}

	public final void loadProperties(String keyPrefix, Properties properties) {
		if (properties != null) {
			for (Entry<Object, Object> entry : properties.entrySet()) {
				Object key = entry.getKey();
				if (key == null) {
					continue;
				}

				Object value = entry.getValue();
				if (value == null) {
					continue;
				}
				put(keyPrefix == null ? key.toString()
						: (keyPrefix + key.toString()), value);
			}
		}
	}

	public String getWorkPath() {
		return getString(WORK_PATH_PROPERTY);
	}
	
	public Observable<String> getObservableWorkPath() {
		return getObservableValue(WORK_PATH_PROPERTY, String.class, null);
	}

	public void setWorkPath(String path) {
		put(WORK_PATH_PROPERTY, path);
	}
	
	public String getCharsetName() {
		return getValue(CHARSET_PROPERTY, String.class, Constants.UTF_8_NAME);
	}
	
	public Observable<String> getObservableCharsetName() {
		return getObservableValue(CHARSET_PROPERTY, String.class, Constants.UTF_8_NAME);
	}
	
	public Charset getCharset() {
		return getValue(CHARSET_PROPERTY, Charset.class, Constants.UTF_8);
	}
	
	public Observable<Charset> getObservableCharset() {
		return getObservableValue(CHARSET_PROPERTY, Charset.class, Constants.UTF_8);
	}
	
	private final AtomicBoolean loaded = new AtomicBoolean();
	
	/**
	 * 使用ServiceLoaderFactory加载依赖服务
	 * @param serviceLoaderFactory
	 * @return 返回是否加载成功
	 */
	public boolean loaderServices(ServiceLoaderFactory serviceLoaderFactory){
		if(loaded.get()){
			return false;
		}
		
		if(loaded.compareAndSet(false, true)){
			for(PropertiesResolver propertiesResolver : serviceLoaderFactory.getServiceLoader(PropertiesResolver.class)){
				addPropertiesResolver(propertiesResolver);
			}
			
			for(ResourceResolver resourceResolver : serviceLoaderFactory.getServiceLoader(ResourceResolver.class)){
				addResourceResolver(resourceResolver);
			}
			
			for(ResourceLoader resourceLoader : serviceLoaderFactory.getServiceLoader(ResourceLoader.class)){
				addResourceLoader(resourceLoader);
			}
			
			for(ProtocolResolver protocolResolver : serviceLoaderFactory.getServiceLoader(ProtocolResolver.class)){
				addProtocolResolver(protocolResolver);
			}
			
			for(ConversionService conversionService : serviceLoaderFactory.getServiceLoader(ConversionService.class)){
				addConversionService(conversionService);
			}
			
			for(PropertyFactory propertyFactory : serviceLoaderFactory.getServiceLoader(PropertyFactory.class)){
				addPropertyFactory(propertyFactory);
			}
			return true;
		}
		return false;
	}
}