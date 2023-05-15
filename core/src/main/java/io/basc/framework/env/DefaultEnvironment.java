package io.basc.framework.env;

import java.nio.charset.Charset;
import java.util.Properties;

import io.basc.framework.beans.BeanDefinition;
import io.basc.framework.convert.ConversionServiceAware;
import io.basc.framework.convert.lang.ConfigurableConversionService;
import io.basc.framework.convert.lang.ConverterConversionService;
import io.basc.framework.convert.lang.ResourceToPropertiesConverter;
import io.basc.framework.convert.resolve.ResourceResolverConversionService;
import io.basc.framework.convert.resolve.ResourceResolvers;
import io.basc.framework.convert.support.DefaultConversionService;
import io.basc.framework.event.Observable;
import io.basc.framework.event.support.ObservableResource;
import io.basc.framework.factory.Configurable;
import io.basc.framework.factory.ConfigurableServices;
import io.basc.framework.factory.FactoryException;
import io.basc.framework.factory.ServiceLoaderFactory;
import io.basc.framework.factory.support.ConfigServiceLoader;
import io.basc.framework.factory.support.DefaultBeanFactory;
import io.basc.framework.io.Resource;
import io.basc.framework.io.ResourceUtils;
import io.basc.framework.io.resolver.PropertiesResolver;
import io.basc.framework.io.resolver.PropertiesResolvers;
import io.basc.framework.lang.Constants;
import io.basc.framework.lang.Nullable;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.util.AntPathMatcher;
import io.basc.framework.util.Processor;
import io.basc.framework.util.Registration;
import io.basc.framework.util.ServiceLoader;
import io.basc.framework.util.ServiceLoaders;
import io.basc.framework.util.ServiceRegistry;
import io.basc.framework.util.StringMatchers;

public class DefaultEnvironment extends DefaultBeanFactory implements ConfigurableEnvironment, Configurable {
	private static final String ENABLE_PREFIX = "io.basc.framework.spi";

	private static Logger logger = LoggerFactory.getLogger(DefaultEnvironment.class);
	private boolean configured = false;

	private final DefaultConversionService conversionService = new DefaultConversionService();
	private final ConfigurableServices<EnvironmentPostProcessor> environmentPostProcessors = new ConfigurableServices<EnvironmentPostProcessor>(
			EnvironmentPostProcessor.class);

	private final DefaultEnvironmentResourceLoader environmentResourceLoader = new DefaultEnvironmentResourceLoader(
			this);

	/**
	 * 是否强制使用spi
	 */
	private boolean forceSpi = false;

	private volatile boolean initialized = false;

	private Environment parentEnvironment;

	// properties和environmentResourceLoader不能更换顺序
	private final ConfigurableEnvironmentProperties properties = new ConfigurableEnvironmentProperties();
	private final PropertiesResolvers propertiesResolvers = new PropertiesResolvers();

	private final ResourceResolvers resourceResolvers = new ResourceResolvers(propertiesResolvers, conversionService,
			getObservableCharset());

	private final ServiceRegistry<Resource> resources = new ServiceRegistry<>();

	public DefaultEnvironment() {
		properties.setConversionService(conversionService);
		conversionService.register(new ConverterConversionService(Resource.class, Properties.class,
				Processor.of(new ResourceToPropertiesConverter(resourceResolvers.getPropertiesResolvers()))));
		conversionService.register(new ResourceResolverConversionService(resourceResolvers));
		registerSingleton(Environment.class.getName(), this);
		this.properties.getServiceInjectors().register((instance) -> {
			if (instance instanceof EnvironmentAware) {
				((EnvironmentAware) instance).setEnvironment(this);
			}
			return Registration.EMPTY;
		});
	}

	@Override
	protected void _dependence(Object instance, BeanDefinition definition) throws FactoryException {
		super._dependence(instance, definition);
		if (instance instanceof ConversionServiceAware) {
			((ConversionServiceAware) instance).setConversionService(getConversionService());
		}
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
	protected <S> ServiceLoader<S> getServiceLoaderInternal(Class<S> serviceClass) {
		ServiceLoader<S> configServiceLoader = new ConfigServiceLoader<S>(serviceClass, getProperties(), this);
		if (isForceSpi() || serviceClass.getName().startsWith(Constants.SYSTEM_PACKAGE_NAME) || useSpi(serviceClass)) {
			ServiceLoaders<S> registry = new ServiceLoaders<>();
			registry.register(configServiceLoader);
			registry.register(super.getServiceLoaderInternal(serviceClass));
			return registry;
		}
		return configServiceLoader;
	}

	@Override
	public ConfigurableConversionService getConversionService() {
		return conversionService;
	}

	public ConfigurableServices<EnvironmentPostProcessor> getEnvironmentPostProcessors() {
		return environmentPostProcessors;
	}

	public Environment getParentEnvironment() {
		return parentEnvironment;
	}

	@Override
	public ConfigurableEnvironmentProperties getProperties() {
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
	public ServiceRegistry<Resource> getResources() {
		return resources;
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

				for (EnvironmentPostProcessor postProcessor : environmentPostProcessors.getServices()) {
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

	@Override
	public boolean isConfigured() {
		return configured;
	}

	public boolean isForceSpi() {
		return forceSpi;
	}

	@Override
	public boolean isInitialized() {
		return super.isInitialized() && initialized;
	}

	public void setForceSpi(boolean forceSpi) {
		this.forceSpi = forceSpi;
	}

	public void setParentEnvironment(Environment environment) {
		setParentBeanFactory(environment);
		this.parentEnvironment = environment;
		if (environment != null) {
			conversionService.registerLast(environment.getConversionService());
			properties.setParentProperties(environment.getProperties());
			environmentResourceLoader.getResourceLoaders().registerLast(environment.getResourceLoader());
			resourceResolvers.registerLast(environment.getResourceResolver());
			propertiesResolvers.registerLast(environment.getPropertiesResolver());
		}
	}

	public Registration source(Observable<Properties> properties) {
		return this.getProperties().getArchive().registerProperties(properties);
	}

	@Override
	public Registration source(Resource resource) {
		return source(resource, getCharset());
	}

	public Registration source(Resource resource, @Nullable Charset charset) {
		return source(resource, getPropertiesResolver(), charset);
	}

	public Registration source(Resource resource, PropertiesResolver propertiesResolver, @Nullable Charset charset) {
		if (resource == null || !resource.exists()) {
			return Registration.EMPTY;
		}

		Registration registration = resources.register(resource);
		if (registration.isEmpty()) {
			return registration;
		}

		logger.info("Import resource {}", resource);
		if (propertiesResolver.canResolveProperties(resource)) {
			Observable<Properties> observable = new ObservableResource(resource)
					.map(ResourceUtils.toPropertiesConverter(getPropertiesResolver()));
			registration = registration.and(source(observable));
		}
		return registration;
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
}