package io.basc.framework.convert.lang;

import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.ConversionServiceAware;
import io.basc.framework.json.JSONSupportAccessor;

public abstract class AbstractConversionService extends JSONSupportAccessor implements ConversionService, ConversionServiceAware {
	private ConversionService conversionService;
	
	public ConversionService getConversionService() {
		return conversionService;
	}

	public void setConversionService(ConversionService conversionService) {
		this.conversionService = conversionService;
	}

	public String toString() {
		return getClass().getName();
	}
}