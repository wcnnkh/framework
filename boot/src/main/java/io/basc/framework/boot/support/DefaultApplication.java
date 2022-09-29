package io.basc.framework.boot.support;

import io.basc.framework.boot.Application;
import io.basc.framework.boot.ApplicationAware;
import io.basc.framework.boot.ApplicationEvent;
import io.basc.framework.boot.ApplicationException;
import io.basc.framework.boot.ApplicationPostProcessor;
import io.basc.framework.boot.ConfigurableApplication;
import io.basc.framework.boot.annotation.ComponentScan;
import io.basc.framework.boot.annotation.ComponentScans;
import io.basc.framework.context.support.DefaultContext;
import io.basc.framework.context.support.LinkedHashSetClassesLoader;
import io.basc.framework.event.EventDispatcher;
import io.basc.framework.event.support.SimpleEventDispatcher;
import io.basc.framework.factory.BeanDefinition;
import io.basc.framework.factory.ConfigurableServices;
import io.basc.framework.factory.FactoryException;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.util.SplitLine;

public class DefaultApplication extends DefaultContext implements ConfigurableApplication {
	private static final String APPLICATION_PREFIX_CONFIGURATION = "io.basc.framework.application.configuration";
	private static final String APPLICATION_PREFIX = "application";
	private final EventDispatcher<ApplicationEvent> applicationEventDispathcer = new SimpleEventDispatcher<ApplicationEvent>();
	private volatile Logger logger;
	private final long createTime;
	private final ConfigurableServices<ApplicationPostProcessor> applicationPostProcessors = new ConfigurableServices<ApplicationPostProcessor>(
			ApplicationPostProcessor.class);
	private volatile boolean initialized;
	private final LinkedHashSetClassesLoader sourceClasses = new LinkedHashSetClassesLoader();

	public DefaultApplication() {
		// 添加默认的类
		getContextClasses().add(sourceClasses);
		this.createTime = System.currentTimeMillis();
		registerSingleton(Application.class.getName(), this);
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

	public long getCreateTime() {
		return createTime;
	}

	public Logger getLogger() {
		if (logger == null) {
			synchronized (this) {
				if (logger == null) {
					logger = LoggerFactory.getLogger(getClass());
				}
			}
		}
		return logger;
	}

	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	@Override
	protected void _dependence(Object instance, BeanDefinition definition) throws FactoryException {
		if (instance != null && instance instanceof ApplicationAware) {
			((ApplicationAware) instance).setApplication(this);
		}
		super._dependence(instance, definition);
	}

	@Override
	public boolean isInitialized() {
		return super.isInitialized() && initialized;
	}

	@Override
	public void init() {
		synchronized (this) {
			if (isInitialized()) {
				throw new ApplicationException("This application has been initialized");
			}

			try {
				String applicationConfiguration = getProperties().getValue(APPLICATION_PREFIX_CONFIGURATION,
						String.class, APPLICATION_PREFIX);
				for (String suffix : new String[] { ".properties", ".yaml", ".yml" }) {
					String configPath = applicationConfiguration + suffix;
					if (getResourceLoader().exists(configPath)) {
						getLogger().info("Configure application resource: {}", configPath);
						loadProperties(configPath);
					}
				}
				
				super.init();

				if (!applicationPostProcessors.isConfigured()) {
					applicationPostProcessors.configure(this);
				}

				for (ApplicationPostProcessor postProcessor : applicationPostProcessors) {
					postProcessApplication(postProcessor);
				}

				getLogger()
						.info(new SplitLine("Start up complete in " + (System.currentTimeMillis() - createTime) + "ms")
								.toString());
			} finally {
				initialized = true;
			}
		}
	}

	public ConfigurableServices<ApplicationPostProcessor> getApplicationPostProcessors() {
		return applicationPostProcessors;
	}

	protected void postProcessApplication(ApplicationPostProcessor processor) {
		try {
			processor.postProcessApplication(this);
		} catch (Throwable e) {
			throw new ApplicationException("Post process application[" + processor + "]", e);
		}
	}

	@Override
	public EventDispatcher<ApplicationEvent> getEventDispatcher() {
		return applicationEventDispathcer;
	}
}
