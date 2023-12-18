package io.basc.framework.boot.support;

import java.util.OptionalInt;

import io.basc.framework.beans.factory.Scope;
import io.basc.framework.beans.factory.annotation.ImportResource;
import io.basc.framework.beans.factory.config.ConfigurableServices;
import io.basc.framework.beans.factory.config.DisposableBean;
import io.basc.framework.boot.Application;
import io.basc.framework.boot.ApplicationAware;
import io.basc.framework.boot.ApplicationEvent;
import io.basc.framework.boot.ApplicationException;
import io.basc.framework.boot.ApplicationPostProcessor;
import io.basc.framework.boot.ApplicationServer;
import io.basc.framework.boot.ConfigurableApplication;
import io.basc.framework.boot.annotation.ApplicationResource;
import io.basc.framework.boot.config.ApplicationPostProcessors;
import io.basc.framework.context.support.DefaultApplicationContext;
import io.basc.framework.core.annotation.AnnotatedElementUtils;
import io.basc.framework.event.broadcast.BroadcastEventDispatcher;
import io.basc.framework.event.broadcast.support.StandardBroadcastEventDispatcher;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.util.Registration;
import io.basc.framework.util.SplitLine;

public class DefaultApplication extends DefaultApplicationContext implements ConfigurableApplication, DisposableBean {
	private static final String SERVER_PORT_PROPERTY = "server.port";

	private final BroadcastEventDispatcher<ApplicationEvent> applicationEventDispathcer = new StandardBroadcastEventDispatcher<ApplicationEvent>();
	private volatile Logger logger;
	private final long createTime;
	private final ApplicationPostProcessors applicationPostProcessors = new ApplicationPostProcessors();
	private volatile boolean initialized;

	public DefaultApplication(Scope scope) {
		super(scope);
		this.createTime = System.currentTimeMillis();
		// 添加默认的类
		registerSingleton(Application.class.getName(), this);
		applicationPostProcessors.getServiceInjectors().register(getServiceInjectors());
		getServiceInjectors().register((bean) -> {
			if (bean instanceof ApplicationAware) {
				((ApplicationAware) bean).setApplication(this);
			}
			return Registration.EMPTY;
		});
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
		if (!getBeanNamesForType(ApplicationServer.class).isEmpty()) {
			for (ApplicationServer server : getBeansOfType(ApplicationServer.class).values()) {
				try {
					server.startup(this);
				} catch (Throwable e) {
					throw new ApplicationException(e);
				}
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
	protected void _init() {
		if (isAutoImportResource() && !getSourceClasses().getServices().stream()
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
			int serverPort = getProperties().get(SERVER_PORT_PROPERTY).orGet(() -> Application.getAvailablePort())
					.getAsInt();
			setPort(serverPort);
			port = OptionalInt.of(serverPort);
		}

		super._init();

		// 启动服务器
		if (isEnableServer()) {
			startServer();
		}

		if (!applicationPostProcessors.isConfigured()) {
			applicationPostProcessors.configure(this);
		}

		applicationPostProcessors.postProcessApplication(this);

		// 初始化所有单例
		if (isInitializeAllSingletonObjects()) {
			initializeAllSingletonObjects();
		}

		getLogger().info(
				new SplitLine("Start up complete in " + (System.currentTimeMillis() - createTime) + "ms").toString());
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
		for (String id : getBeanNames()) {
			if (containsLocalBean(id) && isSingleton(id)) {
				getBean(id);
			}
		}
	}

	public ConfigurableServices<ApplicationPostProcessor> getApplicationPostProcessors() {
		return applicationPostProcessors;
	}

	@Override
	public BroadcastEventDispatcher<ApplicationEvent> getEventDispatcher() {
		return applicationEventDispathcer;
	}
}
