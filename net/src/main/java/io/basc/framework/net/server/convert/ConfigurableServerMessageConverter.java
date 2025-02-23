package io.basc.framework.net.server.convert;

public class ConfigurableServerMessageConverter extends ServerMessageConverters<ServerMessageConverter>
		implements ServerMessageConverter {

	private static volatile ConfigurableServerMessageConverter instance;

	public static ConfigurableServerMessageConverter global() {
		if (instance == null) {
			synchronized (ConfigurableServerMessageConverter.class) {
				if (instance == null) {
					instance = new ConfigurableServerMessageConverter();
					instance.doNativeConfigure();
				}
			}
		}
		return instance;
	}

	public ConfigurableServerMessageConverter() {
		setServiceClass(ServerMessageConverter.class);
	}

}
