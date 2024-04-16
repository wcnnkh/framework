package io.basc.framework.context.config;

import io.basc.framework.beans.factory.spi.SPI;

public class DefaultApplicationContextInitializer extends ConfigurableApplicationContextInitializer {

	public DefaultApplicationContextInitializer() {
		registerServiceLoader(SPI.global().getServiceLoader(ApplicationContextInitializer.class));
	}
}
