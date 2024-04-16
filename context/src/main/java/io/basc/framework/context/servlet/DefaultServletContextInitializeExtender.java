package io.basc.framework.context.servlet;

import io.basc.framework.beans.factory.spi.SPI;

public class DefaultServletContextInitializeExtender extends ConfigurableServletContextInitializeExtender {
	public DefaultServletContextInitializeExtender() {
		registerServiceLoader(SPI.global().getServiceLoader(ServletContextInitializeExtender.class));
	}
}
