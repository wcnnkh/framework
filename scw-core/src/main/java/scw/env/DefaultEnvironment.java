package scw.env;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

import scw.aop.ConfigurableProxyFactory;
import scw.aop.support.DefaultConfigurableProxyFactory;
import scw.convert.ConfigurableConversionService;
import scw.convert.ConversionService;
import scw.convert.resolve.ConfigurableResourceResolver;
import scw.convert.resolve.ResourceResolver;
import scw.convert.resolve.ResourceResolverConversionService;
import scw.convert.resolve.support.DefaultResourceResolver;
import scw.convert.support.DefaultConversionService;
import scw.core.Assert;
import scw.core.utils.ArrayUtils;
import scw.core.utils.ClassUtils;
import scw.core.utils.CollectionUtils;
import scw.core.utils.StringUtils;
import scw.env.ObservablePropertiesPropertyFactory.ValueCreator;
import scw.event.ChangeEvent;
import scw.event.EventListener;
import scw.event.EventRegistration;
import scw.event.KeyValuePairEvent;
import scw.event.MultiEventRegistration;
import scw.event.Observable;
import scw.event.support.ObservableMap;
import scw.event.support.StringNamedEventDispatcher;
import scw.instance.ServiceLoaderFactory;
import scw.io.FileSystemResourceLoader;
import scw.io.ProtocolResolver;
import scw.io.Resource;
import scw.io.ResourceLoader;
import scw.io.resolver.ConfigurablePropertiesResolver;
import scw.io.resolver.PropertiesResolver;
import scw.io.resolver.support.PropertiesResolvers;
import scw.logger.Logger;
import scw.logger.LoggerFactory;
import scw.net.message.convert.DefaultMessageConverters;
import scw.net.message.convert.MessageConverters;
import scw.util.CollectionFactory;
import scw.util.ConcurrentReferenceHashMap;
import scw.util.MultiIterator;
import scw.value.AbstractPropertyFactory;
import scw.value.AnyValue;
import scw.value.ListenablePropertyFactory;
import scw.value.PropertyFactory;
import scw.value.StringValue;
import scw.value.Value;

public class DefaultEnvironment extends AbstractPropertyFactory implements ConfigurableEnvironment {
	private static final String[] SUFFIXS = new String[] {"scw_res_suffix", "SHUCHAOWEN_CONFIG_SUFFIX", "resource.suffix"};
	private static Logger logger = LoggerFactory.getLogger(DefaultEnvironment.class);
	
	private final ConcurrentReferenceHashMap<String, Resource> cacheMap = new ConcurrentReferenceHashMap<String, Resource>();
	private final FileSystemResourceLoader configurableResourceLoader = new FileSystemResourceLoader(){
		protected boolean ignoreClassPathResource(scw.io.FileSystemResource resource) {
			return super.ignoreClassPathResource(resource) || resource.getPath().startsWith(getWorkPath());
		};
	};
	
	private final PropertiesResolvers configurablePropertiesResolver = new PropertiesResolvers();
	private final ConfigurableConversionService configurableConversionService = new DefaultConversionService(configurablePropertiesResolver, getObservableCharset());
	private final ConfigurableResourceResolver configurableResourceResolver = new DefaultResourceResolver(configurableConversionService, configurablePropertiesResolver, getObservableCharset());
	private final DefaultConfigurableProxyFactory proxyFactory = new DefaultConfigurableProxyFactory();
	private final ObservableMap<String, Value> propertyMap;
	private final List<PropertyFactory> propertyFactories;
	private final DefaultMessageConverters messageConverters = new DefaultMessageConverters(configurableConversionService);
	
	public DefaultEnvironment(){
		configurableResourceLoader.setClassLoaderProvider(this);
		configurableConversionService.addConversionService(new ResourceResolverConversionService(configurableResourceResolver));
		this.propertyMap = new ObservableMap<String, Value>(
				true,
				new StringNamedEventDispatcher<KeyValuePairEvent<String, Value>>(
						true));
		this.propertyFactories = CollectionFactory.createArrayList(true,
				8);
	}
	
	@Override
	public void addProtocolResolver(ProtocolResolver resolver) {
		configurableResourceLoader.addProtocolResolver(resolver);
	}

	@Override
	public void addResourceLoader(ResourceLoader resourceLoader) {
		configurableResourceLoader.addResourceLoader(resourceLoader);
	}
	
	private Resource getResourceByCache(String location) {
		Resource resource = cacheMap.get(location);
		if (resource == null) {
			resource = configurableResourceLoader.getResource(location);
			Resource cache = cacheMap.putIfAbsent(location, resource);
			if (cache != null) {
				resource = cache;
			}else {
				if(logger.isDebugEnabled()) {
					logger.debug("Find resource {} result {}", location, resource);
				}
			}
			if (resource == null) {
				cacheMap.putIfAbsent(location, Resource.NONEXISTENT_RESOURCE);
			}
		}
		return resource;
	}

