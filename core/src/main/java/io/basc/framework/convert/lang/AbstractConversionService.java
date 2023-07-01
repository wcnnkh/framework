package io.basc.framework.convert.lang;

import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.config.ConversionServiceAware;
import io.basc.framework.json.JsonSupportAccessor;

public abstract class AbstractConversionService extends JsonSupportAccessor
		implements ConversionService, ConversionServiceAware {
	private ConversionService conversionService;

	public ConversionService getConversionService() {
		return conversionService;
	}

	public void setConversionService(ConversionService conversionService) {
		this.conversionService = conversionService;
	}
}