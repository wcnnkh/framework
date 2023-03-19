package io.basc.framework.context.support;

import java.util.Collection;
import java.util.Set;

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
import io.basc.framework.context.annotation.ComponentScan;
import io.basc.framework.context.annotation.ComponentScans;
import io.basc.framework.context.annotation.Import;
import io.basc.framework.context.annotation.ImportResource;
import io.basc.framework.context.ioc.ConfigurableIocResolver;
import io.basc.framework.context.ioc.IocResolver;
import io.basc.framework.context.ioc.annotation.IocBeanResolverExtend;
import io.basc.framework.context.repository.annotation.RepositoryContextResolverExtend;
import io.basc.framework.context.xml.XmlContextPostProcessor;
import io.basc.framework.core.annotation.AnnotatedElementUtils;
import io.basc.framework.core.type.filter.TypeFilter;
import io.basc.framework.env.DefaultEnvironment;
import io.basc.framework.env.Sys;
import io.basc.framework.factory.BeanDefinition;
import io.basc.framework.factory.BeanFactory;
import io.basc.framework.factory.ConfigurableServices;
import io.basc.framework.factory.FactoryException;
import io.basc.framework.factory.ServiceLoader;
import io.basc.framework.factory.support.BeanDefinitionLoaderChain;
import io.basc.framework.lang.Constants;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.util.ClassUtils;
import io.basc.framework.util.CollectionUtils;
import io.basc.framework.util.ConcurrentReferenceHashMap;
import io.basc.framework.util.Registration;
import io.basc.framework.util.StringUtils;

public class DefaultContext extends DefaultEnvironment implements ConfigurableContext {
	private static Logger logger = LoggerFactory.getLogger(DefaultContext.class);
	private final DefaultClassesLoaderFactory classesLoaderFactory;
	private final DefaultClassesLoader contextClassesLoader = new DefaultClassesLoader();
	private final ConfigurableServices<ContextPostProcessor> contextPostProcessors = new ConfigurableServices<ContextPostProcessor>(
			ContextPostProcessor.class);
	private final ConfigurableContextResolver contextResolver = new ConfigurableContextResolver();
	private volatile boolean initialized = false;
	private final ConfigurableIocResolver iocResolver = new ConfigurableIocResolver();

	private final ConcurrentReferenceHashMap<Class<?>, ProviderClassesLoader> providerClassesLoaderMap = new ConcurrentReferenceHashMap<>(
			128);

	private ConcurrentReferenceHashMap<Class<?>, ServiceLoader<?>> serviceLoaderCacheMap = new ConcurrentReferenceHashMap<Class<?>, ServiceLoader<?>>();

	private final DefaultClassesLoader sourceClasses = new DefaultClassesLoader();

	public DefaultContext() {
		contextClassesLoader.registerLoader(sourceClasses);
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

	private Registration componentScan(ComponentScan componentScan) {
		Registration registration = Registration.EMPTY;
		for (String name : componentScan.value()) {
			registration = registration.and(componentScan(name));
		}

		for (String name : componentScan.basePackages()) {
			registration = registration.and(componentScan(name));
		}
		return registration;
	}

	public Registration componentScan(String packageName) {
		if (packageName.startsWith(Constants.SYSTEM_PACKAGE_NAME)) {
			// 已经默认包含了
			return Registration.EMPTY;
		}

		return componentScan(packageName, contextResolver);
	}

	public Registration componentScan(String packageName, TypeFilter typeFilter) {
		ClassesLoader classesLoader = getClassesLoaderFactory().getClassesLoader(packageName, typeFilter);
		return getContextClasses().registerLoader(classesLoader);
	}

	@Override
	protected <S> ServiceLoader<S> getAfterServiceLoader(Class<S> serviceClass) {
		return ServiceLoader.concat(new ClassesServiceLoader<S>(getProviderClassesLoader(serviceClass), this),
				super.getAfterServiceLoader(serviceClass));
	}

	@Override
	public ClassesLoaderFactory getClassesLoaderFactory() {
		return classesLoaderFactory;
	}

	@Override
	public ConfigurableClassesLoader getContextClasses() {
		return contextClassesLoader;
	}

	public ConfigurableServices<ContextPostProcessor> getContextPostProcessors() {
		return contextPostProcessors;
	}

	@Override
	public ConfigurableContextResolver getContextResolver() {
		return contextResolver;
	}

	public ConfigurableIocResolver getIocResolver() {
		return iocResolver;
	}

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
	public DefaultClassesLoader getSourceClasses() {
		return sourceClasses;
	}

	@Override
	public void init() throws FactoryException {
		synchronized (this) {
			if (isInitialized()) {
				throw new FactoryException("Context has been initialized");
			}

			try {
				// 扫描框架类
				componentScan(Constants.SYSTEM_PACKAGE_NAME, contextResolver);

				super.init();
				logger.debug("Start initializing context[{}]!", this);

				if (!iocResolver.isConfigured()) {
					iocResolver.configure(this);
				}

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

	@Override
	public boolean isInitialized() {
		return super.isInitialized() && initialized;
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

	public void postProcessContext(ContextPostProcessor processor) {
		try {
			processor.postProcessContext(this);
		} catch (Throwable e) {
			throw new FactoryException("Post process context[" + processor + "]", e);
		}
	}

	@Override
	public Registration source(Class<?> sourceClass) {
		Registration registration = sourceClasses.register(sourceClass);
		if (registration.isEmpty()) {
			return Registration.EMPTY;
		}

		if (sourceClass.getPackage() != null) {
			registration = registration.and(componentScan(sourceClass.getPackage().getName()));
		}

		Set<ImportResource> importResources = AnnotatedElementUtils.getAllMergedAnnotations(sourceClass,
				ImportResource.class);
		for (ImportResource importResource : importResources) {
			String[] locations = importResource.value();
			for (String location : locations) {
				if (StringUtils.isEmpty(location)) {
					continue;
				}

				String path = replacePlaceholders(location);
				registration = registration.and(source(path));
			}
		}

		Set<Import> imports = AnnotatedElementUtils.getAllMergedAnnotations(sourceClass, Import.class);
		for (Import im : imports) {
			for (Class<?> clazz : im.value()) {
				registration = registration.and(source(clazz));
			}
		}

		ComponentScan componentScan = AnnotatedElementUtils.getMergedAnnotation(sourceClass, ComponentScan.class);
		if (componentScan != null) {
			registration = registration.and(componentScan(componentScan));
		}

		ComponentScans componentScans = AnnotatedElementUtils.getMergedAnnotation(sourceClass, ComponentScans.class);
		if (componentScans != null) {
			for (ComponentScan scan : componentScans.value()) {
				registration = registration.and(componentScan(scan));
			}
		}
		return registration;
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
}
