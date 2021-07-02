package scw.context.support;

import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;

import scw.context.ClassesLoader;
import scw.context.ClassesLoaderFactory;
import scw.context.ConfigurableClassesLoader;
import scw.context.ConfigurableContext;
import scw.context.annotation.AbstractProviderServiceLoaderFactory;
import scw.context.annotation.ComponentScan;
import scw.context.annotation.ComponentScans;
import scw.context.locks.LockMethodInterceptor;
import scw.context.transaction.TransactionMethodInterceptor;
import scw.core.Constants;
import scw.core.type.scanner.ConfigurableClassScanner;
import scw.core.type.scanner.DefaultClassScanner;
import scw.core.utils.CollectionUtils;
import scw.env.ConfigurableEnvironment;
import scw.env.DefaultEnvironment;
import scw.instance.Configurable;
import scw.instance.ServiceLoaderFactory;
import scw.value.ValueFactory;

public abstract class AbstractConfigurableContext extends AbstractProviderServiceLoaderFactory
		implements ConfigurableContext, Configurable {
	private final DefaultClassScanner classScanner = new DefaultClassScanner();
	private final DefaultClassesLoaderFactory classesLoaderFactory;
	private final Set<Class<?>> sourceClasses = new LinkedHashSet<Class<?>>(8);
	private final DefaultClassesLoader contextClassesLoader = new DefaultClassesLoader();
	private final DefaultEnvironment environment = new DefaultEnvironment(this);

	public AbstractConfigurableContext(boolean cache) {
		super(cache);
		this.classesLoaderFactory = new DefaultClassesLoaderFactory(classScanner, cache, environment);
		// 添加默认的类
		contextClassesLoader.add(TransactionMethodInterceptor.class);
		contextClassesLoader.add(LockMethodInterceptor.class);
		componentScan(Constants.SYSTEM_PACKAGE_NAME);
	}
	
	@Override
	public void configure(ServiceLoaderFactory serviceLoaderFactory) {
		environment.configure(serviceLoaderFactory);
		contextClassesLoader.configure(serviceLoaderFactory);
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
	public ConfigurableClassesLoader getContextClassesLoader() {
		return contextClassesLoader;
	}

	@Override
	public ClassesLoaderFactory getClassesLoaderFactory() {
		return classesLoaderFactory;
	}

	@Override
	public ConfigurableClassScanner getClassScanner() {
		return classScanner;
	}

	@Override
	public Enumeration<Class<?>> getSourceClasses() {
		return CollectionUtils.toEnumeration(sourceClasses.iterator());
	}

	@Override
	public void source(Class<?> sourceClass) {
		if (!sourceClasses.add(sourceClass)) {
			throw new IllegalArgumentException("Already source " + sourceClass);
		}

		componentScan(sourceClass.getPackage().getName());
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
		ClassesLoader classesLoader = getClassesLoaderFactory().getClassesLoader(packageName);
		getContextClassesLoader().add(classesLoader);
	}
}
