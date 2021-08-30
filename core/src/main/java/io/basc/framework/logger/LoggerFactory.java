package io.basc.framework.logger;

import io.basc.framework.factory.ServiceLoaderFactory;
import io.basc.framework.factory.support.DefaultServiceLoaderFactory;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.CollectionUtils;
import io.basc.framework.value.PropertyFactory;
import io.basc.framework.value.support.SystemPropertyFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.logging.Handler;
import java.util.logging.Level;

public final class LoggerFactory {
	private static final java.util.logging.Logger ROOT_LOGGER = java.util.logging.Logger
			.getLogger(LoggerFactory.class.getName());
	
	private static final ILoggerFactory LOGGER_FACTORY = CollectionUtils
			.first(ServiceLoader.load(ILoggerFactory.class));
	private volatile static Map<String, Logger> loggerMap = new HashMap<String, Logger>();

	private static final LevelManager LEVEL_MANAGER;

	static {
		PropertyFactory propertyFactory = SystemPropertyFactory.INSTANCE;
		ServiceLoaderFactory serviceLoaderFactory = new DefaultServiceLoaderFactory(propertyFactory) {
			@Override
			protected boolean useSpi(Class<?> serviceClass) {
				return true;
			}
		};
		Iterator<LevelManager> levelManagerIterator = serviceLoaderFactory.getServiceLoader(LevelManager.class).iterator();
		if (levelManagerIterator.hasNext()) {
			LEVEL_MANAGER = levelManagerIterator.next();
		} else {
			LEVEL_MANAGER = new LevelManager();
		}

		// 使用spi机制加载handlers
		if(propertyFactory.getValue("io.basc.framework.logger.handlers.enable", boolean.class, true)) {
			List<Handler> handlers = serviceLoaderFactory.getServiceLoader(Handler.class).toList();
			if (!CollectionUtils.isEmpty(handlers)) {
				// 存在自定义handler的情况不使用父级的handler
				ROOT_LOGGER.setUseParentHandlers(false);
				for (Handler handler : handlers) {
					ROOT_LOGGER.info("Use logger handler [" + handler + "]");
					ROOT_LOGGER.addHandler(handler);
				}
			}
		}
		
		if (LOGGER_FACTORY == null) {
			//使用jdk自身的日志系统
			java.util.logging.Logger logger = ROOT_LOGGER;
			while (logger != null) {
				Handler[] rootHandlers = logger.getHandlers();
				if (rootHandlers != null) {
					for (Handler handler : rootHandlers) {
						handler.setLevel(Level.ALL);
					}
				}

				if (logger.getUseParentHandlers()) {
					logger = logger.getParent();
				} else {
					break;
				}
			}
		}else{
			// 存在第三方日志系统
			Logger logger = LOGGER_FACTORY.getLogger(LoggerFactory.class
					.getName());
			logger.info("Use logger factory [" + LOGGER_FACTORY + "]");
		}
	}

	public static LevelManager getLevelManager() {
		return LEVEL_MANAGER;
	}

	/**
	 * 获取自定义根日志记录器
	 * 
	 * @see #getLogger(String)
	 * @see #getLogger(Class)
	 * @return
	 */
	public static java.util.logging.Logger getRootLogger() {
		return ROOT_LOGGER;
	}

	/**
	 * 获取默认日志工厂实现<br/>
	 * 一般情况下不要使用此访求获取日志记录器，除非你知道你想做什么
	 * 
	 * @see #getLogger(String)
	 * @see #getLogger(Class)
	 * @return
	 */
	@Nullable
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
		Logger cacheLogger = loggerMap.get(name);
		if (cacheLogger == null) {
			synchronized (loggerMap) {
				cacheLogger = loggerMap.get(name);
				if (cacheLogger == null) {
					if (LOGGER_FACTORY == null) {
						java.util.logging.Logger logger = java.util.logging.Logger
								.getLogger(name);
						java.util.logging.Logger parent = logger.getParent();
						if (parent != ROOT_LOGGER) {
							logger.setParent(ROOT_LOGGER);
						}
						cacheLogger = new JdkLogger(logger);
					} else {
						cacheLogger = LOGGER_FACTORY.getLogger(name);
					}
					loggerMap.put(name, cacheLogger);
				}
			}
		}
		return cacheLogger;
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
		throw new RuntimeException();
	};
}
