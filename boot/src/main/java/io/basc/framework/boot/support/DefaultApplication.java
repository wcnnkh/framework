package io.basc.framework.boot.support;

import java.util.OptionalInt;

import io.basc.framework.boot.Application;
import io.basc.framework.boot.ApplicationAware;
import io.basc.framework.boot.ApplicationEvent;
import io.basc.framework.boot.ApplicationException;
import io.basc.framework.boot.ApplicationPostProcessor;
import io.basc.framework.boot.ApplicationServer;
import io.basc.framework.boot.ConfigurableApplication;
import io.basc.framework.boot.annotation.ApplicationResource;
import io.basc.framework.context.annotation.ImportResource;
import io.basc.framework.context.support.DefaultContext;
import io.basc.framework.core.annotation.AnnotatedElementUtils;
import io.basc.framework.event.BroadcastEventDispatcher;
import io.basc.framework.event.support.StandardBroadcastEventDispatcher;
import io.basc.framework.factory.BeanDefinition;
import io.basc.framework.factory.ConfigurableServices;
import io.basc.framework.factory.FactoryException;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.util.SplitLine;

public class DefaultApplication extends DefaultContext implements ConfigurableApplication {
	private static final String SERVER_PORT_PROPERTY = "server.port";

	private final BroadcastEventDispatcher<ApplicationEvent> applicationEventDispathcer = new StandardBroadcastEventDispatcher<ApplicationEvent>();
	private volatile Logger logger;
	private final long createTime;
	private final ConfigurableServices<ApplicationPostProcessor> applicationPostProcessors = new ConfigurableServices<ApplicationPostProcessor>(
			ApplicationPostProcessor.class);
	private volatile boolean initialized;

	public DefaultApplication() {
		this.createTime = System.currentTimeMillis();
		// 添加默认的类
		registerSingleton(Application.class.getName(), this);
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
		super._dependence(instance, definition);
		if (instance != null && instance instanceof ApplicationAware) {
			((ApplicationAware) instance).setApplication(this);
		}
	}

	@Override
	public boolean isInitialized() {
		return super.isInitialized() && initialized;
	}

	public boolean isEnableServer() {
		return getProperties().get("application.server.enable").or(true).getAsBoolean();
	}

	public void setEnableServer(boolean enable) {
		getProperties().put("application.server.enable", enable);
	}

	public void startServer() {
		if (isInstance(ApplicationServer.class)) {
			ApplicationServer server = getInstance(ApplicationServer.class);
			try {
				server.startup(this);
			} catch (Throwable e) {
				throw new ApplicationException(e);
			}
		}
	}

	@Override
	public void destroy() {
		getLogger().info(new SplitLine("Start destroying application[{}]").toString(), this);
		super.destroy();
	}

	public boolean isAutoImportResource() {
		return getProperties().get("io.basc.framework.application.auto.import.resource").or(true).getAsBoolean();
	}

	public void setAutoImportResource(boolean autoImportResource) {
		getProperties().put("io.basc.framework.application.auto.import.resource", autoImportResource);
	}

	@Override
	public void init() {
		synchronized (this) {
			if (isInitialized()) {
				throw new ApplicationException("This application has been initialized");
			}

			try {
				if (isAutoImportResource() && !getSourceClasses().stream()
						.filter((e) -> AnnotatedElementUtils.hasAnnotation(e, ApplicationResource.class)).findAny()
						.isPresent()) {
					// 如果没有注册过资源就将默认资源注册一次，目的是为了兼容在第三方容器运行时找不到默认配置问题
					ImportResource importResource = ApplicationResource.class.getAnnotation(ImportResource.class);
					if (importResource != null) {
						for (String location : importResource.value()) {
							source(location);
						}
					}
				}

				OptionalInt port = getPort();
				if (!port.isPresent()) {
					// 兼容旧版本
					int serverPort = getProperties().get(SERVER_PORT_PROPERTY)
							.orGet(() -> Application.getAvailablePort()).getAsInt();
					setPort(serverPort);
					port = OptionalInt.of(serverPort);
				}

				super.init();

				// 启动服务器
				if (isEnableServer()) {
					startServer();
				}

				if (!applicationPostProcessors.isConfigured()) {
					applicationPostProcessors.configure(this);
				}

				for (ApplicationPostProcessor postProcessor : applicationPostProcessors) {
					postProcessApplication(postProcessor);
				}

				// 初始化所有单例
				if (isInitializeAllSingletonObjects()) {
					initializeAllSingletonObjects();
				}

				getLogger()
						.info(new SplitLine("Start up complete in " + (System.currentTimeMillis() - createTime) + "ms")
								.toString());
			} finally {
				initialized = true;
			}
		}
	}

	public boolean isInitializeAllSingletonObjects() {
		return getProperties().get("application.initialize.all.singleton.objects.enable").or(true).getAsBoolean();
	}

	public void setInitializeAllSingletonObjects(boolean enable) {
		getProperties().put("application.initialize.all.singleton.objects.enable", enable);
	}

	/**
	 * 初始化所有单例
	 */
	public void initializeAllSingletonObjects() {
		for (String id : getDefinitionIds()) {
			if (isSingleton(id) && isInstance(id)) {
				getInstance(id);
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
	public BroadcastEventDispatcher<ApplicationEvent> getEventDispatcher() {
		return applicationEventDispathcer;
	}
}
