package io.basc.framework.context.support;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import io.basc.framework.beans.BeansException;
import io.basc.framework.beans.factory.Scope;
import io.basc.framework.beans.factory.config.BeanFactoryPostProcessor;
import io.basc.framework.beans.factory.config.BeanFactoryPostProcessors;
import io.basc.framework.beans.factory.support.DefaultServiceLoaderFactory;
import io.basc.framework.context.ApplicationContext;
import io.basc.framework.context.ApplicationContextAware;
import io.basc.framework.context.ConfigurableApplicationContext;
import io.basc.framework.env1.support.DefaultEnvironment;
import io.basc.framework.io.Resource;
import io.basc.framework.io.ResourcePatternResolver;
import io.basc.framework.io.resolver.ConfigurablePropertiesResolver;
import io.basc.framework.util.ClassLoaderProvider;
import io.basc.framework.util.registry.Registration;

public class GenericApplicationContext extends DefaultServiceLoaderFactory implements ConfigurableApplicationContext {
	private final DefaultEnvironment environment = new DefaultEnvironment();
	private ApplicationContext parent;
	private ClassLoaderProvider classLoaderProvider;
	private final ConfigurablePropertiesResolver propertiesResolver = new ConfigurablePropertiesResolver();
	private ResourcePatternResolver resourcePatternResolver;
	private final BeanFactoryPostProcessors beanFactoryPostProcessors = new BeanFactoryPostProcessors();

	public GenericApplicationContext(Scope scope) {
		super(scope);
		getServiceInjectors().register((bean) -> {
			if (bean instanceof ApplicationContextAware) {
				((ApplicationContextAware) bean).setApplicationContext(this);
			}
			return Registration.EMPTY;
		});
		beanFactoryPostProcessors.getServiceInjectors().register(getServiceInjectors());
		propertiesResolver.getServiceInjectors().register(getServiceInjectors());
		environment.getServiceInjectors().register(getServiceInjectors());
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
	public ConfigurablePropertiesResolver getPropertiesResolver() {
		return propertiesResolver;
	}

	@Override
	public Resource[] getResources(String locationPattern) throws IOException {
		return resourcePatternResolver.getResources(locationPattern);
	}

	@Override
	public void addBeanFactoryPostProcessor(BeanFactoryPostProcessor beanFactoryPostProcessor) {
		beanFactoryPostProcessors.register(beanFactoryPostProcessor);
	}

	public BeanFactoryPostProcessors getBeanFactoryPostProcessors() {
		return beanFactoryPostProcessors;
	}

	@Override
	public Resource getResource(String location) {
		return resourcePatternResolver.getResource(location);
	}

	private AtomicBoolean running = new AtomicBoolean();
	private AtomicBoolean active = new AtomicBoolean();

	@Override
	public boolean isRunning() {
		return running.get();
	}

	@Override
	public boolean isActive() {
		return active.get();
	}

	@Override
	public void refresh() throws BeansException, IllegalStateException {
		// 1.先执行销毁
		stop();
		if (active.compareAndSet(false, true)) {
			onClose();
			onRefresh();
		}
		start();
	}

	protected void onRefresh() {
		beanFactoryPostProcessors.postProcessBeanFactory(this);
	}

	protected void onClose() {
		clearAllAlias();
		clearFactoryBeans();
	}

	@Override
	public void close() {
		stop();
		if (active.compareAndSet(true, false)) {
			onClose();
		}

		if (shutdownHookRegistration != null) {
			synchronized (this) {
				if (shutdownHookRegistration != null) {
					shutdownHookRegistration.unregister();
					shutdownHookRegistration = null;
				}
			}
		}
	}

	@Override
	public void start() throws BeansException {
		boolean canStart = running.compareAndSet(false, true);
		if (canStart) {
			ContextLoader.setApplicationContext(this);
		}

		if (active.compareAndSet(false, true)) {
			onRefresh();
		}

		if (canStart) {
			onStart();
		}
	}

	protected void onStart() {
		// 初始化单例
		prepareAllSingletons();
	}

	protected void onStop() {
		destroySingletons();
	}

	@Override
	public void stop() throws BeansException {
		boolean canStop = running.compareAndSet(true, false);
		if (canStop) {
			onStop();
			ContextLoader.removeApplicationContext(this);
		}
	}

	private volatile Registration shutdownHookRegistration;

	@Override
	public Registration registerShutdownHook() {
		if (shutdownHookRegistration == null) {
			synchronized (this) {
				if (shutdownHookRegistration == null) {
					Thread thread = new Thread(() -> close());
					thread.setContextClassLoader(getClassLoader());
					Runtime.getRuntime().addShutdownHook(thread);
					shutdownHookRegistration = () -> Runtime.getRuntime().removeShutdownHook(thread);
					shutdownHookRegistration = shutdownHookRegistration.disposable();
				}
			}
		}
		return shutdownHookRegistration;
	}
}
