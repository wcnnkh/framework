package io.basc.framework.net.rpc.factory;

import io.basc.framework.beans.factory.spi.SPI;

public class GlobalRemoteRequestFactory extends ConfigurableRemoteRequestFactory {
	private static volatile GlobalRemoteRequestFactory instance;

	public static GlobalRemoteRequestFactory getInstance() {
		if (instance == null) {
			synchronized (GlobalRemoteRequestFactory.class) {
				if (instance == null) {
					instance = new GlobalRemoteRequestFactory();
					instance.configure(SPI.global());
				}
			}
		}
		return instance;
	}

	private GlobalRemoteRequestFactory() {
		// TODO
	}
}
