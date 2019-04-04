package scw.logger;

import scw.common.utils.ClassUtils;
import scw.logger.console.ConsoleLoggerFactory;

public final class LoggerFactory {
	private static final String SLF4J_CLASS_NAME = "scw.logger.slf4j.Sl4jILoggerFactory";
	private static final ILoggerFactory CONSOLE_LOGGER_FACTORY = new ConsoleLoggerFactory();
	@SuppressWarnings("unused")
	private static ILoggerFactory sl4j;

	private LoggerFactory() {
	};

	static {
		try {
			Class<?> clz = Class.forName(SLF4J_CLASS_NAME);
			sl4j = (ILoggerFactory) ClassUtils.newInstance(clz);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
		System.out.println("Init shuchaowen-logger [" + getILoggerFactory().getClass().getName() + "]");
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
		return CONSOLE_LOGGER_FACTORY;
		/*if(sl4j == null){
			return CONSOLE_LOGGER_FACTORY;
		}else{
			return sl4j;
		}*/
	}
}
