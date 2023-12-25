package io.basc.framework.beans.factory.component;

import io.basc.framework.beans.factory.spi.SPI;

class DefaultComponentResolvers extends ComponentResolvers {

	public DefaultComponentResolvers() {
		configure(SPI.global());
	}
}
