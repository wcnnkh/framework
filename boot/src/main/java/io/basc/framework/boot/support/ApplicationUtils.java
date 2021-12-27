package io.basc.framework.boot.support;

import io.basc.framework.env.ConfigurableEnvironment;
import io.basc.framework.env.Environment;
import io.basc.framework.env.MainArgs;
import io.basc.framework.lang.Nullable;
import io.basc.framework.value.Value;

public final class ApplicationUtils {
	public static final String SERVER_PORT_PROPERTY = "server.port";
	public static final String APPLICATION_NAME_PROPERTY = "application.name";
	/**
	 * 默认端口号:8080
	 */
	public static final int DEFAULT_SERVER_PORT = 8080;

	@Nullable
	public static String getApplicatoinName(Environment environment) {
		return environment.getString(APPLICATION_NAME_PROPERTY);
	}

	/**
	 * 
	 * 获取服务端的端口号
	 * 
	 * @see ApplicationUtils#DEFAULT_SERVER_PORT
	 * @param environment
	 * @return
	 */
	public static int getServerPort(Environment environment) {
		return environment.getValue(SERVER_PORT_PROPERTY, int.class, DEFAULT_SERVER_PORT);
	}

	public static void setServerPort(ConfigurableEnvironment environment, int port) {
		environment.put(SERVER_PORT_PROPERTY, port);
	}

	/**
	 * 使用-p参数获取端口号
	 * 
	 * @param args
	 * @return
	 */
	@Nullable
	public static Integer getPort(MainArgs args) {
		Value port = args.getValue(SERVER_PORT_PROPERTY);
		if (port != null && !port.isEmpty()) {
			// 这样做的目的是为了可以覆盖-p参数
			return port.getAsInteger();
		}

		port = args.getNextValue("-p");
		return (port != null && port.isNumber()) ? port.getAsInteger() : null;
	}
}
