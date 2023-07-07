package io.basc.framework.env;

import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Properties;

import io.basc.framework.beans.factory.Scope;
import io.basc.framework.beans.factory.ServiceLoaderFactory;
import io.basc.framework.beans.factory.config.Configurable;
import io.basc.framework.beans.factory.support.DefaultServiceLoaderFactory;
import io.basc.framework.convert.config.ConversionServiceAware;
import io.basc.framework.convert.config.support.ConfigurableConversionService;
import io.basc.framework.convert.lang.ConverterConversionService;
import io.basc.framework.convert.lang.ResourceToPropertiesConverter;
import io.basc.framework.convert.resolve.ResourceResolverConversionService;
import io.basc.framework.convert.resolve.ResourceResolvers;
import io.basc.framework.convert.support.DefaultConversionService;
import io.basc.framework.env.config.EnvironmentPostProcessors;
import io.basc.framework.event.Observable;
import io.basc.framework.event.support.ObservableResource;
import io.basc.framework.io.Resource;
import io.basc.framework.io.ResourceUtils;
import io.basc.framework.io.resolver.PropertiesResolver;
import io.basc.framework.io.resolver.PropertiesResolvers;
import io.basc.framework.lang.Nullable;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.util.CachedServiceLoader;
import io.basc.framework.util.Elements;
import io.basc.framework.util.Processor;
import io.basc.framework.util.Registration;
import io.basc.framework.util.ServiceLoader;
import io.basc.framework.util.ServiceRegistry;
import io.basc.framework.util.StringUtils;

public class DefaultEnvironment extends DefaultServiceLoaderFactory implements ConfigurableEnvironment, Configurable {
	private static Logger logger = LoggerFactory.getLogger(DefaultEnvironment.class);
	private boolean configured = false;

	private final DefaultConversionService conversionService = new DefaultConversionService();
	private final EnvironmentPostProcessors environmentPostProcessors = new EnvironmentPostProcessors();
	private final DefaultEnvironmentResourceLoader environmentResourceLoader = new DefaultEnvironmentResourceLoader(
			this);

	private volatile boolean initialized = false;

	private Environment parentEnvironment;

	// properties和environmentResourceLoader不能更换顺序
	private final ConfigurableEnvironmentProperties properties = new ConfigurableEnvironmentProperties();
	private final PropertiesResolvers propertiesResolvers = new PropertiesResolvers();

	private final ResourceResolvers resourceResolvers = new ResourceResolvers(propertiesResolvers, conversionService,
			getObservableCharset());

	private final ServiceRegistry<Resource> resources = new ServiceRegistry<>();

	public DefaultEnvironment(Scope scope) {
		super(scope);
		conversionService.getServiceInjectorRegistry().register(getServiceInjectorRegistry());
		environmentPostProcessors.getServiceInjectorRegistry().register(getServiceInjectorRegistry());
		environmentResourceLoader.getProtocolResolvers().getServiceInjectorRegistry()
				.register(getServiceInjectorRegistry());
		environmentResourceLoader.getResourceLoaders().getServiceInjectorRegistry()
				.register(getServiceInjectorRegistry());
		properties.getServiceInjectorRegistry().register(getServiceInjectorRegistry());

		getServiceInjectorRegistry().register((bean) -> {
			if (bean instanceof EnvironmentAware) {
				((EnvironmentAware) bean).setEnvironment(this);
			}

			if (bean instanceof ConversionServiceAware) {
				((ConversionServiceAware) bean).setConversionService(getConversionService());
			}

			return Registration.EMPTY;
		});

		// 注册一个默认的参数解析
		properties.setConversionService(conversionService);
		conversionService.register(new ConverterConversionService(Resource.class, Properties.class,
				Processor.of(new ResourceToPropertiesConverter(resourceResolvers.getPropertiesResolvers()))));
		conversionService.register(new ResourceResolverConversionService(resourceResolvers));
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
	protected <S> void postProcessorServiceRegistry(ServiceRegistry<S> serviceRegistry, Class<S> serviceClass) {
		ServiceLoader<S> serviceLoader = new CachedServiceLoader<>(Elements.of(() -> {
			String services = properties.getAsString(serviceClass.getName());
			if (StringUtils.isEmpty(services)) {
				return Collections.emptyIterator();
			}

			String[] array = StringUtils.splitToArray(services);
			return Elements.forArray(array).map((e) -> getBean(e, serviceClass)).iterator();
		}));
		serviceRegistry.getServiceLoaderRegistry().register(serviceLoader);
		super.postProcessorServiceRegistry(serviceRegistry, serviceClass);
	}

	@Override
	public ConfigurableConversionService getConversionService() {
		return conversionService;
	}

	public EnvironmentPostProcessors getEnvironmentPostProcessors() {
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
	protected void _init() {
		super._init();

		logger.debug("Start initializing environment[{}]!", this);

		if (!isConfigured()) {
			configure(this);
		}

		environmentPostProcessors.postProcessEnvironment(this);
		logger.debug("Started environment[{}]!", this);
	}

	@Override
	public boolean isConfigured() {
		return configured;
	}

	@Override
	public boolean isInitialized() {
		return super.isInitialized() && initialized;
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
}