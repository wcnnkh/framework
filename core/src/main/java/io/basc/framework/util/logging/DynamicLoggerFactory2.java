package io.basc.framework.util.logging;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

import io.basc.framework.util.Elements;
import io.basc.framework.util.Registration;
import io.basc.framework.util.event.ChangeEvent;
import lombok.NonNull;

public class DynamicLoggerFactory2 implements ILoggerFactory {
	private volatile Map<String, DynamicLogger> loggerMap = new HashMap<String, DynamicLogger>();
	private volatile LevelFactory levelFactory;
	private volatile ILoggerFactory loggerFactory;
	private volatile DynamicLogger rootLogger;

	public DynamicLoggerFactory2(@NonNull ILoggerFactory loggerFactory) {
		this.loggerFactory = loggerFactory;
	}

	public DynamicLogger getRootLogger() {
		if (rootLogger == null) {
			synchronized (this) {
				if (rootLogger == null) {
					rootLogger = getLogger(DynamicLoggerFactory2.class.getName());
				}
			}
		}
		return rootLogger;
	}

	@Override
	public DynamicLogger getLogger(String name) {
		DynamicLogger logger = loggerMap.get(name);
		if (logger == null) {
			synchronized (this) {
				logger = loggerMap.get(name);
				if (logger == null) {
					Logger source = loggerFactory.getLogger(name);
					if (source instanceof DynamicLogger) {
						logger = (DynamicLogger) source;
					} else {
						logger = new DynamicLogger(source);
					}

					if (levelFactory != null) {
						Level level = levelFactory.getLevel(name);
						if (level != null) {
							logger.setLevel(level);
						}
					}
					loggerMap.put(name, logger);
				}
			}
		}
		return logger;
	}

	public ILoggerFactory getLoggerFactory() {
		return loggerFactory;
	}

	public void setLoggerFactory(ILoggerFactory loggerFactory) {
		synchronized (this) {
			if (this.loggerFactory == loggerFactory) {
				return;
			}

			this.loggerFactory = loggerFactory;

		}
	}

	private void resetLoggerMap() {
		ILoggerFactory old = this.loggerFactory;
		this.loggerFactory = loggerFactory;

		if (rootLogger != null) {
			rootLogger.setSource(loggerFactory.getLogger(rootLogger.getName()));
		}

		getRootLogger().info("Set logger factory [" + loggerFactory + "]");
		for (Entry<String, DynamicLogger> entry : loggerMap.entrySet()) {
			if (entry.getValue() == rootLogger) {
				continue;
			}

			String name = entry.getKey();
			Logger logger = loggerFactory.getLogger(name);
			entry.getValue().setSource(logger);
		}
	}

	public LevelFactory getLevelFactory() {
		return levelFactory;
	}

	private void levelObserver(Elements<ChangeEvent<String>> events) {
		synchronized (this) {

		}
	}

	private volatile Registration registration;

	public void setLevelFactory(LevelFactory levelFactory) {
		synchronized (this) {
			if (this.levelFactory == levelFactory) {
				return;
			}
			this.levelFactory = levelFactory;

			if (registration != null) {
				registration.cancel();
			}
			registration = levelFactory.registerListener(this::levelObserver);
		}
	}
}
