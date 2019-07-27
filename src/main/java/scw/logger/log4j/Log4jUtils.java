package scw.logger.log4j;

import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

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
		Properties properties;
		if (StringUtils.isEmpty(log4j)) {
			String rootPath = ConfigUtils.getWorkPath();
			if (StringUtils.isEmpty(rootPath)) {
				rootPath = ConfigUtils.getClassPath();
			}

			properties = new Properties();
			properties.put("log4j.rootLogger", "TRACE, DEBUG, INFO, stdout, logfile, warn");
			properties.put("log4j.appender.stdout",
					"org.apache.log4j.ConsoleAppender");
			properties.put("log4j.appender.stdout.layout",
					"org.apache.log4j.PatternLayout");
			properties.put("log4j.appender.stdout.layout.ConversionPattern",
					"%d %p [%c] - %m%n");
			properties.put("log4j.appender.logfile",
					"org.apache.log4j.DailyRollingFileAppender");
			properties.put("log4j.appender.logfile.File", rootPath
					+ "/log/log.log");
			properties.put("log4j.appender.logfile.layout",
					"org.apache.log4j.PatternLayout");
			properties.put("log4j.appender.logfile.DatePattern",
					"'.'yyyy-MM-dd");
			properties.put("log4j.appender.logfile.layout.ConversionPattern",
					"%d %p [%c] - %m%n");
			properties.put("log4j.appender.warn",
					"org.apache.log4j.DailyRollingFileAppender");
			properties.put("log4j.appender.warn.Encoding", "UTF-8");
			properties.put("log4j.appender.warn.Threshold", "WARN");
			properties.put("log4j.appender.warn.File", rootPath
					+ "/log/error_warn.log");
			properties.put("log4j.appender.warn.layout",
					"org.apache.log4j.PatternLayout");
			properties.put("log4j.appender.warn.DatePattern", "'.'yyyy-MM-dd");
			properties.put("log4j.appender.warn.layout.ConversionPattern",
					"%d %p [%c] - %m%n");
		} else {
			properties = PropertiesUtils.getProperties(log4j);
		}
		PropertyConfigurator.configure(properties);
	}
}
