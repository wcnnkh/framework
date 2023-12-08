package io.basc.framework.net.message.multipart;

import io.basc.framework.beans.factory.spi.SPI;

public class GlobalMultipartMessageResolver extends MultipartMessageResolvers {
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
