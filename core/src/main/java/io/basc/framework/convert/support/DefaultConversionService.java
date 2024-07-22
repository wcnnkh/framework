package io.basc.framework.convert.support;

import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.config.ConfigurableConversionService;

public class DefaultConversionService extends ConfigurableConversionService {

	public DefaultConversionService() {
		setLastService(GlobalConversionService.getInstance());
	}
}