	/**
	 * 可使用的资源别名，使用优先级从左到右
	 * 
	 * @return
	 */
	protected String[] getResourceEnvironmentalNames() {
		Value value = null;
		for(String suffix : SUFFIXS) {
			value = getValue(suffix);
			if(value != null && !value.isEmpty()) {
				return value.getAsObject(String[].class);
			}
		}
		return StringUtils.MEPTY_ARRAY;
	}

	/**
	 * 预计使用的资源列表，返回的资源并不一定存在, 使用优先级从高到低
	 * 
	 * @param resourceName
	 * @return
	 */
	public List<String> getEnvironmentalResourceNameList(String resourceName) {
		String[] suffixs = getResourceEnvironmentalNames();
		String resourceNameToUse = resolvePlaceholders(resourceName);
		if (ArrayUtils.isEmpty(suffixs)) {
			return Arrays.asList(resourceNameToUse);
		}

		List<String> list = new ArrayList<String>(suffixs.length + 1);
		for (int i = suffixs.length - 1; i >= 0; i--) {
			list.add(getEnvironmentalResourceName(resourceNameToUse, suffixs[i]));
		}
		list.add(resourceNameToUse);
		return list;
	}

	protected String getEnvironmentalResourceName(String resourceName, String evnironmental) {
		int index = resourceName.lastIndexOf(".");
		if (index == -1) {// 不存在
			return resourceName + evnironmental;
		} else {
			return resourceName.substring(0, index) + evnironmental + resourceName.substring(index);
		}
	};
	
	public Resource[] getResources(String locationPattern) {
		List<String> nameList = getEnvironmentalResourceNameList(locationPattern);
		List<Resource> resources = new ArrayList<Resource>(nameList.size());
		for (String name : nameList) {
			Resource res = getResourceByCache(name);
			if (res == null) {
				continue;
			}
			resources.add(res);
		}
		
		if(logger.isDebugEnabled()) {
			logger.debug("Get resources [{}] results {}", resources);
		}
		return resources.toArray(new Resource[0]);
	}
	
	protected void aware(Object instance){
		if(instance instanceof EnvironmentAware){
			((EnvironmentAware) instance).setEnvironment(this);
		}
	}
	
	@Override
	public void addPropertyFactory(PropertyFactory propertyFactory) {
		if (propertyFactory == null) {
			return;
		}

		aware(propertyFactory);
		propertyFactories.add(propertyFactory);
	}
	
	public ClassLoader getClassLoader() {
		return ClassUtils.getDefaultClassLoader();
	}
	
	protected Iterator<PropertyFactory> getPropertyFactoriesIterator() {
		return CollectionUtils.getIterator(propertyFactories, true);
	}

	public List<PropertyFactory> getPropertyFactories() {
		return Collections.unmodifiableList(propertyFactories);
	}

	public Value getValue(String key) {
		Value value = propertyMap.get(key);
		if (value != null) {
			return value;
		}

		Iterator<PropertyFactory> iterator = getPropertyFactoriesIterator();
		while (iterator.hasNext()) {
			value = iterator.next().getValue(key);
			if (value != null) {
				return value;
			}
		}
		return null;
	}

	public Iterator<String> iterator() {
		List<Iterator<String>> iterators = new LinkedList<Iterator<String>>();
		iterators.add(propertyMap.keySet().iterator());
		Iterator<PropertyFactory> iterator = getPropertyFactoriesIterator();
		while (iterator.hasNext()) {
			iterators.add(iterator.next().iterator());
		}
		return new MultiIterator<String>(iterators);
	}

	public boolean containsKey(String key) {
		if (propertyMap.containsKey(key)) {
			return true;
		}

		Iterator<PropertyFactory> iterator = getPropertyFactoriesIterator();
		while (iterator.hasNext()) {
			if (iterator.next().containsKey(key)) {
				return true;
			}
		}
		return false;
	}

	public EventRegistration registerListener(final String key,
			final EventListener<ChangeEvent<String>> eventListener) {
		EventRegistration registration1 = this.propertyMap.getEventDispatcher()
				.registerListener(key,
						new EventListener<KeyValuePairEvent<String, Value>>() {

							public void onEvent(
									KeyValuePairEvent<String, Value> event) {
								eventListener.onEvent(new ChangeEvent<String>(
										event, event.getSource().getKey()));
							}
						});

		if (propertyFactories.size() == 0) {
			return registration1;
		}

		List<EventRegistration> registrations = new ArrayList<EventRegistration>(
				propertyFactories.size());
		registrations.add(registration1);
		Iterator<PropertyFactory> iterator = getPropertyFactoriesIterator();
		while (iterator.hasNext()) {
			PropertyFactory propertyFactory = iterator.next();
			if (propertyFactory instanceof ListenablePropertyFactory) {
				EventRegistration registration = ((ListenablePropertyFactory) propertyFactory)
						.registerListener(key, eventListener);
				registrations.add(registration);
			}
		}
		return new MultiEventRegistration(
				registrations.toArray(new EventRegistration[0]));
	}

