package io.basc.framework.env;

import java.util.Properties;

import io.basc.framework.event.Observable;
import io.basc.framework.factory.FactoryException;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.util.Optional;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.XUtils;
import io.basc.framework.value.support.SystemPropertyFactory;

/**
 * 不使用System这个名称的原因是名称冲突
 * 
 * @author wcnnkh
 *
 */
public final class Sys extends DefaultEnvironment {
	private static Sys env = new Sys();
	private static Logger logger = LoggerFactory.getLogger(Sys.class);
	/**
	 * 为了兼容老版本
	 */
	public static final String WEB_ROOT_PROPERTY = "web.root";

	static {
		// 初始化日志等级管理器
		try {
			env.init();
		} finally {
			Runtime.getRuntime().addShutdownHook(new Thread(() -> env.destroy()));
		}

		try {
			String resourceName = Optional
					.ofNullable(env.getProperties().getAsString("io.basc.framework.logger.level.properties"))
					.orElse("/logger-level.properties");
			Observable<Properties> observable = env.getProperties(resourceName);
			LoggerFactory.getLevelManager().registerProperties(observable);
		} catch (Throwable e) {
			logger.error(e, "Initialization log level configuration exception");
		}
	}

	public static Sys getEnv() {
		return env;
	}

	private Sys() {
	}

	@Override
	public void init() throws FactoryException {
		try {
			String path = getWorkPath();
			if (path == null) {
				path = XUtils.getWebAppDirectory(getResourceLoader().getClassLoader());
				if (path != null) {
					setWorkPath(path);
					logger.info("default " + Environment.WORK_PATH_PROPERTY + " in " + path);
				}
			}

			if (StringUtils.isEmpty(getProperties().getAsString(WEB_ROOT_PROPERTY))) {
				getProperties().put(WEB_ROOT_PROPERTY, path);
			}
		} catch (Throwable e) {
			logger.error(e, "Initialization working path exception");
		}

		super.init();

		getProperties().getPropertyFactories().getFactories().addService(SystemPropertyFactory.INSTANCE);

		/**
		 * 加载配置文件
		 */
		try {
			source("system.properties");
			String resourceName = Optional.ofNullable(getProperties().getAsString("io.basc.framework.properties"))
					.orElse("/private.properties");
			source(resourceName);
		} catch (Throwable e) {
			logger.error(e, "Initialization profile exception");
		}
	}
}
