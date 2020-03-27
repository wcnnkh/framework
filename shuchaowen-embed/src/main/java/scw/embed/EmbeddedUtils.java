package scw.embed;

import scw.core.utils.StringUtils;
import scw.util.value.property.PropertyFactory;

public final class EmbeddedUtils {
	private EmbeddedUtils() {
	};

	private static String getApplicationKey(String name) {
		return "application." + name;
	}

	private static String getTomcatKey(String name) {
		return "tomcat." + name;
	}

	private static String getProperty(PropertyFactory propertyFactory, String name) {
		String v = propertyFactory.getString(getApplicationKey(name));
		if (StringUtils.isEmpty(v)) {
			v = propertyFactory.getString(getTomcatKey(name));
		}
		return v;
	}

	public static int getPort(PropertyFactory propertyFactory) {
		return StringUtils.parseInt(getProperty(propertyFactory, "port"), 8080);
	}

	public static String getEmbeddedName(PropertyFactory propertyFactory) {
		return propertyFactory.getString(getApplicationKey("embedded"));
	}

	public static String getBaseDir(PropertyFactory propertyFactory) {
		return getProperty(propertyFactory, "basedir");
	}

	public static String getContextPath(PropertyFactory propertyFactory) {
		return getProperty(propertyFactory, "contextPath");
	}

	public static String getDefaultServletMapping(PropertyFactory propertyFactory) {
		return getProperty(propertyFactory, "source");
	}

	public static String getTomcatProtocol(PropertyFactory propertyFactory) {
		return propertyFactory.getString(getTomcatKey("protocol"));
	}

	public static String getTomcatConnectorName(PropertyFactory propertyFactory) {
		return propertyFactory.getString(getTomcatKey("connector"));
	}

	public static String getTomcatContextManager(PropertyFactory propertyFactory) {
		return propertyFactory.getString(getTomcatKey("context.manager"));
	}

	private static String getShutdownProperty(PropertyFactory propertyFactory, String name) {
		return getProperty(propertyFactory, "shutdown." + name);
	}

	public static String getShutdownPath(PropertyFactory propertyFactory) {
		return getShutdownProperty(propertyFactory, "path");
	}

	public static String getShutdownName(PropertyFactory propertyFactory) {
		return getShutdownProperty(propertyFactory, "name");
	}

	public static String getShutdownIp(PropertyFactory propertyFactory) {
		return getShutdownProperty(propertyFactory, "ip");
	}

	public static String getShutdownUserName(PropertyFactory propertyFactory) {
		return getShutdownProperty(propertyFactory, "username");
	}

	public static String getShutdownPassword(PropertyFactory propertyFactory) {
		return getShutdownProperty(propertyFactory, "password");
	}

	public static boolean tomcatScanTld(PropertyFactory propertyFactory) {
		return StringUtils.parseBoolean(propertyFactory.getString(getTomcatKey("scan.tld")), true);
	}
}
