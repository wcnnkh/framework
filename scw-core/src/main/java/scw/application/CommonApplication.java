package scw.application;

import scw.beans.BeanFactory;
import scw.beans.event.BeanEvent;
import scw.beans.event.BeanLifeCycleEvent;
import scw.beans.event.BeanLifeCycleEvent.Step;
import scw.beans.xml.XmlBeanFactory;
import scw.core.utils.StringUtils;
import scw.event.BasicEventDispatcher;
import scw.event.EventListener;
import scw.event.EventRegistration;
import scw.event.support.DefaultBasicEventDispatcher;
import scw.lang.AlreadyExistsException;
import scw.logger.LoggerFactory;

public class CommonApplication extends XmlBeanFactory implements Application, EventListener<BeanEvent> {
	public static final String DEFAULT_BEANS_PATH = "beans.xml";
	private volatile boolean started = false;
	private volatile BasicEventDispatcher<ApplicationEvent> applicationEventDispathcer;

	public CommonApplication(String xmlConfigPath) {
		super(StringUtils.isEmpty(xmlConfigPath) ? DEFAULT_BEANS_PATH : xmlConfigPath);
		addInternalSingleton(Application.class, this);
		getBeanEventDispatcher().registerListener(this);
	}

	public void onEvent(BeanEvent event) {
		if (event instanceof BeanLifeCycleEvent) {
			BeanLifeCycleEvent beanLifeCycleEvent = (BeanLifeCycleEvent) event;
			if (beanLifeCycleEvent.getStep() == Step.AFTER_INIT) {
				Object source = event.getSource();
				if (source != null && source instanceof ApplicationAware) {
					((ApplicationAware) source).setApplication(this);
				}
			}
		}
	}

	protected void initDefaultApplicationEventDispathcer() {
		if (applicationEventDispathcer == null) {
			synchronized (this) {
				if (applicationEventDispathcer == null) {
					applicationEventDispathcer = new DefaultBasicEventDispatcher<ApplicationEvent>(true);
				}
			}
		}
	}

	public final BasicEventDispatcher<ApplicationEvent> getApplicationEventDispathcer() {
		return applicationEventDispathcer;
	}

	public void setApplicationEventDispathcer(BasicEventDispatcher<ApplicationEvent> applicationEventDispathcer) {
		if (applicationEventDispathcer != null) {
			throw new AlreadyExistsException("ApplicationEventDispathcer");
		}
		this.applicationEventDispathcer = applicationEventDispathcer;
	}

	public BeanFactory getBeanFactory() {
		return this;
	}

	public final synchronized void init() {
		if (started) {
			throw new ApplicationException("已经启动了");
		}

		try {
			super.init();
			initInternal();
		} catch (Exception e) {
			if (e instanceof RuntimeException) {
				throw (RuntimeException) e;
			}
			throw new ApplicationException("Initialization exception", e);
		} finally {
			started = true;
		}
	}

	protected void initInternal() throws Exception {
	}

	protected void destroyInternal() throws Exception {
	}

	public final synchronized void destroy() {
		if (!started) {
			return;
		}

		try {
			try {
				destroyInternal();
			} finally {
				super.destroy();
			}
			started = false;
		} catch (Exception e) {
			if (e instanceof RuntimeException) {
				throw (RuntimeException) e;
			}
			throw new ApplicationException("Destroy exception", e);
		} finally {
			LoggerFactory.getILoggerFactory().destroy();
		}
	}

	public void publishEvent(ApplicationEvent event) {
		initDefaultApplicationEventDispathcer();
		applicationEventDispathcer.publishEvent(event);
	}

	public EventRegistration registerListener(EventListener<ApplicationEvent> eventListener) {
		initDefaultApplicationEventDispathcer();
		return applicationEventDispathcer.registerListener(eventListener);
	}
}
