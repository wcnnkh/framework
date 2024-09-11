package io.basc.framework.util.logging;

import io.basc.framework.util.Reloadable;
import io.basc.framework.util.spi.NativeServiceLoader;

public class LogManager implements ILoggerFactory, Reloadable {
	public static final JdkLoggerFactory GLOBA_LOGGER_FACTORY = new JdkLoggerFactory();
	private final DynamicLoggerFactory3 dynamicLoggerFactory = new DynamicLoggerFactory3(GLOBA_LOGGER_FACTORY);
	private final LevelManager levelManager = new LevelManager();
	private volatile ILoggerFactory loggerFactory;

	public LogManager() {
		dynamicLoggerFactory.setLevelFactory(levelManager);
	}

	public LevelManager getLevelManager() {
		return levelManager;
	}

	@Override
	public Logger getLogger(String name) {
		getLoggerFactory();
		return dynamicLoggerFactory.getLogger(name);
	}

	public ILoggerFactory getLoggerFactory() {
		if (loggerFactory == null) {
			synchronized (this) {
				if (loggerFactory == null) {
					loggerFactory = loadLoggerFactory();
					reloadLoggerFactory();
				}
			}
		}
		return loggerFactory;
	}

	public void setLoggerFactory(ILoggerFactory loggerFactory) {
		this.loggerFactory = loggerFactory;
		dynamicLoggerFactory.setLoggerFactory(loggerFactory);
	}

	public ILoggerFactory loadLoggerFactory() throws Throwable {
		return NativeServiceLoader.load(ILoggerFactory.class).first();
	}

	@Override
	public void reload() {

	}
}
