package scw.logger.log4j;

import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import scw.core.exception.NotFoundException;
import scw.core.utils.ConfigUtils;
import scw.core.utils.PropertiesUtils;
import scw.core.utils.StringUtils;
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
		String log4j = ConfigUtils.searchFileName("log4j.properties");
		if(StringUtils.isEmpty(log4j)){
			throw new NotFoundException("log4j.properties");
		}
		Properties properties = PropertiesUtils.getProperties(log4j);
		PropertyConfigurator.configure(properties);
	}
}
