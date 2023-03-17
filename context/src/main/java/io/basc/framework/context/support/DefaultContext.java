package io.basc.framework.context.support;

import java.util.Collection;

import io.basc.framework.context.ClassesLoader;
import io.basc.framework.context.ClassesLoaderFactory;
import io.basc.framework.context.ConfigurableClassesLoader;
import io.basc.framework.context.ConfigurableContext;
import io.basc.framework.context.ConfigurableContextResolver;
import io.basc.framework.context.Context;
import io.basc.framework.context.ContextAware;
import io.basc.framework.context.ContextPostProcessor;
import io.basc.framework.context.ProviderClassesLoader;
import io.basc.framework.context.annotation.AnnotationContextResolverExtend;
import io.basc.framework.context.ioc.ConfigurableIocResolver;
import io.basc.framework.context.ioc.IocResolver;
import io.basc.framework.context.ioc.annotation.IocBeanResolverExtend;
import io.basc.framework.context.repository.annotation.RepositoryContextResolverExtend;
import io.basc.framework.context.xml.XmlContextPostProcessor;
import io.basc.framework.core.type.filter.TypeFilter;
import io.basc.framework.env.DefaultEnvironment;
import io.basc.framework.env.Sys;
import io.basc.framework.factory.BeanDefinition;
import io.basc.framework.factory.BeanFactory;
import io.basc.framework.factory.ConfigurableServices;
import io.basc.framework.factory.FactoryException;
import io.basc.framework.factory.ServiceLoader;
import io.basc.framework.factory.support.BeanDefinitionLoaderChain;
import io.basc.framework.io.Resource;
import io.basc.framework.lang.Constants;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.util.ClassUtils;
import io.basc.framework.util.CollectionUtils;
import io.basc.framework.util.ConcurrentReferenceHashMap;
import io.basc.framework.util.Services;

public class DefaultContext extends DefaultEnvironment implements ConfigurableContext {
	private static final String BEANS_CONFIGURATION = "io.basc.framework.beans.configuration";
	private static final String DEFAULT_BEANS_CONFIGURATION = "beans.xml";

	private static Logger logger = LoggerFactory.getLogger(DefaultContext.class);
	private final DefaultClassesLoaderFactory classesLoaderFactory;
	private final DefaultClassesLoader contextClassesLoader = new DefaultClassesLoader();
	private final ContextTypeFilter contextTypeFilter = new ContextTypeFilter(getProperties());
	private final ConfigurableServices<ContextPostProcessor> contextPostProcessors = new ConfigurableServices<ContextPostProcessor>(
			ContextPostProcessor.class);
	private final Services<Resource> configurationResources = new Services<Resource>();
	private final ConfigurableContextResolver contextResolver = new ConfigurableContextResolver();
	private final ConfigurableIocResolver iocResolver = new ConfigurableIocResolver();

	public DefaultContext() {
		IocBeanResolverExtend iocBeanResolverExtend = new IocBeanResolverExtend(this, iocResolver);
		iocResolver.setAfterService(iocBeanResolverExtend);
		this.classesLoaderFactory = new DefaultClassesLoaderFactory(getResourceLoader());
		contextResolver.addService(new AnnotationContextResolverExtend(this));
		contextResolver.addService(new RepositoryContextResolverExtend(this));
		getBeanResolver().addService(new AnnotationContextResolverExtend(this));
		getBeanResolver().addService(iocBeanResolverExtend);

		registerSingleton(Context.class.getName(), this);
		registerSingleton(IocResolver.class.getName(), iocResolver);
		setParentEnvironment(Sys.getEnv());
		// 这是为了执行init时重新选择parentBeanFactory
		setParentBeanFactory(null);

		// 扫描框架类
		componentScan(Constants.SYSTEM_PACKAGE_NAME, null);
	}

	@Override
	public BeanDefinition load(BeanFactory beanFactory, ClassLoader classLoader, String name,
			BeanDefinitionLoaderChain chain) throws FactoryException {
		Class<?> clazz = ClassUtils.getClass(name, classLoader);
		if (clazz == null) {
			return super.load(beanFactory, classLoader, name, chain);
		}

		ProviderClassesLoader providerClassesLoader = getProviderClassesLoader(clazz);
		for (Class<?> providerClass : providerClassesLoader) {
			if (beanFactory.isInstance(providerClass)) {
				logger.info("The provider of {} is {}", clazz, providerClass);
				return beanFactory.getDefinition(providerClass);
			}
		}
		return super.load(beanFactory, classLoader, name, chain);
	}

	private final ConcurrentReferenceHashMap<Class<?>, ProviderClassesLoader> providerClassesLoaderMap = new ConcurrentReferenceHashMap<>(
			128);

	public ProviderClassesLoader getProviderClassesLoader(Class<?> providerClass) {
		ProviderClassesLoader classesLoader = providerClassesLoaderMap.get(providerClass);
		if (classesLoader == null) {
			classesLoader = new ProviderClassesLoader(getContextClasses(), providerClass, contextResolver);
			if (!classesLoader.iterator().hasNext()) {
				return classesLoader;
			}

			ProviderClassesLoader old = providerClassesLoaderMap.putIfAbsent(providerClass, classesLoader);
			if (old == null) {
				// 插入成功
				providerClassesLoaderMap.purgeUnreferencedEntries();
			} else {
				classesLoader = old;
			}
		}
		return classesLoader;
	}

