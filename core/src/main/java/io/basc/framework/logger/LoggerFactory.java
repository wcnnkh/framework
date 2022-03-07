package io.basc.framework.logger;

import java.util.logging.Handler;

public final class LoggerFactory {
	private static final DynamicLoggerFactory LOGGER_FACTORY = new DynamicLoggerFactory();
	private static final LevelManager LEVEL_MANAGER = LOGGER_FACTORY.getServiceLoaderFactory()
			.getServiceLoader(LevelManager.class).first(() -> new LevelManager());

	public static LevelManager getLevelManager() {
		return LEVEL_MANAGER;
	}

	/**
	 * 获取默认日志工厂实现<br/>
	 * 一般情况下不要使用此访求获取日志记录器，除非你知道你想做什么
	 * 
	 * @see #getLogger(String)
	 * @see #getLogger(Class)
	 * @return
	 */
	public static ILoggerFactory getLoggerFactory() {
		return LOGGER_FACTORY;
	}

	/**
	 * 获取一个日志记录器
	 * 
	 * @see ILoggerFactory
	 * @see Handler
	 * @param name
	 * @return
	 */
	public static Logger getLogger(String name) {
		return LOGGER_FACTORY.getLogger(name);
	}

	/**
	 * 获取一个日志记录器
	 * 
	 * @see #getLogger(String)
	 * @param clazz
	 * @return
	 */
	public static Logger getLogger(Class<?> clazz) {
		return getLogger(clazz.getName());
	}

	private LoggerFactory() {
		throw new UnsupportedOperationException();
	};
}
