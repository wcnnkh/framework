package io.basc.framework.web.multipart;

import io.basc.framework.beans.factory.spi.SPI;

public class GlobalMultipartMessageResolver extends ConfigurableMultipartMessageResolver {
	private static volatile GlobalMultipartMessageResolver instance;

	public static GlobalMultipartMessageResolver getInstance() {
		if (instance == null) {
			synchronized (GlobalMultipartMessageResolver.class) {
				if (instance == null) {
					instance = new GlobalMultipartMessageResolver();
					instance.configure(SPI.global());
				}
			}
		}
		return instance;
	}
}
