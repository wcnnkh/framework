package io.basc.framework.context.support;

import io.basc.framework.beans.factory.Scope;
import io.basc.framework.beans.factory.support.DefaultServiceLoaderFactory;
import io.basc.framework.context.ApplicationContext;
import io.basc.framework.context.ConfigurableApplicationContext;
import io.basc.framework.context.config.ApplicationContextInitializers;
import io.basc.framework.context.config.ConfigurableClassScanner;
import io.basc.framework.context.config.ConfigurableTypeFilter;
import io.basc.framework.context.config.support.DefaultClassScanner;
import io.basc.framework.env1.DefaultEnvironment;
import io.basc.framework.execution.aop.Aop;
import io.basc.framework.util.ClassLoaderProvider;
import io.basc.framework.util.registry.Registration;
import io.basc.framework.util.spi.Services;

public class GenerApplicationContext extends DefaultServiceLoaderFactory implements ConfigurableApplicationContext {
	private final DefaultEnvironment environment = new DefaultEnvironment();
	private ApplicationContext parent;
	private ClassLoaderProvider classLoaderProvider;
	private final Aop aop = new Aop();
	private final DefaultClassScanner classScanner = new DefaultClassScanner();
	private final Services<Class<?>> contextClassesLoader = new Services<>();
	private final ApplicationContextInitializers applicationContextInitializers = new ApplicationContextInitializers();
	private final ConfigurableTypeFilter configurableTypeFilter = new ConfigurableTypeFilter();
	private final Services<Class<?>> sourceClasses = new Services<Class<?>>();

	public GenerApplicationContext(Scope scope) {
		super(scope);
	}

	public DefaultEnvironment getEnvironment() {
		return environment;
	}

	public ApplicationContext getParent() {
		return parent;
	}

	public void setParent(ApplicationContext parent) {
		this.parent = parent;
		environment.setParentEnvironment(parent == null ? null : parent.getEnvironment());
	}

	public ClassLoaderProvider getClassLoaderProvider() {
		return classLoaderProvider;
	}

	public void setClassLoaderProvider(ClassLoaderProvider classLoaderProvider) {
		this.classLoaderProvider = classLoaderProvider;
	}

	@Override
	public Aop getAop() {
		return aop;
	}

	@Override
	public Registration componentScan(String packageName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Services<Class<?>> getContextClasses() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Services<Class<?>> getSourceClasses() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Registration source(Class<?> sourceClass) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ConfigurableClassScanner getClassScanner() {
		// TODO Auto-generated method stub
		return null;
	}
}
