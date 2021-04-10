package scw.boot.support;

import java.util.LinkedHashSet;
import java.util.Set;

import scw.boot.Application;
import scw.env.Environment;
import scw.instance.InstanceUtils;
import scw.value.Value;

public final class ApplicationUtils {
	public static String getApplicatoinName(Environment environment) {
		return environment.getString("application.name");
	}

	/**
	 * 默认为8080端口
	 * 
	 * @param application
	 * @return
	 */
	public static int getApplicationPort(Application application) {
		return getApplicationPort(application, 8080);
	}

	public static int getApplicationPort(Application application,
			int defaultPort) {
		if (application instanceof MainApplication) {
			Value value = ((MainApplication) application).getMainArgs()
					.getNextValue("-p");
			if (value != null && value.isNumber()) {
				return value.getAsIntValue();
			}
		}

		// tomcat.port兼容老版本
		int port = application.getEnvironment().getValue("tomcat.port",
				int.class, defaultPort);
		port = application.getEnvironment().getValue("port", int.class, port);
		return application.getEnvironment().getValue("server.port", int.class,
				port);
	}

	public static Set<Class<?>> getContextClasses(Application application) {
		return new LinkedHashSet<Class<?>>(InstanceUtils.asList(application
				.getContextClassesLoader()));
	}
}
