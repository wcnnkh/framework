package io.basc.framework.mvc.message.support;

import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.ConversionServiceAware;
import io.basc.framework.convert.lang.ConversionServices;
import io.basc.framework.core.parameter.ParameterFactory;
import io.basc.framework.factory.ServiceLoaderFactory;
import io.basc.framework.mvc.jaxrs2.Jaxrs2HeaderParamMessageConverter;
import io.basc.framework.mvc.jaxrs2.Jaxrs2ParamMessageConverter;
import io.basc.framework.net.message.convert.DefaultMessageConverters;
import io.basc.framework.net.message.convert.MessageConverters;
import io.basc.framework.web.message.WebMessageConverter;
import io.basc.framework.web.message.support.AnnotationMessageConverter;
import io.basc.framework.web.message.support.ConversionMessageConverter;
import io.basc.framework.web.message.support.DefaultWebMessageConverters;
import io.basc.framework.web.message.support.EntityMessageConverter;
import io.basc.framework.web.message.support.InputMessageConverter;
import io.basc.framework.web.message.support.QueryParamsMessageConverter;
import io.basc.framework.web.message.support.RequestBodyMessageConverter;

public class MvcMessageConverters extends DefaultWebMessageConverters {

	public MvcMessageConverters(ConversionService conversionService, ParameterFactory defaultValueFactory) {
		super(conversionService, defaultValueFactory);
		// jaxrs2
		addService(new Jaxrs2ParamMessageConverter(getConversionServices(), defaultValueFactory));
		addService(new Jaxrs2HeaderParamMessageConverter(getConversionServices(), defaultValueFactory));
		addService(new Jaxrs2HeaderParamMessageConverter(getConversionServices(), defaultValueFactory));
	}
}
