package io.basc.framework.net.client.factory;

public class GlobalClientRequestFactory extends ConfigurableClientRequestFactory {
	private static volatile GlobalClientRequestFactory instance;

	public static GlobalClientRequestFactory getInstance() {
		if (instance == null) {
			synchronized (GlobalClientRequestFactory.class) {
				if (instance == null) {
					instance = new GlobalClientRequestFactory();
					instance.doNativeConfigure();
				}
			}
		}
		return instance;
	}

	private GlobalClientRequestFactory() {
	}
}
