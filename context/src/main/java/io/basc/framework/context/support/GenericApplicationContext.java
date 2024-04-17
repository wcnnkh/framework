package io.basc.framework.context.support;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import io.basc.framework.beans.BeansException;
import io.basc.framework.beans.factory.config.AutowireCapableBeanFactory;
import io.basc.framework.beans.factory.config.BeanFactoryPostProcessor;
import io.basc.framework.beans.factory.config.BeanFactoryPostProcessors;
import io.basc.framework.beans.factory.config.ConfigurableListableBeanFactory;
import io.basc.framework.beans.factory.support.DefaultServiceLoaderFactory;
import io.basc.framework.context.ApplicationContext;
import io.basc.framework.context.ApplicationContextAware;
import io.basc.framework.context.ApplicationContextEvent;
import io.basc.framework.context.config.ConfigurableApplicationContext;
import io.basc.framework.env.ConfigurableEnvironment;
import io.basc.framework.env.DefaultEnvironment;
import io.basc.framework.event.EventPushException;
import io.basc.framework.event.EventRegistrationException;
import io.basc.framework.event.batch.BatchEventDispatcher;
import io.basc.framework.event.batch.BatchEventListener;
import io.basc.framework.event.support.DefaultBroadcastEventDispatcher;
import io.basc.framework.io.DefaultResourceLoader;
import io.basc.framework.io.ProtocolResolver;
import io.basc.framework.io.Resource;
import io.basc.framework.io.ResourceLoader;
import io.basc.framework.io.ResourcePatternResolver;
import io.basc.framework.io.support.PathMatchingResourcePatternResolver;
import io.basc.framework.util.ClassLoaderProvider;
import io.basc.framework.util.Registration;
import io.basc.framework.util.element.Elements;

public class GenericApplicationContext extends DefaultServiceLoaderFactory implements ConfigurableApplicationContext {
	private ConfigurableEnvironment environment = new DefaultEnvironment();
	private ApplicationContext parent;
	private ClassLoaderProvider classLoaderProvider;
	private final DefaultResourceLoader defaultResourceLoader = new DefaultResourceLoader();
	private ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver(this);
	private final BeanFactoryPostProcessors beanFactoryPostProcessors = new BeanFactoryPostProcessors();
	private ResourceLoader resourceLoader;

	public GenericApplicationContext() {
		getServiceInjectors().register((bean) -> {
			if (bean instanceof ApplicationContextAware) {
				((ApplicationContextAware) bean).setApplicationContext(this);
			}
			return Registration.EMPTY;
		});
		beanFactoryPostProcessors.getServiceInjectors().register(getServiceInjectors());
	}

	public ConfigurableEnvironment getEnvironment() {
		return environment;
	}

	public ApplicationContext getParent() {
		return parent;
	}

	public void setParent(ApplicationContext parent) {
		this.parent = parent;
	}

	@Override
	public void setEnvironment(ConfigurableEnvironment environment) {
		this.environment = environment;
	}

	public ClassLoaderProvider getClassLoaderProvider() {
		return classLoaderProvider;
	}

	public void setClassLoaderProvider(ClassLoaderProvider classLoaderProvider) {
		this.classLoaderProvider = classLoaderProvider;
	}

	@Override
	public Resource[] getResources(String locationPattern) throws IOException {
		if (resourceLoader instanceof ResourcePatternResolver) {
			return ((ResourcePatternResolver) resourceLoader).getResources(locationPattern);
		}
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
		if (resourceLoader != null) {
			for (ProtocolResolver protocolResolver : defaultResourceLoader.getProtocolResolver().getServices()) {
				Resource resource = protocolResolver.resolve(location, this);
				if (resource != null) {
					return resource;
				}
			}
			return resourceLoader.getResource(location);
		}
		return defaultResourceLoader.getResource(location);
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
		for (String beanName : getBeanNames()) {
			if (isSingleton(beanName) && isFactoryBean(beanName) && containsLocalBean(beanName)) {
				getBean(beanName);
			}
		}
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

	@Override
	public void addProtocolResolver(ProtocolResolver protocolResolver) {
		defaultResourceLoader.getProtocolResolver().register(protocolResolver);
	}

	private final BatchEventDispatcher<ApplicationContextEvent> eventDispatcher = new DefaultBroadcastEventDispatcher<>();

	@Override
	public Registration registerBatchListener(BatchEventListener<ApplicationContextEvent> batchEventListener)
			throws EventRegistrationException {
		return eventDispatcher.registerBatchListener(batchEventListener);
	}

	@Override
	public void publishBatchEvent(Elements<ApplicationContextEvent> events) throws EventPushException {
		eventDispatcher.publishBatchEvent(events);
	}

	@Override
	public AutowireCapableBeanFactory getAutowireCapableBeanFactory() throws IllegalStateException {
		return this;
	}

	@Override
	public ConfigurableListableBeanFactory getBeanFactory() throws IllegalStateException {
		return this;
	}
}
