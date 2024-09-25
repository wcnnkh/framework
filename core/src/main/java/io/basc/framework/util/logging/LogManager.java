package io.basc.framework.util.logging;

import io.basc.framework.util.Assert;

public class LogManager {
	public static final JdkLoggerFactory GLOBA_LOGGER_FACTORY = new JdkLoggerFactory();
	private static DynamicLoggerFactory loggerFactory = new DynamicLoggerFactory(GLOBA_LOGGER_FACTORY);
	private static LevelManager levelManager = new LevelManager();

	static {
		loggerFactory.doNativeConfigure();
	}

	public static Logger getLogger(String name) {
		Assert.requiredArgument(name != null, "name");
		loggerFactory.doNativeConfigure();
		return loggerFactory.getLogger(name);
	}

	public static Logger getLogger(Class<?> clazz) {
		Assert.requiredArgument(clazz != null, "clazz");
		loggerFactory.doNativeConfigure();
		return loggerFactory.getLogger(clazz.getName());
	}
}