	public boolean remove(String key) {
		Assert.requiredArgument(key != null, "key");
		Value value = propertyMap.remove(key);
		return value != null;
	}

	public boolean put(String key, Value value) {
		Assert.requiredArgument(key != null, "key");
		Assert.requiredArgument(value != null, "value");
		propertyMap.put(key, value);
		return true;
	}

	public boolean putIfAbsent(String key, Value value) {
		Assert.requiredArgument(key != null, "key");
		Assert.requiredArgument(value != null, "value");
		return propertyMap.putIfAbsent(key, value) == null;
	}

	public boolean put(String key, Object value) {
		Assert.requiredArgument(key != null, "key");
		Assert.requiredArgument(value != null, "value");
		return put(key, toProperty(value));
	}

	public Value toProperty(Object value) {
		Value v;
		if (value instanceof Value) {
			return (Value) value;
		} else if (value instanceof String) {
			v = new StringFormatValue((String) value);
		} else {
			v = new AnyFormatValue(value);
		}
		return v;
	}

	public boolean putIfAbsent(String key, Object value) {
		Assert.requiredArgument(key != null, "key");
		Assert.requiredArgument(value != null, "value");
		return propertyMap.putIfAbsent(key, toProperty(value)) == null;
	}

	public void clear() {
		propertyMap.clear();
	}
	
	public void loadProperties(
			String keyPrefix, Observable<Properties> properties) {
		ValueCreator valueCreator = new ValueCreator() {

			public Value create(String key, Object value) {
				return toProperty(value);
			}
		};

		ObservablePropertiesPropertyFactory factory = new ObservablePropertiesPropertyFactory(
				properties, keyPrefix, valueCreator);
		addPropertyFactory(factory);
	}

	private class StringFormatValue extends StringValue {
		private static final long serialVersionUID = 1L;

		public StringFormatValue(String value) {
			super(value);
		}

		@Override
		public String getAsString() {
			return resolvePlaceholders(super.getAsString());
		}
	}

	private class AnyFormatValue extends AnyValue {
		private static final long serialVersionUID = 1L;

		public AnyFormatValue(Object value) {
			super(value, DefaultEnvironment.this.getConversionService());
		}

		public String getAsString() {
			return resolvePlaceholders(super.getAsString());
		};
	}
	
	private final AtomicBoolean loaded = new AtomicBoolean();
	
	/**
	 * 使用ServiceLoaderFactory加载依赖服务
	 * @param serviceLoaderFactory
	 * @return 返回是否加载成功
	 */
	public boolean loadServices(ServiceLoaderFactory serviceLoaderFactory, Logger logger){
		if(loaded.compareAndSet(false, true)){
			proxyFactory.loadServices(serviceLoaderFactory);
			
			for(PropertiesResolver propertiesResolver : serviceLoaderFactory.getServiceLoader(PropertiesResolver.class)){
				logger.info("add properties resolver: {}", propertiesResolver);
				configurablePropertiesResolver.addPropertiesResolver(propertiesResolver);
			}
			
			for(ResourceResolver resourceResolver : serviceLoaderFactory.getServiceLoader(ResourceResolver.class)){
				logger.info("add resource resolver: {}", resourceResolver);
				configurableResourceResolver.addResourceResolver(resourceResolver);
			}
			
			for(ResourceLoader resourceLoader : serviceLoaderFactory.getServiceLoader(ResourceLoader.class)){
				logger.info("add resource loader: {}", resourceLoader);
				addResourceLoader(resourceLoader);
			}
			
			for(ProtocolResolver protocolResolver : serviceLoaderFactory.getServiceLoader(ProtocolResolver.class)){
				logger.info("add protocol resolver: {}", protocolResolver);
				addProtocolResolver(protocolResolver);
			}
			
			for(ConversionService conversionService : serviceLoaderFactory.getServiceLoader(ConversionService.class)){
				logger.info("add conversion service: {}", conversionService);
				configurableConversionService.addConversionService(conversionService);
			}
			
			for(PropertyFactory propertyFactory : serviceLoaderFactory.getServiceLoader(PropertyFactory.class)){
				logger.info("add property factory: {}", propertyFactory);
				addPropertyFactory(propertyFactory);
			}
			return true;
		}
		return false;
	}

	@Override
	public ConfigurablePropertiesResolver getPropertiesResolver() {
		return configurablePropertiesResolver;
	}

	@Override
	public ConfigurableProxyFactory getProxyFactory() {
		return proxyFactory;
	}

	@Override
	public ConfigurableConversionService getConversionService() {
		return configurableConversionService;
	}

	@Override
	public ConfigurableResourceResolver getResourceResolver() {
		return configurableResourceResolver;
	}
	
	@Override
	public MessageConverters getMessageConverter() {
		return messageConverters;
	}
}