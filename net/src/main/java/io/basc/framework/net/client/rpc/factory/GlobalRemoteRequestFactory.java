package io.basc.framework.net.client.rpc.factory;

public class GlobalRemoteRequestFactory extends ConfigurableRemoteRequestFactory {
	private static volatile GlobalRemoteRequestFactory instance;

	public static GlobalRemoteRequestFactory getInstance() {
		if (instance == null) {
			synchronized (GlobalRemoteRequestFactory.class) {
				if (instance == null) {
					instance = new GlobalRemoteRequestFactory();
					instance.doNativeConfigure();
				}
			}
		}
		return instance;
	}

	private GlobalRemoteRequestFactory() {
		// TODO
	}
}
