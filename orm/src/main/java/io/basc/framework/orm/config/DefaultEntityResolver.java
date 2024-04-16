package io.basc.framework.orm.config;

import io.basc.framework.beans.factory.spi.SPI;

public class DefaultEntityResolver extends ConfigurableEntityResolver {

	public DefaultEntityResolver() {
		getServiceLoaderRegistry().registerLast(SPI.global().getServiceLoader(EntityResolverExtend.class));
	}
}
