package run.soeasy.framework.core.convert.support;

import run.soeasy.framework.core.convert.ConversionService;
import run.soeasy.framework.core.convert.ConversionServiceAware;

public abstract class AbstractConversionService implements ConversionService, ConversionServiceAware {
	private ConversionService conversionService;

	public ConversionService getConversionService() {
		return conversionService;
	}

	public void setConversionService(ConversionService conversionService) {
		this.conversionService = conversionService;
	}
}