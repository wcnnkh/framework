package io.basc.framework.util.logging;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

import io.basc.framework.util.Assert;
import io.basc.framework.util.Elements;
import io.basc.framework.util.KeyValue;
import io.basc.framework.util.event.EventListener;
import io.basc.framework.util.function.ConsumeProcessor;
import io.basc.framework.util.observe.event.ChangeEvent;

public class LoggerManager implements ILoggerFactory, EventListener<Elements<ChangeEvent<KeyValue<String, Level>>>> {
	private LevelManager levelManager = new LevelManager(this::onEvent);
	private volatile Map<String, DynamicLogger> loggerMap = new HashMap<String, DynamicLogger>();
	private ILoggerFactory loggerFactory;

	@Override
	public void onEvent(Elements<ChangeEvent<KeyValue<String, Level>>> events) {
		ConsumeProcessor.consumeAll(events, (event) -> touchLoggerName(event.getSource().getKey()));
	}

	private void touchLoggerName(String key) {
		for (Entry<String, DynamicLogger> entry : loggerMap.entrySet()) {
			if (levelManager.getNameMatcher().match(key, entry.getKey())) {
				// 匹配上了
				entry.getValue().onEvent(levelManager);
			}
		}
	}

	public LevelManager getLevelManager() {
		return levelManager;
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

					Level level = levelManager.getLevel(name);
					if (level != null) {
						logger.setLevel(level);
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

	/**
	 * 设置日志工厂
	 * 
	 * @param loggerFactory
	 * @return 旧的日志工厂
	 */
	public ILoggerFactory setLoggerFactory(ILoggerFactory loggerFactory) {
		Assert.requiredArgument(loggerFactory != null, "loggerFactory");
		if (loggerFactory == this.loggerFactory) {
			return this.loggerFactory;
		}

		synchronized (this) {
			ILoggerFactory old = this.loggerFactory;
			this.loggerFactory = loggerFactory;
			logger.setSource(loggerFactory.getLogger(logger.getName()));
			logger.info("Set logger factory [" + loggerFactory + "]");
			for (Entry<String, DynamicLogger> entry : loggerMap.entrySet()) {
				if (entry.getValue() == logger) {
					continue;
				}

				String name = entry.getKey();
				Logger oldLogger = entry.getValue().getSource();
				Logger logger = loggerFactory.getLogger(name);
				entry.getValue().setSource(logger);
				try {
					if (oldLogger instanceof AutoCloseable) {
						((AutoCloseable) oldLogger).close();
					}
				} catch (Throwable e) {
					entry.getValue().error(e, "Failed to close the old logger {}", oldLogger);
				}
			}
			return old;
		}
	}
}
