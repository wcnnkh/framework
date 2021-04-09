package scw.boot.support;

import scw.beans.BeanLifeCycleEvent;
import scw.beans.BeanLifeCycleEvent.Step;
import scw.beans.xml.XmlBeanFactory;
import scw.boot.Application;
import scw.boot.ApplicationAware;
import scw.boot.ApplicationEvent;
import scw.boot.ApplicationPostProcessor;
import scw.boot.ConfigurableApplication;
import scw.context.ClassesLoader;
import scw.context.ConfigurableClassesLoader;
import scw.context.ConfigurableContextEnvironment;
import scw.context.support.LifecycleAuxiliary;
import scw.core.utils.ClassUtils;
import scw.core.utils.StringUtils;
import scw.env.SystemEnvironment;
import scw.event.BasicEventDispatcher;
import scw.event.EventListener;
import scw.event.EventRegistration;
import scw.event.support.DefaultBasicEventDispatcher;
import scw.logger.Logger;
import scw.logger.LoggerFactory;
import scw.logger.LoggerUtils;
import scw.logger.SplitLineAppend;
import scw.util.ClassLoaderProvider;
import scw.util.DefaultClassLoaderProvider;

public class DefaultApplication extends LifecycleAuxiliary implements
		ConfigurableApplication, EventListener<BeanLifeCycleEvent> {
	private final XmlBeanFactory beanFactory;
	private final BasicEventDispatcher<ApplicationEvent> applicationEventDispathcer = new DefaultBasicEventDispatcher<ApplicationEvent>(
			true);
	private volatile Logger logger;
	private ClassLoaderProvider classLoaderProvider;
	private final long createTime;
	private final Thread shutdown;

	public DefaultApplication() {
		this(XmlBeanFactory.DEFAULT_CONFIG);
	}

	public DefaultApplication(String xml) {
		this.createTime = System.currentTimeMillis();
		beanFactory = new XmlBeanFactory(
				StringUtils.isEmpty(xml) ? XmlBeanFactory.DEFAULT_CONFIG : xml);
		beanFactory.setClassLoaderProvider(this);
		getBeanFactory().registerSingleton(Application.class.getName(), this);
		getEnvironment().addPropertyFactory(SystemEnvironment.getInstance());
		beanFactory.registerListener(this);
		
		this.shutdown = new Thread(new Runnable() {

			public void run() {
				if (isInitialized()) {
					try {
						destroy();
					} catch (Throwable e) {
						getLogger().error(e, "destroy error");
					}
				}
			}
		}, "shutdown-" + this);
	}
	
	public void addShutdownHook() {
		Runtime.getRuntime().addShutdownHook(shutdown);
	}
	
	public long getCreateTime() {
		return createTime;
	}
	
	public void setClassLoader(ClassLoader classLoader){
		setClassLoaderProvider(new DefaultClassLoaderProvider(classLoader));
	}

	public void setClassLoaderProvider(ClassLoaderProvider classLoaderProvider) {
		this.classLoaderProvider = classLoaderProvider;
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

	@Override
	protected void beforeInit() throws Throwable {
		ApplicationUtils.config(getEnvironment());
		beanFactory.init();
	}
	
	protected void postProcessApplication(ApplicationPostProcessor processor) throws Throwable{
		processor.postProcessApplication(this);
	}

	@Override
	protected void afterInit() throws Throwable {
		for (ApplicationPostProcessor initializer : getBeanFactory()
				.getServiceLoader(ApplicationPostProcessor.class)) {
			postProcessApplication(initializer);
		}
		super.afterInit();
	}

	@Override
	protected void initComplete() throws Throwable {
		super.initComplete();
		getLogger().info(new SplitLineAppend("Start up complete in " + (System.currentTimeMillis() - createTime) + "ms"));
	}

	protected void beforeDestroy() throws Throwable {
		getLogger().info(new SplitLineAppend("destroy"));
	};

	@Override
	protected void afterDestroy() throws Throwable {
		try {
			beanFactory.destroy();
		} finally {
			LoggerFactory.getILoggerFactory().destroy();
		}
		super.afterDestroy();
	}

	public void publishEvent(ApplicationEvent event) {
		applicationEventDispathcer.publishEvent(event);
	}

	public EventRegistration registerListener(
			EventListener<ApplicationEvent> eventListener) {
		return applicationEventDispathcer.registerListener(eventListener);
	}

	public XmlBeanFactory getBeanFactory() {
		return beanFactory;
	}

	public ClassesLoader<?> getClassesLoader(String packageName) {
		return beanFactory.getClassesLoader(packageName);
	}

	public ClassLoader getClassLoader() {
		return ClassUtils.getClassLoader(classLoaderProvider);
	}

	public ConfigurableContextEnvironment getEnvironment() {
		return beanFactory.getEnvironment();
	}

	public ConfigurableClassesLoader<?> getContextClassesLoader() {
		return beanFactory.getContextClassesLoader();
	}
}
