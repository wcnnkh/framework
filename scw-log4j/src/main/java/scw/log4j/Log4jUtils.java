package scw.log4j;

import java.lang.reflect.Method;
import java.util.Map.Entry;
import java.util.Properties;

import org.w3c.dom.Element;

import scw.core.reflect.ReflectionUtils;
import scw.env.SystemEnvironment;
import scw.logger.Level;
import scw.logger.LoggerLevelManager;
import scw.logger.LoggerPropertyFactory;
import scw.util.FormatUtils;
import scw.util.DefaultPlaceholderResolver;

public final class Log4jUtils {
	private Log4jUtils() {
	}

	public static void initByProperties(Properties properties) {
		if (properties == null) {
			return;
		}

		Method method = ReflectionUtils.getMethod("org.apache.log4j.PropertyConfigurator", "configure",
				Properties.class);
		if (method == null) {
			return;
		}

		try {
			Properties propertiesToUse = FormatUtils.format(properties, new DefaultPlaceholderResolver(LoggerPropertyFactory.getInstance()));
			method.invoke(null, propertiesToUse);
		} catch (Exception e) {
		}
	}

	public static void initByXml(Element element) {
		if (element == null) {
			return;
		}

		Method method = ReflectionUtils.getMethod("org.apache.log4j.xml.DOMConfigurator", "configure", Element.class);
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
			FormatUtils.info(Log4jUtils.class, "Already exist log4j.properties");
			return;
		}

		Properties properties = SystemEnvironment.getInstance().getProperties("classpath:/scw/log4j/default-log4j.properties").get();
		for (Entry<String, Level> entry : LoggerLevelManager.getInstance().get().entrySet()) {
			properties.put("log4j.logger." + entry.getKey(), entry.getValue().getName());
		}
		initByProperties(properties);
	}
}
