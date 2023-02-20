package io.basc.framework.env;

import java.util.Properties;
import java.util.function.Consumer;

import io.basc.framework.convert.ConversionServiceAware;
import io.basc.framework.convert.lang.ConfigurableConversionService;
import io.basc.framework.convert.lang.ConverterConversionService;
import io.basc.framework.convert.lang.ResourceToPropertiesConverter;
import io.basc.framework.convert.resolve.ResourceResolverConversionService;
import io.basc.framework.convert.resolve.ResourceResolvers;
import io.basc.framework.convert.support.DefaultConversionService;
import io.basc.framework.event.Observable;
import io.basc.framework.factory.BeanDefinition;
import io.basc.framework.factory.Configurable;
import io.basc.framework.factory.ConfigurableServices;
import io.basc.framework.factory.FactoryException;
import io.basc.framework.factory.ServiceLoader;
import io.basc.framework.factory.ServiceLoaderFactory;
import io.basc.framework.factory.support.ConfigServiceLoader;
import io.basc.framework.factory.support.DefaultBeanFactory;
import io.basc.framework.factory.support.ServiceLoaders;
import io.basc.framework.io.Resource;
import io.basc.framework.io.resolver.PropertiesResolvers;
import io.basc.framework.lang.Constants;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.util.AntPathMatcher;
import io.basc.framework.util.Processor;
import io.basc.framework.util.StringMatchers;
import io.basc.framework.util.placeholder.ConfigurablePlaceholderReplacer;
import io.basc.framework.util.placeholder.support.DefaultPlaceholderReplacer;
import io.basc.framework.value.AnyValue;
import io.basc.framework.value.PropertyFactory;
import io.basc.framework.value.PropertyWrapper;
import io.basc.framework.value.Value;

public class DefaultEnvironment extends DefaultBeanFactory
		implements ConfigurableEnvironment, PropertyWrapper, Consumer<PropertyFactory>, Configurable {
	private static Logger logger = LoggerFactory.getLogger(DefaultEnvironment.class);

	private class AnyFormatValue extends AnyValue {

		public AnyFormatValue(Object value) {
			super(value, null, DefaultEnvironment.this.getConversionService());
		}

		public String getAsString() {
			return replacePlaceholders(super.getAsString());
		};
	}

	private static final String ENABLE_PREFIX = "io.basc.framework.spi";
	private final DefaultConversionService conversionService = new DefaultConversionService();

	// properties和environmentResourceLoader不能更换顺序
	private final DefaultEnvironmentProperties properties = new DefaultEnvironmentProperties(this);
	private final DefaultEnvironmentResourceLoader environmentResourceLoader = new DefaultEnvironmentResourceLoader(
			this);

	private final PropertiesResolvers propertiesResolvers = new PropertiesResolvers();

	private final ResourceResolvers resourceResolvers = new ResourceResolvers(propertiesResolvers, conversionService,
			getObservableCharset());

	/**
	 * 是否强制使用spi
	 */
	private boolean forceSpi = false;

	private final DefaultPlaceholderReplacer placeholderReplacer = new DefaultPlaceholderReplacer();

	private final ConfigurableServices<EnvironmentPostProcessor> environmentPostProcessors = new ConfigurableServices<EnvironmentPostProcessor>(
			EnvironmentPostProcessor.class);
	private Environment parentEnvironment;

	public DefaultEnvironment() {
		conversionService.addService(new ConverterConversionService(Resource.class, Properties.class,
				Processor.of(new ResourceToPropertiesConverter(resourceResolvers.getPropertiesResolvers()))));
		conversionService.addService(new ResourceResolverConversionService(resourceResolvers));
		registerSingleton(Environment.class.getName(), this);
		this.properties.getPropertyFactories().getFactories().getConsumers().addService(this);
	}

	public Environment getParentEnvironment() {
		return parentEnvironment;
	}

	public void setParentEnvironment(Environment environment) {
		setParentBeanFactory(environment);
		this.parentEnvironment = environment;
		placeholderReplacer.setAfterService(environment == null ? null : environment.getPlaceholderReplacer());
		conversionService.setAfterService(environment == null ? null : environment.getConversionService());
		properties.getPropertyFactories().getFactories()
				.setAfterService(environment == null ? null : environment.getProperties());
		environmentResourceLoader.getResourceLoaders()
				.setAfterService(environment == null ? null : environment.getResourceLoader());
		resourceResolvers.setAfterService(environment == null ? null : environment.getResourceResolver());
		propertiesResolvers.setAfterService(environment == null ? null : environment.getPropertiesResolver());
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

	private volatile boolean initialized = false;

	private boolean configured = false;

	@Override
	public boolean isConfigured() {
		return configured;
	}

	@Override
	public synchronized void configure(ServiceLoaderFactory serviceLoaderFactory) {
		configured = true;

		if (!environmentResourceLoader.isConfigured()) {
			environmentResourceLoader.configure(serviceLoaderFactory);
		}

		if (!conversionService.isConfigured()) {
			conversionService.configure(serviceLoaderFactory);
		}

		if (!properties.isConfigured()) {
			properties.configure(serviceLoaderFactory);
		}

		if (!placeholderReplacer.isConfigured()) {
			placeholderReplacer.configure(this);
		}

		if (!environmentPostProcessors.isConfigured()) {
			environmentPostProcessors.configure(serviceLoaderFactory);
		}

		if (!resourceResolvers.isConfigured()) {
			resourceResolvers.configure(serviceLoaderFactory);
		}

		if (!propertiesResolvers.isConfigured()) {
			propertiesResolvers.configure(serviceLoaderFactory);
		}
	}

	@Override
	public void init() throws FactoryException {
		synchronized (this) {
			if (isInitialized()) {
				throw new FactoryException("The environment has been initialized");
			}

			try {
				super.init();
				logger.debug("Start initializing environment[{}]!", this);

				if (!isConfigured()) {
					configure(this);
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
	public ConfigurableConversionService getConversionService() {
		return conversionService;
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
		return propertiesResolvers;
	}

	@Override
	public ConfigurableEnvironmentResourceLoader getResourceLoader() {
		return environmentResourceLoader;
	}

	@Override
	public ResourceResolvers getResourceResolver() {
		return resourceResolvers;
	}

	@Override
	protected <S> ServiceLoader<S> getBeforeServiceLoader(Class<S> serviceClass) {
		return new ServiceLoaders<>(new ConfigServiceLoader<S>(serviceClass, getProperties(), this),
				super.getBeforeServiceLoader(serviceClass));
	}

	@Override
	protected <S> ServiceLoader<S> getAfterServiceLoader(Class<S> serviceClass) {
		if (isForceSpi() || serviceClass.getName().startsWith(Constants.SYSTEM_PACKAGE_NAME) || useSpi(serviceClass)) {
			return super.getAfterServiceLoader(serviceClass);
		}
		return null;
	}

	public boolean isForceSpi() {
		return forceSpi;
	}

	public void loadProperties(Observable<Properties> properties) {
		this.getProperties().getObservable().registerProperties(properties);
	}

	public void setForceSpi(boolean forceSpi) {
		this.forceSpi = forceSpi;
	}

	protected boolean useSpi(Class<?> serviceClass) {
		String[] prefixs = getProperties().getAsObject(ENABLE_PREFIX, String[].class);
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
		} else {
			v = new AnyFormatValue(value);
		}
		return v;
	}

	@Override
	protected void _dependence(Object instance, BeanDefinition definition) throws FactoryException {
		super._dependence(instance, definition);
		if (instance instanceof ConversionServiceAware) {
			((ConversionServiceAware) instance).setConversionService(getConversionService());
		}
	}
}