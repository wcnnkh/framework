package scw.logger;

import scw.core.instance.InstanceUtils;

public final class LoggerFactory {
	private static final ILoggerFactory LOGGER_FACTORY;

	private LoggerFactory() {
	};

	static {
		String[] supportArr = { "scw.logger.slf4j.Slf4jILoggerFactory" };

		//TODO 暂不使用其他日志框架，这里只是伪代码
		@SuppressWarnings("unused")
		ILoggerFactory loggerFactory;
		for (String name : supportArr) {
			loggerFactory = InstanceUtils.newInstance(name);
		}

		LOGGER_FACTORY = new ConsoleLoggerFactory();
		System.out.println("Init shuchaowen-logger ["
				+ getILoggerFactory().getClass().getName() + "]");
	}

	public static Logger getLogger(String name) {
		return getILoggerFactory().getLogger(name);
	}

	public static Logger getLogger(Class<?> clz) {
		return getLogger(clz.getName());
	}

	public static void destroy() {
		getILoggerFactory().destroy();
	}

	public static ILoggerFactory getILoggerFactory() {
		return LOGGER_FACTORY;
	}
}
