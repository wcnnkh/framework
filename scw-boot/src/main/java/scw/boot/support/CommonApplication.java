package scw.boot.support;

import scw.beans.BeanLifeCycleEvent;
import scw.beans.BeanLifeCycleEvent.Step;
import scw.beans.xml.XmlBeanFactory;
import scw.boot.Application;
import scw.boot.ApplicationAware;
import scw.boot.ApplicationEvent;
import scw.boot.ApplicationPostProcessor;
import scw.boot.ConfigurableApplication;
import scw.core.utils.StringUtils;
import scw.env.SystemEnvironment;
import scw.event.BasicEventDispatcher;
import scw.event.EventListener;
import scw.event.EventRegistration;
import scw.event.support.DefaultBasicEventDispatcher;
import scw.lang.AlreadyExistsException;
import scw.logger.Logger;
import scw.logger.LoggerFactory;
import scw.logger.LoggerUtils;
import scw.logger.SplitLineAppend;

public class CommonApplication extends XmlBeanFactory implements
		ConfigurableApplication, EventListener<BeanLifeCycleEvent> {
	private volatile BasicEventDispatcher<ApplicationEvent> applicationEventDispathcer;
	private volatile Logger logger;

	public CommonApplication() {
		this(DEFAULT_CONFIG);
	}

	public CommonApplication(String xml) {
		super(StringUtils.isEmpty(xml) ? DEFAULT_CONFIG : xml);
		getBeanFactory().registerSingletion(Application.class.getName(), this);
		getEnvironment().addPropertyFactory(SystemEnvironment.getInstance());
		getBeanLifeCycleEventDispatcher().registerListener(this);
	}

	public Logger getLogger() {
		if (logger == null) {
			synchronized (this) {
				if (logger == null) {
					logger = LoggerUtils.getLogger(getClass());
				}
			}
		}
		return logger;
	}

	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	public void onEvent(BeanLifeCycleEvent event) {
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

	protected void initDefaultApplicationEventDispathcer() {
		if (applicationEventDispathcer == null) {
			synchronized (this) {
				if (applicationEventDispathcer == null) {
					applicationEventDispathcer = new DefaultBasicEventDispatcher<ApplicationEvent>(
							true);
				}
			}
		}
	}

	public final BasicEventDispatcher<ApplicationEvent> getApplicationEventDispathcer() {
		return applicationEventDispathcer;
	}

	public void setApplicationEventDispathcer(
			BasicEventDispatcher<ApplicationEvent> applicationEventDispathcer) {
		if (applicationEventDispathcer != null) {
			throw new AlreadyExistsException("ApplicationEventDispathcer");
		}
		this.applicationEventDispathcer = applicationEventDispathcer;
	}

	@Override
	public void init() throws Throwable {
		super.init();
		getLogger().info(new SplitLineAppend("Start up complete"));
	}

	@Override
	public void destroy() throws Throwable {
		try {
			super.destroy();
		} finally {
			LoggerFactory.getILoggerFactory().destroy();
		}
	}

	@Override
	public void afterInit() throws Throwable {
		for (ApplicationPostProcessor initializer : getBeanFactory()
				.getServiceLoader(ApplicationPostProcessor.class)) {
			initializer.postProcessApplication(this);
		}
		super.afterInit();
	}

	@Override
	public void beforeDestroy() throws Throwable {
		getLogger().info(new SplitLineAppend("destroy"));
		ApplicationUtils.config(getEnvironment());
		super.beforeDestroy();
	}

	public void publishEvent(ApplicationEvent event) {
		initDefaultApplicationEventDispathcer();
		applicationEventDispathcer.publishEvent(event);
	}

	public EventRegistration registerListener(
			EventListener<ApplicationEvent> eventListener) {
		initDefaultApplicationEventDispathcer();
		return applicationEventDispathcer.registerListener(eventListener);
	}

	public XmlBeanFactory getBeanFactory() {
		return this;
	}
}
