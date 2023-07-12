package io.basc.framework.context.support;

import java.util.Set;

import io.basc.framework.beans.factory.Scope;
import io.basc.framework.beans.factory.config.ConfigurableServices;
import io.basc.framework.context.ContextAware;
import io.basc.framework.context.annotation.AnnotationContextPostProcessor;
import io.basc.framework.context.annotation.AnnotationTypeFilterExtend;
import io.basc.framework.context.annotation.ComponentScan;
import io.basc.framework.context.annotation.ComponentScans;
import io.basc.framework.context.annotation.Import;
import io.basc.framework.context.annotation.ImportResource;
import io.basc.framework.context.config.ConfigurableContext;
import io.basc.framework.context.config.ConfigurableTypeFilter;
import io.basc.framework.context.config.ContextPostProcessor;
import io.basc.framework.context.config.ContextPostProcessors;
import io.basc.framework.context.config.support.DefaultClassScanner;
import io.basc.framework.context.jaxrs.JaxrsTypeFilterExtend;
import io.basc.framework.context.websocket.WebSocketTypeFilterExtend;
import io.basc.framework.core.annotation.AnnotatedElementUtils;
import io.basc.framework.core.type.filter.TypeFilter;
import io.basc.framework.env.DefaultEnvironment;
import io.basc.framework.env.Sys;
import io.basc.framework.execution.aop.Aop;
import io.basc.framework.lang.Constants;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.util.ClassLoaderProvider;
import io.basc.framework.util.Registration;
import io.basc.framework.util.ServiceLoader;
import io.basc.framework.util.Services;
import io.basc.framework.util.StringUtils;

public class DefaultContext extends DefaultEnvironment implements ConfigurableContext {
	private static Logger logger = LoggerFactory.getLogger(DefaultContext.class);
	private final DefaultClassScanner classScanner = new DefaultClassScanner();
	private final Services<Class<?>> contextClassesLoader = new Services<>();
	private final ContextPostProcessors contextPostProcessors = new ContextPostProcessors();
	private final ConfigurableTypeFilter configurableTypeFilter = new ConfigurableTypeFilter();
	private final Services<Class<?>> sourceClasses = new Services<Class<?>>();
	private ClassLoaderProvider classLoaderProvider;
	private final Aop aop = new Aop();

	public DefaultContext(Scope scope) {
		super(scope);
		classScanner.getServiceInjectors().register(getServiceInjectors());
		contextPostProcessors.getServiceInjectors().register(getServiceInjectors());
		configurableTypeFilter.getServiceInjectors().register(getServiceInjectors());
		getServiceInjectors().register((bean) -> {
			if (bean instanceof ContextAware) {
				((ContextAware) bean).setContext(this);
			}
			return Registration.EMPTY;
		});

		configurableTypeFilter.register(new AnnotationTypeFilterExtend());
		configurableTypeFilter.register(new WebSocketTypeFilterExtend());
		configurableTypeFilter.register(new JaxrsTypeFilterExtend());

		contextClassesLoader.getServiceLoaders().register(sourceClasses);

		// 注册后置处理器
		contextPostProcessors.register(new AnnotationContextPostProcessor());

		setParentEnvironment(Sys.getEnv());
		// 这是为了执行init时重新选择parentBeanFactory
		setParentBeanFactory(null);
	}

	@Override
	public Aop getAop() {
		return aop;
	}

	@Override
	public void setClassLoaderProvider(ClassLoaderProvider classLoaderProvider) {
		this.classLoaderProvider = classLoaderProvider;
	}

	@Override
	public ClassLoaderProvider getClassLoaderProvider() {
		return classLoaderProvider;
	}

	public ConfigurableTypeFilter getConfigurableTypeFilter() {
		return configurableTypeFilter;
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

		return componentScan(packageName, configurableTypeFilter);
	}

	public Registration componentScan(String packageName, TypeFilter typeFilter) {
		ServiceLoader<Class<?>> classesLoader = getClassScanner().scan(packageName, getClassLoader(), typeFilter);
		return getContextClasses().getServiceLoaders().register(classesLoader);
	}

	@Override
	public DefaultClassScanner getClassScanner() {
		return classScanner;
	}

	@Override
	public Services<Class<?>> getContextClasses() {
		return contextClassesLoader;
	}

	public ConfigurableServices<ContextPostProcessor> getContextPostProcessors() {
		return contextPostProcessors;
	}

	@Override
	public Services<Class<?>> getSourceClasses() {
		return sourceClasses;
	}

	@Override
	protected void _init() {
		// 扫描框架类
		componentScan(Constants.SYSTEM_PACKAGE_NAME, configurableTypeFilter);

		super._init();

		logger.debug("Start initializing context[{}]!", this);

		// 因为typeFilter可能发生变化
		contextClassesLoader.reload();

		if (!contextPostProcessors.isConfigured()) {
			contextPostProcessors.configure(this);
		}

		contextPostProcessors.postProcessContext(this);
		logger.debug("Started context[{}]!", this);
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

				String path = getProperties().replacePlaceholders(location);
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
}
