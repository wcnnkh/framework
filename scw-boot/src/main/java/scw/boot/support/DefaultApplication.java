package scw.boot.support;

import java.util.Properties;

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
import scw.event.EventDispatcher;
import scw.event.EventListener;
import scw.event.EventRegistration;
import scw.event.Observable;
import scw.event.support.DefaultEventDispatcher;
import scw.io.Resource;
import scw.logger.Logger;
import scw.logger.LoggerFactory;
import scw.util.ClassLoaderProvider;
import scw.util.DefaultClassLoaderProvider;
import scw.util.SplitLine;

public class DefaultApplication extends LifecycleAuxiliary
		implements ConfigurableApplication, EventListener<BeanLifeCycleEvent> {
	private static final String APPLICATION_PREFIX = "application";

	private final XmlBeanFactory beanFactory;
	private final EventDispatcher<ApplicationEvent> applicationEventDispathcer = new DefaultEventDispatcher<ApplicationEvent>(
			true);
	private volatile Logger logger;
	private ClassLoaderProvider classLoaderProvider;
	private final long createTime;

	public DefaultApplication() {
		this(XmlBeanFactory.DEFAULT_CONFIG);
	}

	public DefaultApplication(String xml) {
		this.createTime = System.currentTimeMillis();
		beanFactory = new XmlBeanFactory(StringUtils.isEmpty(xml) ? XmlBeanFactory.DEFAULT_CONFIG : xml);
		beanFactory.setClassLoaderProvider(this);
		getBeanFactory().registerSingleton(Application.class.getName(), this);
		beanFactory.registerListener(this);
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setClassLoader(ClassLoader classLoader) {
		setClassLoaderProvider(new DefaultClassLoaderProvider(classLoader));
	}

	public void setClassLoaderProvider(ClassLoaderProvider classLoaderProvider) {
		this.classLoaderProvider = classLoaderProvider;
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
		for(String suffix : new String[]{".properties", ".yaml", ".yml"}){
			Resource resource = getEnvironment().getResource(APPLICATION_PREFIX + suffix);
			if(resource != null && resource.exists()){
				Observable<Properties> properties = SystemEnvironment.getInstance().toObservableProperties(resource);
				getEnvironment().loadProperties(properties);
			}
		}
		beanFactory.init();
	}

	protected void postProcessApplication(ApplicationPostProcessor processor) throws Throwable {
		processor.postProcessApplication(this);
	}

	@Override
	protected void afterInit() throws Throwable {
		for (ApplicationPostProcessor initializer : getBeanFactory().getServiceLoader(ApplicationPostProcessor.class)) {
			postProcessApplication(initializer);
		}
		super.afterInit();
	}

	@Override
	protected void initComplete() throws Throwable {
		super.initComplete();
		getLogger()
				.info(new SplitLine("Start up complete in " + (System.currentTimeMillis() - createTime) + "ms").toString());
	}

	protected void beforeDestroy() throws Throwable {
		getLogger().info(new SplitLine("destroy").toString());
	};

	@Override
	protected void afterDestroy() throws Throwable {
		beanFactory.destroy();
		super.afterDestroy();
	}

	public void publishEvent(ApplicationEvent event) {
		applicationEventDispathcer.publishEvent(event);
	}

	public EventRegistration registerListener(EventListener<ApplicationEvent> eventListener) {
		return applicationEventDispathcer.registerListener(eventListener);
	}

	public XmlBeanFactory getBeanFactory() {
		return beanFactory;
	}

	public ClassesLoader getClassesLoader(String packageName) {
		return beanFactory.getClassesLoader(packageName);
	}

	public ClassLoader getClassLoader() {
		return ClassUtils.getClassLoader(classLoaderProvider);
	}

	public ConfigurableContextEnvironment getEnvironment() {
		return beanFactory.getEnvironment();
	}

	public ConfigurableClassesLoader getContextClassesLoader() {
		return beanFactory.getContextClassesLoader();
	}
}
