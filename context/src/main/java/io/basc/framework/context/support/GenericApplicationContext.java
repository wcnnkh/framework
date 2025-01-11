package io.basc.framework.context.support;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import io.basc.framework.beans.BeansException;
import io.basc.framework.beans.factory.config.AutowireCapableBeanFactory;
import io.basc.framework.beans.factory.config.BeanFactoryPostProcessor;
import io.basc.framework.beans.factory.config.BeanFactoryPostProcessors;
import io.basc.framework.beans.factory.config.ConfigurableListableBeanFactory;
import io.basc.framework.beans.factory.support.DefaultBeanFactory;
import io.basc.framework.context.ApplicationContext;
import io.basc.framework.context.ApplicationContextAware;
import io.basc.framework.context.ApplicationContextEvent;
import io.basc.framework.context.config.ConfigurableApplicationContext;
import io.basc.framework.core.env.config.ConfigurableEnvironment;
import io.basc.framework.core.env.config.DefaultEnvironment;
import io.basc.framework.lang.ClassLoaderProvider;
import io.basc.framework.util.actor.EventPushException;
import io.basc.framework.util.actor.EventRegistrationException;
import io.basc.framework.util.actor.batch.BatchEventListener;
import io.basc.framework.util.actor.support.DefaultBroadcastEventDispatcher;
import io.basc.framework.util.collections.Elements;
import io.basc.framework.util.exchange.Listener;
import io.basc.framework.util.exchange.Receipt;
import io.basc.framework.util.exchange.Registration;
import io.basc.framework.util.exchange.event.BatchEventDispatcher;
import io.basc.framework.util.exchange.event.EventDispatcher;
import io.basc.framework.util.io.Resource;
import io.basc.framework.util.io.load.DefaultResourceLoader;
import io.basc.framework.util.io.load.PathMatchingResourcePatternResolver;
import io.basc.framework.util.io.load.ProtocolResolver;
import io.basc.framework.util.io.load.ResourceLoader;
import io.basc.framework.util.io.load.ResourcePatternResolver;
import io.basc.framework.util.register.DisposableRegistration;

public class GenericApplicationContext extends DefaultBeanFactory implements ConfigurableApplicationContext {
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
			return Registration.SUCCESS;
		});
		beanFactoryPostProcessors.getInjectors().register(getServiceInjectors());
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
			for (ProtocolResolver protocolResolver : defaultResourceLoader.getProtocolResolver()) {
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
					shutdownHookRegistration.cancel();
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
					shutdownHookRegistration = new DisposableRegistration(
							() -> Runtime.getRuntime().removeShutdownHook(thread));
				}
			}
		}
		return shutdownHookRegistration;
	}

	@Override
	public void addProtocolResolver(ProtocolResolver protocolResolver) {
		defaultResourceLoader.getProtocolResolver().register(protocolResolver);
	}

	private final EventDispatcher<ApplicationContextEvent> eventDispatcher = new EventDispatcher<>();

	@Override
	public Registration registerListener(Listener<ApplicationContextEvent> listener) {
		return eventDispatcher.registerListener(listener);
	}

	@Override
	public Receipt publish(ApplicationContextEvent resource) {
		return eventDispatcher.publish(resource);
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
