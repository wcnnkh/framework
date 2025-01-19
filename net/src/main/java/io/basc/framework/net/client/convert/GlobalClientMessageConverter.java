package io.basc.framework.net.client.convert;

public class GlobalClientMessageConverter extends ConfigurableClientMessageConverter {
	private static volatile GlobalClientMessageConverter instance;

	public static GlobalClientMessageConverter getInstance() {
		if (instance == null) {
			synchronized (GlobalClientMessageConverter.class) {
				if (instance == null) {
					instance = new GlobalClientMessageConverter();
					instance.doNativeConfigure();
				}
			}
		}
		return instance;
	}

	private GlobalClientMessageConverter() {
	}
}
