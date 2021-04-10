package scw.context.support;

import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;

import scw.context.ClassesLoader;
import scw.context.ConfigurableClassesLoader;
import scw.context.ConfigurableContextEnvironment;
import scw.context.annotation.ComponentScan;
import scw.context.annotation.ComponentScans;
import scw.core.type.filter.TypeFilter;
import scw.core.type.scanner.ClassScanner;
import scw.core.utils.CollectionUtils;
import scw.env.support.DefaultEnvironment;
import scw.instance.NoArgsInstanceFactory;
import scw.instance.ServiceLoader;

public class DefaultContextEnvironment extends DefaultEnvironment implements ConfigurableContextEnvironment {
	private final DefaultProviderLoaderFactory loaderFactory;
	private final Set<Class<?>> sourceClasses = new LinkedHashSet<Class<?>>(8);

	public DefaultContextEnvironment(boolean concurrent, boolean cache, NoArgsInstanceFactory instanceFactory) {
		super(concurrent);
		this.loaderFactory = new DefaultProviderLoaderFactory(concurrent, false, this, instanceFactory);
	}

	@Override
	public ConfigurableClassesLoader<?> getContextClassesLoader() {
		return loaderFactory.getContextClassesLoader();
	}

	@Override
	public ClassesLoader<?> getClassesLoader(String packageName) {
		return loaderFactory.getClassesLoader(packageName);
	}

	@Override
	public <S> ServiceLoader<S> getServiceLoader(Class<S> serviceClass) {
		return loaderFactory.getServiceLoader(serviceClass);
	}

	@Override
	public Enumeration<Class<?>> getSourceClasses() {
		return CollectionUtils.toEnumeration(sourceClasses.iterator());
	}

	@Override
	public void source(Class<?> sourceClass) {
		if(!sourceClasses.add(sourceClass)) {
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

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void componentScan(String packageName) {
		getContextClassesLoader().add((ClassesLoader) getClassesLoader(packageName));
	}

	@Override
	public void addClassScanner(ClassScanner classScanner) {
		loaderFactory.addClassScanner(classScanner);
	}

	@Override
	public Set<Class<?>> getClasses(String packageName, ClassLoader classLoader, TypeFilter typeFilter) {
		return loaderFactory.getClasses(packageName, classLoader, typeFilter);
	}
}
