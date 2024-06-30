package io.basc.framework.net.client.convert;

import io.basc.framework.beans.factory.spi.SPI;

public class GlobalClientMessageConverter extends ConfigurableClientMessageConverter {
	private static volatile GlobalClientMessageConverter instance;

	public static GlobalClientMessageConverter getInstance() {
		if (instance == null) {
			synchronized (GlobalClientMessageConverter.class) {
				if (instance == null) {
					instance = new GlobalClientMessageConverter();
					instance.configure(SPI.global());
				}
			}
		}
		return instance;
	}

	private GlobalClientMessageConverter() {
	}
}
