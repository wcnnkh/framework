package io.basc.framework.context.support;

import io.basc.framework.context.ClassesLoader;
import io.basc.framework.context.ClassesLoaderFactory;
import io.basc.framework.context.ConfigurableClassesLoader;
import io.basc.framework.context.ConfigurableContext;
import io.basc.framework.context.annotation.AbstractProviderServiceLoaderFactory;
import io.basc.framework.context.annotation.ComponentScan;
import io.basc.framework.context.annotation.ComponentScans;
import io.basc.framework.context.locks.LockMethodInterceptor;
import io.basc.framework.context.transaction.TransactionMethodInterceptor;
import io.basc.framework.core.type.scanner.ConfigurableClassScanner;
import io.basc.framework.core.type.scanner.DefaultClassScanner;
import io.basc.framework.env.ConfigurableEnvironment;
import io.basc.framework.env.DefaultEnvironment;
import io.basc.framework.instance.Configurable;
import io.basc.framework.instance.ServiceLoaderFactory;
import io.basc.framework.lang.Constants;
import io.basc.framework.value.ValueFactory;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

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
	public ConfigurableClassesLoader getContextClasses() {
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
	public ClassesLoader getSourceClasses() {
		return new ClassesLoader() {
			
			@Override
			public void reload() {
			}
			
			@Override
			public Iterator<Class<?>> iterator() {
				return sourceClasses.iterator();
			}
		};
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
		getContextClasses().add(classesLoader);
	}
}
