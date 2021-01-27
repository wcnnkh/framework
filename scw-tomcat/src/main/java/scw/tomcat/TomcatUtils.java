package scw.tomcat;

import java.util.Properties;

import scw.core.utils.StringUtils;
import scw.env.Environment;
import scw.io.ResourceUtils;

public final class TomcatUtils {
	private TomcatUtils() {
	};

	private static String getProperty(Environment environment, String name) {
		return environment.getString("tomcat." + name);
	}

	public static String getBaseDir(Environment environment) {
		return getProperty(environment, "basedir");
	}

	public static String getContextPath(Environment environment) {
		return getProperty(environment, "contextPath");
	}

	public static String getDefaultServletMapping(Environment environment) {
		return getProperty(environment, "source");
	}

	public static String getTomcatProtocol(Environment environment) {
		return getProperty(environment, "protocol");
	}

	public static String getTomcatConnectorName(Environment environment) {
		return getProperty(environment, "connector");
	}

	public static String getTomcatContextManager(Environment environment) {
		return getProperty(environment, "context.manager");
	}

	private static String getShutdownProperty(Environment environment, String name) {
		return getProperty(environment, "shutdown." + name);
	}

	public static String getShutdownPath(Environment environment) {
		return getShutdownProperty(environment, "path");
	}

	public static String getShutdownName(Environment environment) {
		return getShutdownProperty(environment, "name");
	}

	public static String getShutdownIp(Environment environment) {
		return getShutdownProperty(environment, "ip");
	}

	public static String getShutdownUserName(Environment environment) {
		return getShutdownProperty(environment, "username");
	}

	public static String getShutdownPassword(Environment environment) {
		return getShutdownProperty(environment, "password");
	}

	public static boolean tomcatScanTld(Environment environment) {
		return StringUtils.parseBoolean(getProperty(environment, "scan.tld"), true);
	}

	public static Properties getServletInitParametersConfig(Environment environment, String servletName, boolean loadOnStartup) {
		String path = servletName + "-servlet-init-params.properties";
		Properties properties = new Properties();
		if (loadOnStartup) {
			properties.put("load-on-startup", 1);
		}

		if (ResourceUtils.exists(environment, path)) {
			properties.putAll(environment.getProperties(path).get());
		}
		return properties;
	}
}
