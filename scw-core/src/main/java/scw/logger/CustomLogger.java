package scw.logger;

import java.util.List;
import java.util.ServiceLoader;
import java.util.SortedMap;
import java.util.function.Supplier;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import scw.core.utils.CollectionUtils;
import scw.event.ChangeEvent;
import scw.event.EventListener;
import scw.util.FormatUtils;

//TODO 这是因为可变参数方法重写导致的，暂不处理此警告
public class CustomLogger extends java.util.logging.Logger
		implements scw.logger.Logger, EventListener<ChangeEvent<SortedMap<String, Level>>> {
	private static final Logger ROOT_LOGGER = Logger.getLogger(CustomLogger.class.getName());

	static {
		//将默认的全局记录器设置为父级记录器
		ROOT_LOGGER.setParent(Logger.getLogger(GLOBAL_LOGGER_NAME));

		//使用spi机制加载handlers
		List<Handler> handlers = CollectionUtils.toList(ServiceLoader.load(Handler.class));
		if (!CollectionUtils.isEmpty(handlers)) {
			//存在自定义handler的情况不使用父级的handler
			ROOT_LOGGER.setUseParentHandlers(false);
			for (Handler handler : handlers) {
				ROOT_LOGGER.addHandler(handler);
			}
		}
	}

	/**
	 * 所有自定义日志记录器的根记录器
	 * @return
	 */
	public static Logger getRootLogger() {
		return ROOT_LOGGER;
	}

	public static CustomLogger getLogger(Class<?> clazz) {
		return new CustomLogger(getLogger(clazz.getName()));
	}

	public static CustomLogger getLogger(String name) {
		return new CustomLogger(Logger.getLogger(name));
	}

	private CustomLogger(Logger logger) {
		super(logger.getName(), logger.getResourceBundleName());
		
		//设置根记录器
		setParent(ROOT_LOGGER);
		
		if (logger.getFilter() != null) {
			setFilter(logger.getFilter());
		}

		if (logger.getLevel() != null) {
			setLevel(logger.getLevel());
		}
		
		//注册日志变量监听
		LoggerLevelManager.getInstance().registerListener(this);
	}

	/**
	 * 当存存在日志级别变更时会调用此方法
	 */
	@Override
	public void onEvent(ChangeEvent<SortedMap<String, Level>> event) {
		Level level = LoggerLevelManager.getInstance().getLevel(getName());
		if (!level.equals(getLevel())) {
			// 这里使用off是为了任意日志级别都会显示该日志
			log(Level.OFF, "Level [{}] change to [{}]", new Object[] { getLevel(), level });
			setLevel(level);
		}
	}

	public boolean isInfoEnabled() {
		return isLoggable(CustomLevel.INFO);
	}
	
	@Override
	public void log(Level level, Throwable e, String msg, Object... args) {
		if(!isLoggable(level)) {
			return ;
		}
		
		logp(level, null, null, e, new Supplier<String>() {
			
			@Override
			public String get() {
				return FormatUtils.formatPlaceholder(msg, null, args);
			}
		});
	}
}
