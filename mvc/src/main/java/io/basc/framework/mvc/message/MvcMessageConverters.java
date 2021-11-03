package io.basc.framework.mvc.message;

import io.basc.framework.convert.ConversionService;
import io.basc.framework.core.parameter.ParameterFactory;
import io.basc.framework.mvc.jaxrs2.Jaxrs2HeaderParamMessageConverter;
import io.basc.framework.mvc.jaxrs2.Jaxrs2ParamMessageConverter;
import io.basc.framework.web.message.support.DefaultWebMessageConverters;

public class MvcMessageConverters extends DefaultWebMessageConverters {

	public MvcMessageConverters(ConversionService conversionService, ParameterFactory defaultValueFactory) {
		super(conversionService, defaultValueFactory);
		// jaxrs2
		addService(new Jaxrs2ParamMessageConverter(getConversionServices(), defaultValueFactory));
		addService(new Jaxrs2HeaderParamMessageConverter(getConversionServices(), defaultValueFactory));
		addService(new Jaxrs2HeaderParamMessageConverter(getConversionServices(), defaultValueFactory));
	}
}