	public ContextTypeFilter getContextTypeFilter() {
		return contextTypeFilter;
	}

	@Override
	public ConfigurableClassesLoader getContextClasses() {
		return contextClassesLoader;
	}

	@Override
	public ClassesLoaderFactory getClassesLoaderFactory() {
		return classesLoaderFactory;
	}

	private volatile boolean initialized = false;

	@Override
	public void init() throws FactoryException {
		synchronized (this) {
			if (isInitialized()) {
				throw new FactoryException("Context has been initialized");
			}

			try {
				super.init();
				logger.debug("Start initializing context[{}]!", this);

				if (!iocResolver.isConfigured()) {
					iocResolver.configure(this);
				}

				String beansConfiguration = getProperties().get(BEANS_CONFIGURATION).or(DEFAULT_BEANS_CONFIGURATION)
						.getAsString();
				getConfigurationResources().addService(getResourceLoader().getResource(beansConfiguration));

				postProcessContext(new XmlContextPostProcessor());

				if (!contextResolver.isConfigured()) {
					contextResolver.configure(this);
				}

				for (Class<?> clazz : getContextClasses()) {
					Collection<BeanDefinition> definitions = contextResolver.resolveBeanDefinitions(clazz);
					if (CollectionUtils.isEmpty(definitions)) {
						continue;
					}

					for (BeanDefinition definition : definitions) {
						if (containsDefinition(definition.getId())) {
							continue;
						}

						registerDefinition(definition);
					}
				}

				if (!contextPostProcessors.isConfigured()) {
					contextPostProcessors.configure(this);
				}

				for (ContextPostProcessor postProcessor : contextPostProcessors) {
					postProcessContext(postProcessor);
				}
				logger.debug("Started context[{}]!", this);
			} finally {
				initialized = true;
			}
		}
	}

	public void postProcessContext(ContextPostProcessor processor) {
		try {
			processor.postProcessContext(this);
		} catch (Throwable e) {
			throw new FactoryException("Post process context[" + processor + "]", e);
		}
	}

	public ConfigurableServices<ContextPostProcessor> getContextPostProcessors() {
		return contextPostProcessors;
	}

	@Override
	public boolean isInitialized() {
		return super.isInitialized() && initialized;
	}

	public void componentScan(String packageName) {
		if (packageName.startsWith(Constants.SYSTEM_PACKAGE_NAME)) {
			// 已经默认包含了
			return;
		}

		componentScan(packageName, null);
	}

	public void componentScan(String packageName, TypeFilter typeFilter) {
		ClassesLoader classesLoader = getClassesLoaderFactory().getClassesLoader(packageName,
				(e, m) -> contextTypeFilter.match(e, m) && (typeFilter == null || typeFilter.match(e, m)));
		getContextClasses().add(classesLoader);
	}

	private ConcurrentReferenceHashMap<Class<?>, ServiceLoader<?>> serviceLoaderCacheMap = new ConcurrentReferenceHashMap<Class<?>, ServiceLoader<?>>();

	@Override
	protected <S> ServiceLoader<S> getAfterServiceLoader(Class<S> serviceClass) {
		return ServiceLoader.concat(new ClassesServiceLoader<S>(getProviderClassesLoader(serviceClass), this),
				super.getAfterServiceLoader(serviceClass));
	}

	@SuppressWarnings("unchecked")
	public <S> ServiceLoader<S> getServiceLoader(Class<S> serviceClass) {
		ServiceLoader<?> serviceLoader = serviceLoaderCacheMap.get(serviceClass);
		if (serviceLoader == null) {
			serviceLoader = serviceLoaderCacheMap.get(serviceClass);
			if (serviceLoader != null) {
				return (ServiceLoader<S>) serviceLoader;
			}

			ServiceLoader<S> created = super.getServiceLoader(serviceClass);
			ServiceLoader<?> old = serviceLoaderCacheMap.putIfAbsent(serviceClass, created);
			if (old == null) {
				old = created;
			} else {
				// 出现新的时清理缓存
				serviceLoaderCacheMap.purgeUnreferencedEntries();
			}
			return (ServiceLoader<S>) old;
		}
		return (ServiceLoader<S>) serviceLoader;
	}

	@Override
	public Services<Resource> getConfigurationResources() {
		return configurationResources;
	}

	@Override
	public ConfigurableContextResolver getContextResolver() {
		return contextResolver;
	}

	public ConfigurableIocResolver getIocResolver() {
		return iocResolver;
	}

	@Override
	protected void _dependence(Object instance, BeanDefinition definition) throws FactoryException {
		if (instance != null && definition != null) {
			ContextConfigurator configurator = new ContextConfigurator(this);
			configurator.configurationProperties(instance, definition.getTypeDescriptor());
		}

		super._dependence(instance, definition);
		if (instance instanceof ContextAware) {
			((ContextAware) instance).setContext(this);
		}
	}
}
