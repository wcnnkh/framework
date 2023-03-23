package io.basc.framework.logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;

import io.basc.framework.event.support.StandardBroadcastEventDispatcher;
import io.basc.framework.factory.Configurable;
import io.basc.framework.factory.ServiceLoaderFactory;
import io.basc.framework.util.Assert;
import io.basc.framework.util.ConsumeProcessor;
import io.basc.framework.util.Registration;

public class DynamicLoggerFactory extends StandardBroadcastEventDispatcher<LevelManager>
		implements ILoggerFactory, Configurable {
	public static final JdkLoggerFactory GLOBA_LOGGER_FACTORY = new JdkLoggerFactory();
	public static final LevelManager GLOBAL_LEVEL_MANAGER = new LevelManager();

	private AtomicBoolean configured = new AtomicBoolean();
	private LevelManager levelManager = GLOBAL_LEVEL_MANAGER;
	private final DynamicLogger logger;
	private ILoggerFactory loggerFactory = GLOBA_LOGGER_FACTORY;
	private volatile Map<String, DynamicLogger> loggerMap = new HashMap<String, DynamicLogger>();
	private Registration registration;

	public DynamicLoggerFactory() {
		registration = this.levelManager.registerListener((e) -> publishEvent(this.levelManager));
		this.logger = getLogger(DynamicLoggerFactory.class.getName());
	}

	@Override
	public void configure(ServiceLoaderFactory serviceLoaderFactory) {
		if (isConfigured()) {
			return;
		}

		ILoggerFactory loggerFactory = null;
		try {
			loggerFactory = serviceLoaderFactory.getServiceLoader(ILoggerFactory.class).first();
		} catch (Throwable e) {
			// 解决循环依赖问题,如果出现异常继续使用旧的日志工厂,待初始化完成后会被动态替换
			logger.debug(e, "Configuration log factory exception");
			return;
		}

		if (configured.compareAndSet(false, true) && loggerFactory != null) {
			setLoggerFactory(loggerFactory);
		}
	}

	public ILoggerFactory getILoggerFactory() {
		return loggerFactory;
	}

	public LevelManager getLevelManager() {
		return levelManager;
	}

	public DynamicLogger getLogger() {
		return logger;
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
					registerListener(logger);
					loggerMap.put(name, logger);
				}
			}
		}
		return logger;
	}

	public ILoggerFactory getLoggerFactory() {
		return loggerFactory;
	}

	@Override
	public boolean isConfigured() {
		return configured.get();
	}

	/**
	 * 设置一个新的日志等级管理
	 * 
	 * @param levelManager
	 * @return 旧的日志等级管理
	 */
	public LevelManager setLevelManager(LevelManager levelManager) {
		Assert.requiredArgument(levelManager != null, "levelManager");
		if (levelManager == this.levelManager) {
			return this.levelManager;
		}

		synchronized (this) {
			LevelManager old = this.levelManager;
			this.levelManager = levelManager;
			this.registration.unregister();
			this.registration = levelManager.registerListener((e) -> publishEvent(levelManager));
			publishEvent(levelManager);
			logger.info("Set level manager [" + levelManager + "]");
			return old;
		}
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

	public <E extends Throwable> void update(ConsumeProcessor<? super DynamicLogger, ? extends E> updateProcessor) {
		synchronized (this) {
			for (Entry<String, DynamicLogger> entry : loggerMap.entrySet()) {
				try {
					updateProcessor.process(entry.getValue());
				} catch (Throwable e) {
					entry.getValue().error(e, "Failed to close the old logger {}", entry.getKey());
				}
			}
		}
	}
}
