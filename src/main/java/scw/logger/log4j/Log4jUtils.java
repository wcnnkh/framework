package scw.logger.log4j;

import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import scw.core.Constants;
import scw.core.exception.NotFoundException;
import scw.core.utils.PropertiesUtils;
import scw.core.utils.ResourceUtils;
import scw.logger.AsyncLogger;
import scw.logger.AsyncLoggerFactory;

public class Log4jUtils {
	public static scw.logger.Logger getLogger(String name,
			AsyncLoggerFactory asyncLoggerFactory) {
		Logger logger = Logger.getLogger(name);
		return new AsyncLogger(logger.isTraceEnabled(),
				logger.isDebugEnabled(), logger.isInfoEnabled(), true, true,
				name, asyncLoggerFactory);
	}

	public static void defaultInit() {
		if (!ResourceUtils.isExist("classpath:log4j.properties")) {
			throw new NotFoundException("log4j.properties");
		}

		Properties properties = PropertiesUtils.getProperties(
				"classpath:log4j.properties", Constants.DEFAULT_CHARSET_NAME);
		PropertyConfigurator.configure(properties);
	}
}
