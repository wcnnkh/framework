package scw.logger.log4j;

import java.lang.reflect.Method;
import java.util.Properties;

import org.w3c.dom.Element;

import scw.core.Constants;
import scw.core.exception.NotSupportException;
import scw.core.reflect.ReflectUtils;
import scw.core.utils.PropertiesUtils;
import scw.core.utils.ResourceUtils;
import scw.core.utils.StringUtils;
import scw.core.utils.SystemPropertyUtils;
import scw.core.utils.XMLUtils;

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
			if (ResourceUtils.isExist("classpath:log4j.properties")) {
				b = true;
				Properties properties = PropertiesUtils.getProperties("classpath:log4j.properties",
						Constants.DEFAULT_CHARSET_NAME);
				initByProperties(properties);
			} else if (ResourceUtils.isExist("classpath:log4j.xml")) {
				b = true;
				Element element = XMLUtils.getRootElement("classpath:log4j.xml");
				initByXml(element);
			}
		} else {
			if (ResourceUtils.isExist(path)) {
				if (path.endsWith(".properties")) {
					b = true;
					Properties properties = PropertiesUtils.getProperties(path, Constants.DEFAULT_CHARSET_NAME);
					initByProperties(properties);
				} else if (path.endsWith(".xml")) {
					b = true;
					Element element = XMLUtils.getRootElement(path);
					initByXml(element);
				}
			}
		}

		if (!b) { 
			throw new NotSupportException("log4j");
			//initByProperties(getDefaultProperties());
		}
	}

	/*private static Properties getDefaultProperties() {
		Properties properties = new Properties();
		// set log levels
		properties.put("log4j.rootLogger", "debug,stdout,D,E");
		// 输出到控制台
		properties.put("log4j.appender.stdout", "org.apache.log4j.ConsoleAppender");
		properties.put("log4j.appender.stdout.Target", "System.out");
		properties.put("log4j.appender.stdout.layout", "org.apache.log4j.PatternLayout");
		properties.put("log4j.appender.stdout.layout.ConversionPatter", "%d{ABSOLUTE} %5p %c{ 1 }:%L - %m%n");
		// 输出到日志文件
		properties.put("log4j.appender.D", "org.apache.log4j.DailyRollingFileAppender");
		properties.put("log4j.appender.D.File", "logs/log.log");
		properties.put("log4j.appender.D.Append", "true");
		properties.put("log4j.appender.D.Threshold", "DEBUG");
		properties.put("log4j.appender.D.layout", "org.apache.log4j.PatternLayout");
		properties.put("log4j.appender.D.layout.ConversionPattern",
				"%-d{yyyy-MM-dd HH:mm:ss}  [ %t:%r ] - [ %p ]  %m%n");
		// 保存异常信息到单独文件
		properties.put("log4j.appender.D", "org.apache.log4j.DailyRollingFileAppender");
		properties.put("log4j.appender.D.File", "logs/error.log");
		properties.put("log4j.appender.D.Append", "true");
		properties.put("log4j.appender.D.Threshold", "ERROR");
		properties.put("log4j.appender.D.layout", "org.apache.log4j.PatternLayout");
		properties.put("log4j.appender.D.layout.ConversionPattern",
				"%-d{yyyy-MM-dd HH:mm:ss}  [ %t:%r ] - [ %p ]  %m%n");
		return properties;
	}*/
}
