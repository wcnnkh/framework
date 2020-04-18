package scw.logger.log4j;

import java.lang.reflect.Method;
import java.util.Properties;

import org.w3c.dom.Element;

import scw.core.GlobalPropertyFactory;
import scw.core.reflect.ReflectionUtils;
import scw.io.resource.ResourceUtils;
import scw.logger.Level;
import scw.logger.LoggerLevelUtils;
import scw.util.FormatUtils;
import scw.util.KeyValuePair;

public final class Log4jUtils {
	private static final String LOG4J_APPEND_PATH = "/log4j-append.properties";

	private Log4jUtils() {
	}

	public static void initByProperties(Properties properties) {
		if (properties == null) {
			return;
		}

		Method method = ReflectionUtils.getMethod(
				"org.apache.log4j.PropertyConfigurator", "configure",
				Properties.class);
		if (method == null) {
			return;
		}

		try {
			method.invoke(null, properties);
		} catch (Exception e) {
		}
	}

	public static void initByXml(Element element) {
		if (element == null) {
			return;
		}

		Method method = ReflectionUtils.getMethod(
				"org.apache.log4j.xml.DOMConfigurator", "configure",
				Element.class);
		if (method == null) {
			return;
		}

		try {
			method.invoke(null, element);
		} catch (Exception e) {
		}
	}

	public static void defaultInit() {
		if (ResourceUtils.getResourceOperations().isExist("log4j.properties")) {
			FormatUtils
					.info(Log4jUtils.class, "Already exist log4j.properties");
			return;
		}

		String rootPath = GlobalPropertyFactory.getInstance().getWorkPath();
		FormatUtils.info(Log4jUtils.class,
				"load the default log directory: {}", rootPath);
		Properties properties = ResourceUtils.getResourceOperations()
				.getProperties(
						"classpath:/scw/logger/log4j/default-log4j.properties",
						LoggerLevelUtils.PROPERTY_FACTORY);
		for (KeyValuePair<String, Level> entry : LoggerLevelUtils
				.getLevelConfigList()) {
			properties.put("log4j.logger." + entry.getKey(), entry.getValue()
					.name());
		}

		if (ResourceUtils.getResourceOperations().isExist(LOG4J_APPEND_PATH)) {
			FormatUtils.info(Log4jUtils.class, "loading " + LOG4J_APPEND_PATH);
			Properties append = ResourceUtils.getResourceOperations()
					.getProperties(LOG4J_APPEND_PATH);
			properties.putAll(append);
		}

		initByProperties(properties);
	}
}
