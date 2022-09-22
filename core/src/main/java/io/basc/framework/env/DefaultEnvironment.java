package io.basc.framework.env;

import java.util.Properties;
import java.util.function.Consumer;

import io.basc.framework.convert.lang.ConversionServices;
import io.basc.framework.convert.resolve.ResourceResolvers;
import io.basc.framework.convert.support.DefaultConversionServices;
import io.basc.framework.event.Observable;
import io.basc.framework.factory.Configurable;
import io.basc.framework.factory.ConfigurableServices;
import io.basc.framework.factory.FactoryException;
import io.basc.framework.factory.ServiceLoader;
import io.basc.framework.factory.ServiceLoaderFactory;
import io.basc.framework.factory.support.ConfigServiceLoader;
import io.basc.framework.factory.support.DefaultBeanFactory;
import io.basc.framework.factory.support.ServiceLoaders;
import io.basc.framework.io.resolver.PropertiesResolvers;
import io.basc.framework.lang.Constants;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.util.AntPathMatcher;
import io.basc.framework.util.StringMatchers;
import io.basc.framework.util.placeholder.ConfigurablePlaceholderReplacer;
import io.basc.framework.util.placeholder.support.DefaultPlaceholderReplacer;
import io.basc.framework.value.AnyValue;
import io.basc.framework.value.PropertyFactory;
import io.basc.framework.value.StringValue;
import io.basc.framework.value.Value;

public class DefaultEnvironment extends DefaultBeanFactory
		implements ConfigurableEnvironment, Configurable, PropertyWrapper, Consumer<PropertyFactory> {
	private static Logger logger = LoggerFactory.getLogger(DefaultEnvironment.class);

	private class AnyFormatValue extends AnyValue {
		private static final long serialVersionUID = 1L;

		public AnyFormatValue(Object value) {
			super(value, DefaultEnvironment.this.getConversionService());
		}

		public String getAsString() {
			return replacePlaceholders(super.getAsString());
		};
	}

	private class StringFormatValue extends StringValue {
		private static final long serialVersionUID = 1L;

		public StringFormatValue(String value) {
			super(value);
		}

		@Override
		public String getAsString() {
			return replacePlaceholders(super.getAsString());
		}
	}

	private static final String ENABLE_PREFIX = "io.basc.framework.spi";
	private final DefaultConversionServices conversionServices = new DefaultConversionServices();

	private final DefaultEnvironmentResourceLoader environmentResourceLoader = new DefaultEnvironmentResourceLoader(
			this);

	/**
	 * 是否强制使用spi
	 */
	private boolean forceSpi = false;

	private final DefaultPlaceholderReplacer placeholderReplacer = new DefaultPlaceholderReplacer();

	private final DefaultEnvironmentProperties properties = new DefaultEnvironmentProperties(this);
	private final ConfigurableServices<EnvironmentPostProcessor> environmentPostProcessors = new ConfigurableServices<EnvironmentPostProcessor>();

	public DefaultEnvironment() {
		registerSingleton(Environment.class.getName(), this);
		this.properties.getTandemFactories().getConsumers().addService(this);
	}

	@Override
	public void accept(PropertyFactory instance) {
		if (instance == null) {
			return;
		}

		if (instance instanceof EnvironmentAware) {
			((EnvironmentAware) instance).setEnvironment(this);
		}
	}

	@Override
	public void configure(ServiceLoaderFactory serviceLoaderFactory) {
		environmentResourceLoader.configure(serviceLoaderFactory);
		conversionServices.configure(serviceLoaderFactory);
		properties.getTandemFactories().configure(serviceLoaderFactory);
		placeholderReplacer.configure(serviceLoaderFactory);
	}

	private volatile boolean initialized = false;

	@Override
	public void init() throws FactoryException {
		synchronized (this) {
			if (isInitialized()) {
				throw new FactoryException("The environment has been initialized");
			}

			try {
				super.init();
				logger.debug("Start initializing environment[{}]!", this);

				if (!environmentPostProcessors.isConfigured()) {
					environmentPostProcessors.configure(this);
				}

				for (EnvironmentPostProcessor postProcessor : environmentPostProcessors) {
					try {
						postProcessor.postProcessEnvironment(this);
					} catch (Throwable e) {
						throw new FactoryException("Post process environment[" + postProcessor + "]", e);
					}
				}

				logger.debug("Started environment[{}]!", this);
			} finally {
				this.initialized = true;
			}
		}
	}

	public ConfigurableServices<EnvironmentPostProcessor> getEnvironmentPostProcessors() {
		return environmentPostProcessors;
	}

	@Override
	public boolean isInitialized() {
		return super.isInitialized() && initialized;
	}

	@Override
	public ConversionServices getConversionService() {
		return conversionServices;
	}

	@Override
	public ConfigurablePlaceholderReplacer getPlaceholderReplacer() {
		return placeholderReplacer;
	}

	@Override
	public DefaultEnvironmentProperties getProperties() {
		return this.properties;
	}

	@Override
	public PropertiesResolvers getPropertiesResolver() {
		return conversionServices.getResourceResolvers().getPropertiesResolvers();
	}

	@Override
	public ConfigurableEnvironmentResourceLoader getResourceLoader() {
		return environmentResourceLoader;
	}

	@Override
	public ResourceResolvers getResourceResolver() {
		return conversionServices.getResourceResolvers();
	}

	@Override
	public <S> ServiceLoader<S> getServiceLoader(Class<S> serviceClass) {
		ServiceLoader<S> configServiceLoader = new ConfigServiceLoader<S>(serviceClass, getProperties(), this);
		if (isForceSpi() || serviceClass.getName().startsWith(Constants.SYSTEM_PACKAGE_NAME) || useSpi(serviceClass)) {
			return new ServiceLoaders<S>(configServiceLoader, super.getServiceLoader(serviceClass));
		}
		return configServiceLoader;
	}

	public boolean isForceSpi() {
		return forceSpi;
	}

	public void loadProperties(String keyPrefix, Observable<Properties> properties) {
		ObservablePropertiesPropertyFactory factory = new ObservablePropertiesPropertyFactory(properties, keyPrefix,
				this);
		this.properties.getTandemFactories().addService(factory);
	}

	public void setForceSpi(boolean forceSpi) {
		this.forceSpi = forceSpi;
	}

	protected boolean useSpi(Class<?> serviceClass) {
		String[] prefixs = getProperties().getObject(ENABLE_PREFIX, String[].class);
		if (prefixs == null) {
			return false;
		}

		for (String prefix : prefixs) {
			if (StringMatchers.matchAny(prefix, serviceClass.getName())
					|| AntPathMatcher.POINT_PATH_MATCHER.match(prefix, serviceClass.getName())) {
				return true;
			}
		}
		return false;
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