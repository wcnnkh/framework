package scw.logger.log4j;

import java.lang.reflect.Method;
import java.util.Properties;

import org.w3c.dom.Element;

import scw.core.GlobalPropertyFactory;
import scw.core.reflect.ReflectionUtils;
import scw.core.utils.StringUtils;
import scw.core.utils.XMLUtils;
import scw.io.resource.ResourceUtils;
import scw.logger.Level;
import scw.logger.LoggerLevelUtils;
import scw.util.FormatUtils;
import scw.util.KeyValuePair;

public final class Log4jUtils {
	private static final String LOG4J_PATH = "scw_log4j";
	private static final String LOG4J_APPEND_PATH = "/log4j-append.properties";
	private static final String LOG4J_DEFAULT_CONFIG = "log4j.default.config";

	private Log4jUtils() {
	}

	public static void setDefaultLog4jPath(String path) {
		GlobalPropertyFactory.getInstance().put(LOG4J_PATH, path);
	}
	
	public static String getDefaultLog4jPath(){
		return GlobalPropertyFactory.getInstance().getString(LOG4J_PATH);
	}
	
	public static boolean isLog4jDefaultConfig(){
		return GlobalPropertyFactory.getInstance().getValue(LOG4J_DEFAULT_CONFIG, boolean.class, true);
	}
	
	public static void setLog4jDefaultConfig(boolean config){
		GlobalPropertyFactory.getInstance().put(LOG4J_DEFAULT_CONFIG, config);
	}

	private static void initByProperties(Properties properties) {
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

	private static void initByXml(Element element) {
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
		if (!isLog4jDefaultConfig()) {
			return;
		}

		String path = getDefaultLog4jPath();
		if (StringUtils.isEmpty(path)) {
			if (ResourceUtils.getResourceOperations().isExist(
					"classpath:/log4j.properties")) {
				Properties properties = ResourceUtils.getResourceOperations()
						.getProperties("classpath:/log4j.properties");
				initByProperties(properties);
				return;
			} else if (ResourceUtils.getResourceOperations().isExist(
					"classpath:/log4j.xml")) {
				Element element = XMLUtils
						.getRootElement("classpath:/log4j.xml");
				initByXml(element);
				return;
			}
		} else {
			if (ResourceUtils.getResourceOperations().isExist(path)) {
				if (path.endsWith(".properties")) {
					Properties properties = ResourceUtils
							.getResourceOperations().getProperties(path);
					initByProperties(properties);
					return;
				} else if (path.endsWith(".xml")) {
					Element element = XMLUtils.getRootElement(path);
					initByXml(element);
					return;
				}
			}
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
