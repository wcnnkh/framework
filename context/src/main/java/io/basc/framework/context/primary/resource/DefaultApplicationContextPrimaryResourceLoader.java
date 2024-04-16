package io.basc.framework.context.primary.resource;

import io.basc.framework.beans.factory.spi.SPI;

public class DefaultApplicationContextPrimaryResourceLoader
		extends ConfigurableApplicationContextPrimaryResourceLoader {

	public DefaultApplicationContextPrimaryResourceLoader() {
		registerServiceLoader(SPI.global().getServiceLoader(ApplicationContextPrimaryResourceLoader.class));
		getExtender().registerServiceLoader(
				SPI.global().getServiceLoader(ApplicationContextPrimaryResourceLoadExtender.class));
	}
}
