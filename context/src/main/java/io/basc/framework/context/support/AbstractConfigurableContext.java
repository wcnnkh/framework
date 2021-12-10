package io.basc.framework.context.support;

import io.basc.framework.context.ClassesLoader;
import io.basc.framework.context.ClassesLoaderFactory;
import io.basc.framework.context.ConfigurableClassesLoader;
import io.basc.framework.context.ConfigurableContext;
import io.basc.framework.context.annotation.AbstractProviderServiceLoaderFactory;
import io.basc.framework.context.annotation.ComponentScan;
import io.basc.framework.context.annotation.ComponentScans;
import io.basc.framework.core.type.filter.TypeFilter;
import io.basc.framework.env.ConfigurableEnvironment;
import io.basc.framework.env.DefaultEnvironment;
import io.basc.framework.factory.Configurable;
import io.basc.framework.factory.ServiceLoaderFactory;
import io.basc.framework.io.DefaultResourceLoader;
import io.basc.framework.io.FileSystemResourceLoader;
import io.basc.framework.lang.Constants;
import io.basc.framework.value.ValueFactory;

public abstract class AbstractConfigurableContext extends AbstractProviderServiceLoaderFactory
		implements ConfigurableContext, Configurable {
	private final DefaultClassesLoaderFactory classesLoaderFactory;
	private final LinkedHashSetClassesLoader sourceClasses = new LinkedHashSetClassesLoader();
	private final DefaultEnvironment environment = new DefaultEnvironment(this);
	private final DefaultClassesLoader contextClassesLoader = new DefaultClassesLoader();
	private final DefaultResourceLoader classesResourceLoader = new FileSystemResourceLoader();
	private final ContextTypeFilter contextTypeFilter = new ContextTypeFilter(environment);

	public AbstractConfigurableContext(boolean cache) {
		super(cache);
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
		environment.configure(serviceLoaderFactory);
		contextClassesLoader.configure(serviceLoaderFactory);
		classesResourceLoader.configure(serviceLoaderFactory);
	}

	@Override
	protected ClassesLoader getScanClassesLoader() {
		return contextClassesLoader;
	}

	@Override
	protected ValueFactory<String> getConfigFactory() {
		return environment;
	}

	@Override
	public ConfigurableEnvironment getEnvironment() {
		return environment;
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
}
