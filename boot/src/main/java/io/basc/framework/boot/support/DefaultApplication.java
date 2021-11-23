package io.basc.framework.boot.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import io.basc.framework.beans.BeanlifeCycleEvent;
import io.basc.framework.beans.BeanlifeCycleEvent.Step;
import io.basc.framework.beans.ConfigurableBeanFactory;
import io.basc.framework.beans.xml.XmlBeanFactory;
import io.basc.framework.boot.Application;
import io.basc.framework.boot.ApplicationAware;
import io.basc.framework.boot.ApplicationEvent;
import io.basc.framework.boot.ApplicationException;
import io.basc.framework.boot.ApplicationPostProcessor;
import io.basc.framework.boot.ConfigurableApplication;
import io.basc.framework.core.OrderComparator;
import io.basc.framework.env.Sys;
import io.basc.framework.event.EventDispatcher;
import io.basc.framework.event.EventListener;
import io.basc.framework.event.EventRegistration;
import io.basc.framework.event.Observable;
import io.basc.framework.event.support.SimpleEventDispatcher;
import io.basc.framework.io.Resource;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.util.SplitLine;

public class DefaultApplication extends XmlBeanFactory
		implements ConfigurableApplication, EventListener<BeanlifeCycleEvent> {
	private static final String APPLICATION_PREFIX_CONFIGURATION = "io.basc.framework.application";
	private static final String APPLICATION_PREFIX = "application";
	private final EventDispatcher<ApplicationEvent> applicationEventDispathcer = new SimpleEventDispatcher<ApplicationEvent>(
			true);
	private volatile Logger logger;
	private final long createTime;
	private List<ApplicationPostProcessor> postProcessors = new ArrayList<ApplicationPostProcessor>(8);
	private volatile boolean initialized;

	public DefaultApplication() {
		this.createTime = Sys.currentTimeMillis();
		registerSingleton(Application.class.getName(), this);
		getLifecycleDispatcher().registerListener(this);
	}

	public void addPostProcessor(ApplicationPostProcessor postProcessor) {
		if (initialized) {
			throwInitializedApplicationException();
		}

		synchronized (postProcessors) {
			postProcessors.add(postProcessor);
			Collections.sort(postProcessors, OrderComparator.INSTANCE);
		}
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

	public void onEvent(BeanlifeCycleEvent event) {
		if (event.getStep() == Step.AFTER_INIT) {
			Object source = event.getSource();
			if (source == null) {
				return;
			}

			if (source instanceof ApplicationAware) {
				((ApplicationAware) source).setApplication(this);
			}
		}
	}

	protected void throwInitializedApplicationException() {
		throw new ApplicationException("This application has been initialized");
	}

	public void init() throws Throwable {
		synchronized (this) {
			if (initialized) {
				throwInitializedApplicationException();
			}

			String applicationConfiguration = getEnvironment().getValue(APPLICATION_PREFIX_CONFIGURATION, String.class,
					APPLICATION_PREFIX);
			for (String suffix : new String[] { ".properties", ".yaml", ".yml" }) {
				Resource resource = getEnvironment().getResource(applicationConfiguration + suffix);
				if (resource != null && resource.exists()) {
					getLogger().info("Configure application resource: {}", resource);
					Observable<Properties> properties = Sys.env.toObservableProperties(resource);
					getEnvironment().loadProperties(properties);
				}
			}

			super.init();

			for (ApplicationPostProcessor initializer : getBeanFactory()
					.getServiceLoader(ApplicationPostProcessor.class)) {
				postProcessApplication(initializer);
			}

			for (ApplicationPostProcessor postProcessor : postProcessors) {
				postProcessApplication(postProcessor);
			}

			getLogger().info(
					new SplitLine("Start up complete in " + (Sys.currentTimeMillis() - createTime) + "ms").toString());
			initialized = true;
		}
	}

	public void destroy() throws Throwable {
		synchronized (this) {
			getLogger().info(new SplitLine("destroy").toString());
			super.destroy();
			initialized = false;
		}
	}

	protected void postProcessApplication(ApplicationPostProcessor processor) throws Throwable {
		processor.postProcessApplication(this);
	}

	public void publishEvent(ApplicationEvent event) {
		applicationEventDispathcer.publishEvent(event);
	}

	public EventRegistration registerListener(EventListener<ApplicationEvent> eventListener) {
		return applicationEventDispathcer.registerListener(eventListener);
	}

	public ConfigurableBeanFactory getBeanFactory() {
		return this;
	}
}
