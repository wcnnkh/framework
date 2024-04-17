package io.basc.framework.orm.config;

import io.basc.framework.beans.factory.spi.SPI;
import io.basc.framework.orm.stereotype.AnnotationAnalyzeExtender;

public class DefaultAnalyzer extends ConfigurableAnalyzer {

	public DefaultAnalyzer() {
		getServiceLoaderRegistry().registerLast(SPI.global().getServiceLoader(AnalyzeExtender.class));
		register(new AnnotationAnalyzeExtender());
	}
}
