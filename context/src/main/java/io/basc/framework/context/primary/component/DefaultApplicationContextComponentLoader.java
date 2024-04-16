package io.basc.framework.context.primary.component;

import io.basc.framework.beans.factory.spi.SPI;

public class DefaultApplicationContextComponentLoader extends ConfigurableApplicationContextComponentLoader {

	public DefaultApplicationContextComponentLoader() {
		registerServiceLoader(SPI.global().getServiceLoader(ApplicationContextComponentLoader.class));
		getExtender()
				.registerServiceLoader(SPI.global().getServiceLoader(ApplicationContextComponentLoadExtender.class));
	}
}
