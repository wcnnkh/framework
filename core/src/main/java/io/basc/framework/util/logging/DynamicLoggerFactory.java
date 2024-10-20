package io.basc.framework.util.logging;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;
import java.util.logging.Level;

import io.basc.framework.util.Assert;
import lombok.NonNull;

public class DynamicLoggerFactory implements LoggerFactory {
	private volatile LevelFactory levelFactory;
	@NonNull
	private volatile LoggerFactory loggerFactory;
	private volatile Map<String, DynamicLogger> loggerMap = new HashMap<String, DynamicLogger>();

	public DynamicLoggerFactory(@NonNull LoggerFactory loggerFactory) {
		Assert.requiredArgument(loggerFactory != null, "loggerFactory");
		this.loggerFactory = loggerFactory;
	}

	public LevelFactory getLevelFactory() {
		return levelFactory;
	}

	@Override
	public DynamicLogger getLogger(String name) {
		return getDynamicLogger(name, true);
	}

	protected Logger getSource(String name) {
		return loggerFactory.getLogger(name);
	}

	protected Level getLevel(String name) {
		if (levelFactory == null) {
			return null;
		}

		return levelFactory.getLevel(name);
	}

	public DynamicLogger getDynamicLogger(String name, boolean create) {
		DynamicLogger logger = loggerMap.get(name);
		if (logger == null && create) {
			synchronized (this) {
				logger = loggerMap.get(name);
				if (logger == null && create) {
					Logger source = getSource(name);
					if (source instanceof DynamicLogger) {
						logger = (DynamicLogger) source;
					} else {
						logger = new DynamicLogger(source);
					}

					Level level = getLevel(name);
					if (level != null) {
						logger.setLevel(level);
					}
					loggerMap.put(name, logger);
				}
			}
		}
		return logger;
	}

	public LoggerFactory getLoggerFactory() {
		synchronized (this) {
			return loggerFactory;
		}
	}

	public void setLogger(String name, Logger source) {
		synchronized (this) {
			DynamicLogger logger = getLogger(name);
			logger.setSource(source);
		}
	}

	public void setLevel(String name, Level level) {
		synchronized (this) {
			DynamicLogger logger = getLogger(name);
			logger.setLevel(level);
		}
	}

	public boolean update(@NonNull String name, @NonNull Predicate<? super DynamicLogger> consumer) {
		synchronized (this) {
			DynamicLogger logger = getDynamicLogger(name, false);
			if (logger == null) {
				// 还没有初始化可以忽略
				return false;
			}

			return consumer.test(logger);
		}
	}

	public boolean updateLevel(@NonNull String name, @NonNull LevelFactory levelFactory) {
		return update(name, (logger) -> {
			Level level = levelFactory.getLevel(name);
			logger.setLevel(level);
			return true;
		});
	}

	public boolean updateLogger(@NonNull String name, @NonNull LoggerFactory loggerFactory) {
		return update(name, (logger) -> {
			Logger source = loggerFactory.getLogger(name);
			if (source == null) {
				return false;
			}

			logger.setSource(source);
			return true;
		});
	}

	public void setLevelFactory(@NonNull LevelFactory levelFactory) {
		synchronized (this) {
			if (this.levelFactory == levelFactory) {
				return;
			}

			this.levelFactory = levelFactory;
			for (Entry<String, DynamicLogger> entry : this.loggerMap.entrySet()) {
				updateLevel(entry.getKey(), levelFactory);
			}
		}
	}

	public void setLoggerFactory(@NonNull LoggerFactory loggerFactory) {
		Assert.requiredArgument(loggerFactory != null, "loggerFactory");
		synchronized (this) {
			if (this.loggerFactory == loggerFactory) {
				return;
			}

			this.loggerFactory = loggerFactory;
			for (Entry<String, DynamicLogger> entry : this.loggerMap.entrySet()) {
				updateLogger(entry.getKey(), loggerFactory);
			}
		}
	}
}