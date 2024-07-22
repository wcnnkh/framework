package io.basc.framework.convert.lang;

import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.config.ConversionServiceAware;

public abstract class AbstractConversionService implements ConversionService, ConversionServiceAware {
	private ConversionService conversionService;

	public ConversionService getConversionService() {
		return conversionService;
	}

	public void setConversionService(ConversionService conversionService) {
		this.conversionService = conversionService;
	}
}