package scw.env.support;

import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import scw.core.Constants;
import scw.core.utils.ArrayUtils;
import scw.core.utils.ClassUtils;
import scw.env.ConfigurableEnvironment;
import scw.event.EmptyObservable;
import scw.event.Observable;
import scw.instance.factory.ServiceLoaderFactory;
import scw.io.ProtocolResolver;
import scw.io.Resource;
import scw.io.ResourceLoader;
import scw.io.ResourceUtils;
import scw.io.event.ObservableProperties;
import scw.io.resolver.PropertiesResolver;
import scw.io.resolver.support.DefaultPropertiesResolver;
import scw.lang.Nullable;
import scw.value.Value;
import scw.value.factory.PropertyFactory;

public class DefaultEnvironment extends DefaultPropertyManager implements ConfigurableEnvironment {
	private final DefaultEnvironmentResourceLoader resourceLoader = new DefaultEnvironmentResourceLoader(this, this);
	
	public DefaultEnvironment(boolean concurrent){
		super(concurrent);
		resourceLoader.addPropertiesResolver(DefaultPropertiesResolver.INSTANCE);
	}
	
	public boolean isSupportResolveProperties(Resource resource) {
		return resourceLoader.isSupportResolveProperties(resource);
	};
	
	public void addProtocolResolver(scw.io.ProtocolResolver resolver) {
		resourceLoader.addProtocolResolver(resolver);
	};
	
	public void addResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader.addResourceLoader(resourceLoader);
	}
	
	public void resolveProperties(Properties properties, Resource resource,
			Charset charset) {
		resourceLoader.resolveProperties(properties, resource, charset);
	}
	
	public void addPropertiesResolver(PropertiesResolver propertiesResolver) {
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
		return loadProperties(resource, (String) null);
	}

	public final Observable<Map<String, Value>> loadProperties(String resource,
			String charsetName) {
		return loadProperties(null, resource, charsetName);
	}

	public final Observable<Map<String, Value>> loadProperties(
			final String keyPrefix, String resource, String charsetName) {
		return loadProperties(keyPrefix, resource, charsetName, true);
	}

	public final Observable<Map<String, Value>> loadProperties(String resource,
			boolean format) {
		return loadProperties((String) resource, (String) null, format);
	}

	public final Observable<Map<String, Value>> loadProperties(String resource,
			String charsetName, boolean format) {
		return loadProperties(null, resource, charsetName, format);
	}

	public final Observable<Map<String, Value>> loadProperties(
			final String keyPrefix, String location, String charsetName,
			final boolean format) {
		Observable<Properties> observable = getProperties(location, charsetName);
		return loadProperties(keyPrefix, observable, format);
	}

	public final Observable<Map<String, Value>> loadProperties(
			String keyPrefix, boolean format, Charset charset,
			Resource... resources) {
		return loadProperties(keyPrefix, new ObservableProperties(this,
				resources, charset), format);
	}

	public final Observable<Map<String, Value>> loadProperties(
			String keyPrefix, boolean format, Charset charset,
			Collection<Resource> resources) {
		return loadProperties(keyPrefix, new ObservableProperties(this,
				resources, charset), format);
	}

	public final void loadProperties(Properties properties) {
		loadProperties(null, properties, false);
	}

	public final void loadProperties(String keyPrefix, Properties properties) {
		loadProperties(keyPrefix, properties, false);
	}

	public final void loadProperties(String keyPrefix, Properties properties,
			boolean format) {
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
						: (keyPrefix + key.toString()), value, format);
			}
		}
	}

	public String getWorkPath() {
		return getString(WORK_PATH_PROPERTY);
	}

	public void setWorkPath(String path) {
		put(WORK_PATH_PROPERTY, path);
	}
	
	public String getCharsetName() {
		return getValue(CHARSET_PROPERTY, String.class, Constants.UTF_8_NAME);
	}
	
	public Charset getCharset() {
		return getValue(CHARSET_PROPERTY, Charset.class, Constants.UTF_8);
	}
	
	public void config(ServiceLoaderFactory serviceLoaderFactory){
		for(PropertiesResolver propertiesResolver : serviceLoaderFactory.getServiceLoader(PropertiesResolver.class)){
			addPropertiesResolver(propertiesResolver);
		}
		
		for(ResourceLoader resourceLoader : serviceLoaderFactory.getServiceLoader(ResourceLoader.class)){
			addResourceLoader(resourceLoader);
		}
		
		for(ProtocolResolver protocolResolver : serviceLoaderFactory.getServiceLoader(ProtocolResolver.class)){
			addProtocolResolver(protocolResolver);
		}
		
		for(PropertyFactory propertyFactory : serviceLoaderFactory.getServiceLoader(PropertyFactory.class)){
			addPropertyFactory(propertyFactory);
		}
	}
}