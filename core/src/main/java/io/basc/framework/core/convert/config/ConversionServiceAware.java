package io.basc.framework.core.convert.config;

import io.basc.framework.core.convert.ConversionService;

public interface ConversionServiceAware {
	void setConversionService(ConversionService conversionService);
}
