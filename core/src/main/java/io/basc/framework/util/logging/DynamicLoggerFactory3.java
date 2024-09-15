package io.basc.framework.util.logging;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

import io.basc.framework.util.Assert;
import io.basc.framework.util.Elements;
import io.basc.framework.util.Registration;
import io.basc.framework.util.Reloadable;
import io.basc.framework.util.event.ChangeEvent;
import lombok.NonNull;

public class DynamicLoggerFactory3 implements ILoggerFactory, Reloadable {
	private volatile LevelFactory levelFactory;
	@NonNull
	private volatile ILoggerFactory loggerFactory;
	private volatile Map<String, DynamicLogger> loggerMap = new HashMap<String, DynamicLogger>();

	private volatile Registration registration;

	public DynamicLoggerFactory3(@NonNull ILoggerFactory loggerFactory) {
		Assert.requiredArgument(loggerFactory != null, "loggerFactory");
		this.loggerFactory = loggerFactory;
	}

	public LevelFactory getLevelFactory() {
		synchronized (this) {
			return levelFactory;
		}
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

					updateLevel(name, logger);
					loggerMap.put(name, logger);
				}
			}
		}
		return logger;
	}

	public ILoggerFactory getLoggerFactory() {
		synchronized (this) {
			return loggerFactory;
		}
	}

	private void levelObserver(Elements<ChangeEvent<String>> events) {
		synchronized (this) {
			for (Entry<String, DynamicLogger> entry : loggerMap.entrySet()) {
				events.forEach((event) -> {
					if (levelFactory.match(entry.getKey(), event.getSource())) {
						updateLevel(entry.getKey(), entry.getValue());
					}
				});
			}
		}
	}

	@Override
	public void reload() {
		synchronized (this) {
			for (Entry<String, DynamicLogger> entry : loggerMap.entrySet()) {
				reload(entry.getKey(), entry.getValue());
			}
		}
	}

	private void reload(String name, DynamicLogger dynamicLogger) {
		synchronized (this) {
			if (loggerFactory != null) {
				Logger logger = loggerFactory.getLogger(name);
				if (logger != null) {
					dynamicLogger.setSource(logger);
				}
			}

			updateLevel(name, dynamicLogger);
		}
	}

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

	public void setLoggerFactory(@NonNull ILoggerFactory loggerFactory) {
		Assert.requiredArgument(loggerFactory != null, "loggerFactory");
		synchronized (this) {
			if (this.loggerFactory == loggerFactory) {
				return;
			}

			this.loggerFactory = loggerFactory;
			reload();
		}
	}

	private void updateLevel(String name, DynamicLogger dynamicLogger) {
		if (levelFactory != null) {
			Level level = levelFactory.getLevel(name);
			if (level != null) {
				dynamicLogger.setLevel(level);
			}
		}
	}
}
