package io.basc.framework.net.server.convert;

import io.basc.framework.beans.factory.spi.SPI;

public class GlobalServerMessageConverter extends ConfigurableServerMessageConverter {
	private static volatile GlobalServerMessageConverter instance;

	public static GlobalServerMessageConverter getInstance() {
		if (instance == null) {
			synchronized (GlobalServerMessageConverter.class) {
				if (instance == null) {
					instance = new GlobalServerMessageConverter();
					instance.configure(SPI.global());
				}
			}
		}
		return instance;
	}

	private GlobalServerMessageConverter() {
	}
}
