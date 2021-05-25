package scw.boot.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import scw.beans.BeanlifeCycleEvent;
import scw.beans.BeanlifeCycleEvent.Step;
import scw.beans.ConfigurableBeanFactory;
import scw.beans.xml.XmlBeanFactory;
import scw.boot.Application;
import scw.boot.ApplicationAware;
import scw.boot.ApplicationEvent;
import scw.boot.ApplicationPostProcessor;
import scw.boot.ConfigurableApplication;
import scw.core.OrderComparator;
import scw.env.Sys;
import scw.event.EventDispatcher;
import scw.event.EventListener;
import scw.event.EventRegistration;
import scw.event.Observable;
import scw.event.support.DefaultEventDispatcher;
import scw.io.Resource;
import scw.logger.Logger;
import scw.logger.LoggerFactory;
import scw.util.SplitLine;

public class DefaultApplication extends XmlBeanFactory implements ConfigurableApplication, EventListener<BeanlifeCycleEvent> {
	private static final String APPLICATION_PREFIX = "application";
	private final EventDispatcher<ApplicationEvent> applicationEventDispathcer = new DefaultEventDispatcher<ApplicationEvent>(
			true);
	private volatile Logger logger;
	private final long createTime;
	private List<ApplicationPostProcessor> postProcessors = new ArrayList<ApplicationPostProcessor>(8);
	
	public DefaultApplication() {
		this(XmlBeanFactory.DEFAULT_CONFIG);
	}

	public DefaultApplication(String xml) {
		super(xml);
		this.createTime = Sys.currentTimeMillis();
		registerSingleton(Application.class.getName(), this);
		getLifecycleDispatcher().registerListener(this);
	}

	@Override
	public void addPostProcessor(ApplicationPostProcessor postProcessor) {
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

	public void init() throws Throwable {
		for (String suffix : new String[] { ".properties", ".yaml", ".yml" }) {
			Resource resource = getEnvironment().getResource(APPLICATION_PREFIX + suffix);
			if (resource != null && resource.exists()) {
				Observable<Properties> properties = Sys.env.toObservableProperties(resource);
				getEnvironment().loadProperties(properties);
			}
		}
		super.init();

		for (ApplicationPostProcessor postProcessor : postProcessors) {
			postProcessApplication(postProcessor);
		}

		for (ApplicationPostProcessor initializer : getBeanFactory().getServiceLoader(ApplicationPostProcessor.class)) {
			postProcessApplication(initializer);
		}

		getLogger().info(
				new SplitLine("Start up complete in " + (Sys.currentTimeMillis() - createTime) + "ms").toString());
	}
	
	public void destroy() throws Throwable{
		getLogger().info(new SplitLine("destroy").toString());
		super.destroy();
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
