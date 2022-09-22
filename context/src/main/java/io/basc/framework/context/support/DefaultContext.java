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
import io.basc.framework.context.ProviderServiceLoader;
import io.basc.framework.context.annotation.ComponentScan;
import io.basc.framework.context.annotation.ComponentScans;
import io.basc.framework.context.annotation.ProviderClassAccept;
import io.basc.framework.context.ioc.ConfigurableIocResolver;
import io.basc.framework.context.ioc.IocResolver;
import io.basc.framework.context.ioc.annotation.IocBeanResolverExtend;
import io.basc.framework.context.ioc.support.DefaultIocResolver;
import io.basc.framework.context.repository.RepositoryContextResolverExtend;
import io.basc.framework.context.xml.XmlContextPostProcessor;
import io.basc.framework.core.type.filter.TypeFilter;
import io.basc.framework.env.DefaultEnvironment;
import io.basc.framework.factory.BeanDefinition;
import io.basc.framework.factory.ConfigurableServices;
import io.basc.framework.factory.FactoryException;
import io.basc.framework.factory.ServiceLoader;
import io.basc.framework.factory.ServiceLoaderFactory;
import io.basc.framework.factory.support.ServiceLoaders;
import io.basc.framework.io.DefaultResourceLoader;
import io.basc.framework.io.FileSystemResourceLoader;
import io.basc.framework.io.Resource;
import io.basc.framework.lang.Constants;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.util.CollectionUtils;
import io.basc.framework.util.ConcurrentReferenceHashMap;
import io.basc.framework.util.Services;

public class DefaultContext extends DefaultEnvironment implements ConfigurableContext {
	private static Logger logger = LoggerFactory.getLogger(DefaultContext.class);
	private final DefaultClassesLoaderFactory classesLoaderFactory;
	private final LinkedHashSetClassesLoader sourceClasses = new LinkedHashSetClassesLoader();
	private final DefaultClassesLoader contextClassesLoader = new DefaultClassesLoader();
	private final DefaultResourceLoader classesResourceLoader = new FileSystemResourceLoader();
	private final ContextTypeFilter contextTypeFilter = new ContextTypeFilter(getProperties());
	private final ConfigurableServices<ContextPostProcessor> contextPostProcessors = new ConfigurableServices<ContextPostProcessor>();
	private final Services<Resource> configurationResources = new Services<Resource>();
	private final DefaultContextResolver contextResolver = new DefaultContextResolver();
	private final ConfigurableIocResolver iocResolver = new DefaultIocResolver(this);

	public DefaultContext() {
		contextResolver.addService(new RepositoryContextResolverExtend(this));
		iocResolver.addService(new IocBeanResolverExtend(this));
		getBeanResolver().addService(new IocBeanResolverExtend(this));

		registerSingleton(Context.class.getName(), this);
		registerSingleton(IocResolver.class.getName(), iocResolver);
		this.classesLoaderFactory = new DefaultClassesLoaderFactory(classesResourceLoader);

		// 添加默认的类
		contextClassesLoader.add(sourceClasses);

		// 扫描框架类，忽略(.test.)路径
		componentScan(Constants.SYSTEM_PACKAGE_NAME, null);
	}

	public ContextTypeFilter getContextTypeFilter() {
		return contextTypeFilter;
	}

	@Override
	protected boolean useSpi(Class<?> serviceClass) {
		for (Class<?> sourceClass : sourceClasses) {
			Package pg = sourceClass.getPackage();
			if (pg == null) {
				continue;
			}

			if (serviceClass.getName().startsWith(pg.getName())) {
				return true;
			}
		}
		return super.useSpi(serviceClass);
	}

	@Override
	public void configure(ServiceLoaderFactory serviceLoaderFactory) {
		iocResolver.configure(serviceLoaderFactory);
		contextClassesLoader.configure(serviceLoaderFactory);
		classesResourceLoader.configure(serviceLoaderFactory);
		super.configure(serviceLoaderFactory);
	}

	@Override
	public ConfigurableClassesLoader getContextClasses() {
		return contextClassesLoader;
	}

	@Override
	public ClassesLoaderFactory getClassesLoaderFactory() {
		return classesLoaderFactory;
	}

	@Override
	public LinkedHashSetClassesLoader getSourceClasses() {
		return sourceClasses;
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

	@Override
	public void source(Class<?> sourceClass) {
		if (!sourceClasses.add(sourceClass)) {
			throw new IllegalArgumentException("Already source " + sourceClass);
		}

		if (sourceClass.getPackage() != null) {
			componentScan(sourceClass.getPackage().getName());
		}

		ComponentScan componentScan = sourceClass.getAnnotation(ComponentScan.class);
		if (componentScan != null) {
			componentScan(componentScan);
		}

		ComponentScans componentScans = sourceClass.getAnnotation(ComponentScans.class);
		if (componentScans != null) {
			for (ComponentScan scan : componentScans.value()) {
				componentScan(scan);
			}
		}
	}

	private void componentScan(ComponentScan componentScan) {
		for (String name : componentScan.value()) {
			componentScan(name);
		}

		for (String name : componentScan.basePackages()) {
			componentScan(name);
		}
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
	private volatile ClassesLoader providerClassesLoader;

	public ClassesLoader getProviderClassesLoader() {
		if (providerClassesLoader == null) {
			synchronized (this) {
				if (providerClassesLoader == null) {
					providerClassesLoader = new AcceptClassesLoader(getContextClasses(), ProviderClassAccept.INSTANCE);
				}
			}
		}
		return providerClassesLoader;
	}

	@SuppressWarnings("unchecked")
	public <S> ServiceLoader<S> getServiceLoader(Class<S> serviceClass) {
		ServiceLoader<?> serviceLoader = serviceLoaderCacheMap.get(serviceClass);
		if (serviceLoader == null) {
			serviceLoader = serviceLoaderCacheMap.get(serviceClass);
			if (serviceLoader != null) {
				return (ServiceLoader<S>) serviceLoader;
			}

			ServiceLoader<S> parentServiceLoader = new ProviderServiceLoader<S>(getProviderClassesLoader(), this,
					getContextResolver(), serviceClass);
			ServiceLoader<S> defaultServiceLoader = super.getServiceLoader(serviceClass);
			ServiceLoader<S> created = new ServiceLoaders<S>(parentServiceLoader, defaultServiceLoader);
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
