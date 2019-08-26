package scw.logger.log4j;

import java.lang.reflect.Method;
import java.util.Properties;

import org.w3c.dom.Element;

import scw.core.Constants;
import scw.core.SystemPropertyFactory;
import scw.core.exception.NotSupportException;
import scw.core.reflect.ReflectUtils;
import scw.core.utils.PropertiesUtils;
import scw.core.utils.ResourceUtils;
import scw.core.utils.StringUtils;
import scw.core.utils.SystemPropertyUtils;
import scw.core.utils.XMLUtils;
import scw.logger.LoggerUtils;

public final class Log4jUtils {
	private static final String LOG4J_PATH = "scw_log4j";

	private Log4jUtils() {
	}

	public static void setLog4jPath(String path) {
		System.setProperty(LOG4J_PATH, path);
	}

	private static void initByProperties(Properties properties) {
		if (properties == null) {
			return;
		}

		Method method = ReflectUtils.findMethod("org.apache.log4j.PropertyConfigurator", "configure", Properties.class);
		if (method == null) {
			return;
		}

		try {
			method.invoke(null, properties);
		} catch (Exception e) {
		}
	}

	private static void initByXml(Element element) {
		if (element == null) {
			return;
		}

		Method method = ReflectUtils.findMethod("org.apache.log4j.xml.DOMConfigurator", "configure", Element.class);
		if (method == null) {
			return;
		}

		try {
			method.invoke(null, element);
		} catch (Exception e) {
		}
	}

	public static void defaultInit() {
		boolean b = false;
		String path = SystemPropertyUtils.getProperty(LOG4J_PATH);
		if (StringUtils.isEmpty(path)) {
			if (ResourceUtils.isExist("classpath:/log4j.properties")) {
				b = true;
				Properties properties = PropertiesUtils.getProperties("classpath:/log4j.properties",
						Constants.DEFAULT_CHARSET_NAME, SystemPropertyFactory.INSTANCE);
				initByProperties(properties);
			} else if (ResourceUtils.isExist("classpath:/log4j.xml")) {
				b = true;
				Element element = XMLUtils.getRootElement("classpath:/log4j.xml");
				initByXml(element);
			}
		} else {
			if (ResourceUtils.isExist(path)) {
				if (path.endsWith(".properties")) {
					b = true;
					Properties properties = PropertiesUtils.getProperties(path, Constants.DEFAULT_CHARSET_NAME, SystemPropertyFactory.INSTANCE);
					initByProperties(properties);
				} else if (path.endsWith(".xml")) {
					b = true;
					Element element = XMLUtils.getRootElement(path);
					initByXml(element);
				}
			}
		}

		if (!b) {
			if(LoggerUtils.defaultConfigEnable()){
				LoggerUtils.info(Log4jUtils.class, "init default log4j config");
				Properties properties = new Properties();
				properties.put("log4j.rootLogger", "INFO, stdout, logfile, warn");
				properties.put("log4j.appender.stdout", "org.apache.log4j.ConsoleAppender");
				properties.put("log4j.appender.stdout.layout", "org.apache.log4j.PatternLayout");
				properties.put("log4j.appender.stdout.layout.ConversionPattern", "%d %p [%c] - %m%n");
				properties.put("log4j.appender.logfile", "org.apache.log4j.DailyRollingFileAppender");
				properties.put("log4j.appender.logfile.File", SystemPropertyUtils.getWorkPath() + "/logs/log.log");
				properties.put("log4j.appender.logfile.layout", "org.apache.log4j.PatternLayout");
				properties.put("log4j.appender.logfile.DatePattern", "'.'yyyy-MM-dd");
				properties.put("log4j.appender.logfile.layout.ConversionPattern", "%d %p [%c] - %m%n");
				properties.put("log4j.appender.warn", "org.apache.log4j.DailyRollingFileAppender");
				properties.put("log4j.appender.warn.Encoding", Constants.DEFAULT_CHARSET_NAME);
				properties.put("log4j.appender.warn.Threshold", "WARN");
				properties.put("log4j.appender.warn.File", SystemPropertyUtils.getWorkPath() + "/logs/error_warn.log");
				properties.put("log4j.appender.warn.layout", "org.apache.log4j.PatternLayout");
				properties.put("log4j.appender.warn.DatePattern", "'.'yyyy-MM-dd");
				properties.put("log4j.appender.warn.layout.ConversionPattern", "%d %p [%c] - %m%n");
				initByProperties(properties);
				return ;
			}
			throw new NotSupportException("log4j");
		}
	}
}
