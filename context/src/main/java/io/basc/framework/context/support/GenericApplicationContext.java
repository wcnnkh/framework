package io.basc.framework.context.support;

import java.io.IOException;

import io.basc.framework.beans.factory.Scope;
import io.basc.framework.beans.factory.support.DefaultServiceLoaderFactory;
import io.basc.framework.context.ApplicationContext;
import io.basc.framework.context.ConfigurableApplicationContext;
import io.basc.framework.context.config.ApplicationContextInitializers;
import io.basc.framework.env1.support.DefaultEnvironment;
import io.basc.framework.execution.aop.Aop;
import io.basc.framework.io.Resource;
import io.basc.framework.io.ResourcePatternResolver;
import io.basc.framework.io.resolver.ConfigurablePropertiesResolver;
import io.basc.framework.util.ClassLoaderProvider;
import io.basc.framework.util.registry.Registration;

public class GenericApplicationContext extends DefaultServiceLoaderFactory implements ConfigurableApplicationContext {
	private final DefaultEnvironment environment = new DefaultEnvironment();
	private ApplicationContext parent;
	private ClassLoaderProvider classLoaderProvider;
	private final Aop aop = new Aop();
	private final ApplicationContextInitializers applicationContextInitializers = new ApplicationContextInitializers();
	private final ConfigurablePropertiesResolver propertiesResolver = new ConfigurablePropertiesResolver();
	private ResourcePatternResolver resourcePatternResolver;

	public GenericApplicationContext(Scope scope) {
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
	public ConfigurablePropertiesResolver getPropertiesResolver() {
		return propertiesResolver;
	}

	@Override
	public Resource[] getResources(String locationPattern) throws IOException {
		return resourcePatternResolver.getResources(locationPattern);
	}

	@Override
	public Resource getResource(String location) {
		return resourcePatternResolver.getResource(location);
	}

	@Override
	public void start() {
		applicationContextInitializers.initialize(this);
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isRunning() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Registration registerShutdownHook() {
		// TODO Auto-generated method stub
		return null;
	}
}
