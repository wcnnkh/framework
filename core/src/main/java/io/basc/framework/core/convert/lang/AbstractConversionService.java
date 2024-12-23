package io.basc.framework.core.convert.lang;

import io.basc.framework.core.convert.service.ConversionService;
import io.basc.framework.core.convert.service.ConversionServiceAware;

public abstract class AbstractConversionService implements ConversionService, ConversionServiceAware {
	private ConversionService conversionService;

	public ConversionService getConversionService() {
		return conversionService;
	}

	public void setConversionService(ConversionService conversionService) {
		this.conversionService = conversionService;
	}
}