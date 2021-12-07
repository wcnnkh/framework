package io.basc.framework.env;

import java.util.Iterator;
import java.util.Properties;

import io.basc.framework.convert.lang.ConversionServices;
import io.basc.framework.convert.resolve.ResourceResolvers;
import io.basc.framework.convert.support.DefaultConversionServices;
import io.basc.framework.event.Observable;
import io.basc.framework.factory.Configurable;
import io.basc.framework.factory.ConfigurableServices;
import io.basc.framework.factory.ServiceLoaderFactory;
import io.basc.framework.io.ProtocolResolver;
import io.basc.framework.io.Resource;
import io.basc.framework.io.ResourceLoader;
import io.basc.framework.io.resolver.PropertiesResolvers;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.Assert;
import io.basc.framework.util.ClassLoaderProvider;
import io.basc.framework.util.ClassUtils;
import io.basc.framework.util.DefaultClassLoaderProvider;
import io.basc.framework.util.MultiIterator;
import io.basc.framework.util.placeholder.ConfigurablePlaceholderReplacer;
import io.basc.framework.util.placeholder.support.DefaultPlaceholderReplacer;
import io.basc.framework.value.AnyValue;
import io.basc.framework.value.PropertyFactory;
import io.basc.framework.value.StringValue;
import io.basc.framework.value.Value;
import io.basc.framework.value.support.DefaultPropertyFactory;

public class DefaultEnvironment extends DefaultPropertyFactory
		implements ConfigurableEnvironment, Configurable, PropertyWrapper {
	private final DefaultEnvironmentResourceLoader environmentResourceLoader = new DefaultEnvironmentResourceLoader(
			this);
	private final DefaultConversionServices conversionServices = new DefaultConversionServices();
	private final DefaultPlaceholderReplacer placeholderReplacer = new DefaultPlaceholderReplacer();

	private ClassLoaderProvider classLoaderProvider;

	public DefaultEnvironment() {
		this(null);
	}

	public DefaultEnvironment(@Nullable ClassLoaderProvider classLoaderProvider) {
		super(true);
		this.classLoaderProvider = classLoaderProvider;
	}

	public void setClassLoaderProvider(ClassLoaderProvider classLoaderProvider) {
		this.classLoaderProvider = classLoaderProvider;
	}

	public void setClassLoader(ClassLoader classLoader) {
		setClassLoaderProvider(new DefaultClassLoaderProvider(classLoader));
	}

	@Override
	public void addProtocolResolver(ProtocolResolver resolver) {
		environmentResourceLoader.addProtocolResolver(resolver);
	}

	@Override
	public void addResourceLoader(ResourceLoader resourceLoader) {
		environmentResourceLoader.addResourceLoader(resourceLoader);
	}

	@Override
	public Resource getResource(String location) {
		return environmentResourceLoader.getResource(location);
	}

	public Resource[] getResources(String locationPattern) {
		return environmentResourceLoader.getResources(locationPattern);
	}

	protected void aware(Object instance) {
		if (instance == null) {
			return;
		}

		if (instance instanceof EnvironmentAware) {
			((EnvironmentAware) instance).setEnvironment(this);
		}
	}

	@Override
	public void addFactory(PropertyFactory propertyFactory) {
		aware(propertyFactory);
		super.addFactory(propertyFactory);
	}

	public ClassLoader getClassLoader() {
		return ClassUtils.getClassLoader(classLoaderProvider);
	}

	public boolean put(String key, Object value) {
		Assert.requiredArgument(key != null, "key");
		Assert.requiredArgument(value != null, "value");
		return put(key, wrap(key, value));
	}

	public boolean putIfAbsent(String key, Object value) {
		Assert.requiredArgument(key != null, "key");
		Assert.requiredArgument(value != null, "value");
		return putIfAbsent(key, wrap(key, value));
	}

	public void loadProperties(String keyPrefix, Observable<Properties> properties) {
		ObservablePropertiesPropertyFactory factory = new ObservablePropertiesPropertyFactory(properties, keyPrefix,
				this);
		addFactory(factory);
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

	private ConfigurableServices<PropertyFactory> propertyFactorys = new ConfigurableServices<>(PropertyFactory.class,
			(s) -> aware(s));

	@Override
	public Iterator<PropertyFactory> getFactories() {
		return new MultiIterator<>(super.getFactories(), propertyFactorys.iterator());
	}

	@Override
	public void configure(ServiceLoaderFactory serviceLoaderFactory) {
		environmentResourceLoader.configure(serviceLoaderFactory);
		conversionServices.configure(serviceLoaderFactory);
		propertyFactorys.configure(serviceLoaderFactory);
		placeholderReplacer.configure(serviceLoaderFactory);
	}

	@Override
	public ConfigurablePlaceholderReplacer getPlaceholderReplacer() {
		return placeholderReplacer;
	}

	@Override
	public PropertiesResolvers getPropertiesResolver() {
		return conversionServices.getResourceResolvers().getPropertiesResolvers();
	}

	@Override
	public ConversionServices getConversionService() {
		return conversionServices;
	}

	@Override
	public ResourceResolvers getResourceResolver() {
		return conversionServices.getResourceResolvers();
	}

	@Override
	public Value wrap(String key, Object value) {
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
}