package io.basc.framework.env;

import java.util.Properties;

import io.basc.framework.event.Observable;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.XUtils;
import io.basc.framework.value.support.SystemPropertyFactory;

/**
 * 不使用System这个名称的原因是名称冲突
 * 
 * @author shuchaowen
 *
 */
public final class Sys extends DefaultEnvironment {
	private static Sys env = new Sys();

	private static DefaultEnvironment environment = new DefaultEnvironment();

	private static Logger logger = LoggerFactory.getLogger(Sys.class);

	/**
	 * 为了兼容老版本
	 */
	public static final String WEB_ROOT_PROPERTY = "web.root";

	public static Sys getEnv() {
		return env;
	}

	{
		environment.getProperties().getTandemFactories().addService(SystemPropertyFactory.INSTANCE);
		String path = environment.getWorkPath();
		if (path == null) {
			path = XUtils.getWebAppDirectory(environment.getResourceLoader().getClassLoader());
			if (path != null) {
				environment.setWorkPath(path);
				logger.info("default " + Environment.WORK_PATH_PROPERTY + " in " + path);
			}
		}

		if (StringUtils.isEmpty(environment.getProperties().getString(WEB_ROOT_PROPERTY))) {
			environment.getProperties().put(WEB_ROOT_PROPERTY, path);
		}

		/**
		 * 加载配置文件
		 */
		environment.loadProperties("system.properties");
		environment.loadProperties(environment.getProperties().getValue("io.basc.framework.properties", String.class,
				"/private.properties"));

		// 初始化日志等级管理器
		Observable<Properties> observable = environment.getProperties(environment.getProperties()
				.getValue("io.basc.framework.logger.level.properties", String.class, "/logger-level.properties"));
		LoggerFactory.getLevelManager().combine(observable);

		/**
		 * 加载默认服务
		 */
		environment.configure(env);
	}

	private Sys() {
	}
}
