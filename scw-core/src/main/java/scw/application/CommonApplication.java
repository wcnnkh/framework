package scw.application;

import scw.beans.BeanFactory;
import scw.beans.BeanLifeCycleEvent;
import scw.beans.BeanLifeCycleEvent.Step;
import scw.beans.xml.XmlBeanFactory;
import scw.core.utils.StringUtils;
import scw.event.BasicEventDispatcher;
import scw.event.EventListener;
import scw.event.EventRegistration;
import scw.event.support.DefaultBasicEventDispatcher;
import scw.lang.AlreadyExistsException;
import scw.logger.Logger;
import scw.logger.LoggerFactory;
import scw.logger.LoggerUtils;
import scw.logger.SplitLineAppend;

public class CommonApplication extends XmlBeanFactory implements Application, EventListener<BeanLifeCycleEvent> {
	public static final String DEFAULT_BEANS_PATH = "beans.xml";
	private volatile boolean initialized = false;
	private volatile BasicEventDispatcher<ApplicationEvent> applicationEventDispathcer;
	private volatile Logger logger;

	public CommonApplication() {
		this(DEFAULT_BEANS_PATH);
	}

	public CommonApplication(String xmlConfigPath) {
		super(StringUtils.isEmpty(xmlConfigPath) ? DEFAULT_BEANS_PATH : xmlConfigPath);
		addInternalSingleton(Application.class, this);
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
			if (source != null && source instanceof ApplicationAware) {
				((ApplicationAware) source).setApplication(this);
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
		if (initialized) {
			throw new ApplicationException("已经启动了");
		}

		try {
			beforeInit();
			super.init();
		} catch (Exception e) {
			if (e instanceof RuntimeException) {
				throw (RuntimeException) e;
			}
			throw new ApplicationException("Initialization exception", e);
		} finally {
			initialized = true;
		}
	}

	protected void beforeInit() throws Exception {
	}

	protected void afterDestroy() throws Exception {
	}

	public final synchronized void destroy() {
		if (!initialized) {
			return;
		}

		if (logger != null) {
			logger.info(new SplitLineAppend("destroy"));
		}

		try {
			try {
				super.destroy();
			} finally {
				afterDestroy();
			}
			initialized = false;
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

	public boolean isInitialized() {
		return initialized;
	}
}
