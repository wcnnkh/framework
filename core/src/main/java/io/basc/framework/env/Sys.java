package io.basc.framework.env;

import io.basc.framework.beans.factory.Scope;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.XUtils;
import io.basc.framework.value.SystemPropertyFactory;

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
			Runtime.getRuntime().addShutdownHook(new Thread(() -> env.destroySingletons()));
		}
	}

	public static Sys getEnv() {
		return env;
	}

	public Sys() {
		super(Scope.DEFAULT);
	}

	@Override
	protected void _init() {
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

		super._init();

		getProperties().registerLast(SystemPropertyFactory.INSTANCE);
	}
}
