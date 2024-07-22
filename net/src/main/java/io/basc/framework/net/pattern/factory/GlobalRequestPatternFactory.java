package io.basc.framework.net.pattern.factory;

import io.basc.framework.beans.factory.spi.SPI;

public class GlobalRequestPatternFactory extends ConfigurableRequestPatternFactory {
	private static volatile GlobalRequestPatternFactory instance;

	public static GlobalRequestPatternFactory getInstance() {
		if (instance == null) {
			synchronized (GlobalRequestPatternFactory.class) {
				if (instance == null) {
					instance = new GlobalRequestPatternFactory();
					instance.configure(SPI.global());
				}
			}
		}
		return instance;
	}

	private GlobalRequestPatternFactory() {
		// TODO
	}
}
