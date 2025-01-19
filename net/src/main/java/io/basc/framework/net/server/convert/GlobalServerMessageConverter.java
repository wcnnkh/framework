package io.basc.framework.net.server.convert;

public class GlobalServerMessageConverter extends ConfigurableServerMessageConverter {
	private static volatile GlobalServerMessageConverter instance;

	public static GlobalServerMessageConverter getInstance() {
		if (instance == null) {
			synchronized (GlobalServerMessageConverter.class) {
				if (instance == null) {
					instance = new GlobalServerMessageConverter();
					instance.doNativeConfigure();
				}
			}
		}
		return instance;
	}

	private GlobalServerMessageConverter() {
	}
}
