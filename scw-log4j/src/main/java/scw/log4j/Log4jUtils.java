package scw.log4j;

import java.lang.reflect.Method;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.w3c.dom.Element;

import scw.core.reflect.ReflectionUtils;
import scw.env.SystemEnvironment;
import scw.logger.LoggerLevelManager;
import scw.logger.LoggerPropertyFactory;
import scw.util.FormatUtils;
import scw.util.placeholder.PlaceholderResolver;
import scw.util.placeholder.PropertyResolver;
import scw.util.placeholder.support.DefaultPlaceholderResolver;
import scw.util.placeholder.support.TempPropertyResolver;

public final class Log4jUtils {
	private static Logger logger = Logger.getLogger(Log4jUtils.class.getName());
	private Log4jUtils() {
	}

	public static void initByProperties(Properties properties) {
		if (properties == null) {
			return;
		}

		Method method = ReflectionUtils.getMethod("org.apache.log4j.PropertyConfigurator", null, "configure",
				Properties.class);
		if (method == null) {
			return;
		}

		PlaceholderResolver placeholderResolver = new DefaultPlaceholderResolver(LoggerPropertyFactory.getInstance());
		PropertyResolver propertyResolver = new TempPropertyResolver(SystemEnvironment.getInstance(), placeholderResolver);
		try {
			Properties propertiesToUse = FormatUtils.format(properties, propertyResolver);
			method.invoke(null, propertiesToUse);
		} catch (Exception e) {
		}
	}

	public static void initByXml(Element element) {
		if (element == null) {
			return;
		}

		Method method = ReflectionUtils.getMethod("org.apache.log4j.xml.DOMConfigurator", null, "configure", Element.class);
		if (method == null) {
			return;
		}

		try {
			method.invoke(null, element);
		} catch (Exception e) {
		}
	}

	public static void defaultInit() {
		if (SystemEnvironment.getInstance().exists("log4j.properties")) {
			logger.info("Already exist log4j.properties");
			return;
		}

		Properties properties = SystemEnvironment.getInstance().getProperties("classpath:/scw/log4j/default-log4j.properties").get();
		for (Entry<String, Level> entry : LoggerLevelManager.getInstance().get().entrySet()) {
			properties.put("log4j.logger." + entry.getKey(), entry.getValue().getName());
		}
		initByProperties(properties);
	}
}
