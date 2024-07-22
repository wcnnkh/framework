package io.basc.framework.net.client.factory;

import io.basc.framework.beans.factory.spi.SPI;

public class GlobalClientRequestFactory extends ConfigurableClientRequestFactory {
	private static volatile GlobalClientRequestFactory instance;

	public static GlobalClientRequestFactory getInstance() {
		if (instance == null) {
			synchronized (GlobalClientRequestFactory.class) {
				if (instance == null) {
					instance = new GlobalClientRequestFactory();
					instance.configure(SPI.global());
				}
			}
		}
		return instance;
	}

	private GlobalClientRequestFactory() {
	}
}
