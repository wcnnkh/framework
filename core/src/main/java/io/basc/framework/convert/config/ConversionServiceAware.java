package io.basc.framework.convert.config;

import io.basc.framework.convert.ConversionService;

public interface ConversionServiceAware {
	void setConversionService(ConversionService conversionService);
}
