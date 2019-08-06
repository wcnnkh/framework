package scw.application.embedded;

import scw.core.PropertiesFactory;
import scw.core.utils.StringUtils;

public final class EmbeddedUtils {
	private EmbeddedUtils() {
	};

	private static String getApplicationKey(String name) {
		return "application." + name;
	}

	private static String getTomcatKey(String name) {
		return "tomcat." + name;
	}

	private static String getProperty(PropertiesFactory propertiesFactory, String name) {
		String v = propertiesFactory.getValue(getApplicationKey(name));
		if (StringUtils.isEmpty(v)) {
			v = propertiesFactory.getValue(getTomcatKey(name));
		}
		return v;
	}

	public static int getPort(PropertiesFactory propertiesFactory) {
		return StringUtils.parseInt(getProperty(propertiesFactory, "port"), 8080);
	}

	public static String getEmbeddedName(PropertiesFactory propertiesFactory) {
		return propertiesFactory.getValue(getApplicationKey("embedded"));
	}

	public static String getBaseDir(PropertiesFactory propertiesFactory) {
		return getProperty(propertiesFactory, "basedir");
	}

	public static String getContextPath(PropertiesFactory propertiesFactory) {
		return getProperty(propertiesFactory, "contextPath");
	}
	
	public static String getSource(PropertiesFactory propertiesFactory){
		return getProperty(propertiesFactory, "source");
	}

	public static String getTomcatProtocol(PropertiesFactory propertiesFactory) {
		return propertiesFactory.getValue(getTomcatKey("protocol"));
	}

	public static String getTomcatConnectorName(PropertiesFactory propertiesFactory) {
		return propertiesFactory.getValue(getTomcatKey("connector"));
	}

	private static String getShutdownProperty(PropertiesFactory propertiesFactory, String name) {
		return getProperty(propertiesFactory, "shutdown." + name);
	}

	public static String getShutdownPath(PropertiesFactory propertiesFactory) {
		return getShutdownProperty(propertiesFactory, "path");
	}

	public static String getShutdownName(PropertiesFactory propertiesFactory) {
		return getShutdownProperty(propertiesFactory, "name");
	}
	
	public static String getShutdownIp(PropertiesFactory propertiesFactory){
		return getShutdownProperty(propertiesFactory, "ip");
	}
	
	public static String getShutdownUserName(PropertiesFactory propertiesFactory){
		return getShutdownProperty(propertiesFactory, "username");
	}
	
	public static String getShutdownPassword(PropertiesFactory propertiesFactory){
		return getShutdownProperty(propertiesFactory, "password");
	}
}
