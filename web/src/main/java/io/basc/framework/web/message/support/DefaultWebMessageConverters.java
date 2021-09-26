package io.basc.framework.web.message.support;

import io.basc.framework.convert.ConversionService;
import io.basc.framework.core.parameter.ParameterFactory;
import io.basc.framework.factory.ServiceLoaderFactory;
import io.basc.framework.net.message.convert.DefaultMessageConverters;
import io.basc.framework.net.message.convert.MessageConverters;
import io.basc.framework.web.jaxrs2.Jaxrs2HeaderParamMessageConverter;
import io.basc.framework.web.jaxrs2.Jaxrs2ParamMessageConverter;
import io.basc.framework.web.message.WebMessageConverters;

public class DefaultWebMessageConverters extends WebMessageConverters {
	private final MessageConverters messageConverters;

	public DefaultWebMessageConverters(ConversionService conversionService, ParameterFactory defaultValueFactory) {
		super(new ConversionMessageConverter(conversionService, defaultValueFactory));
		this.messageConverters = new DefaultMessageConverters(conversionService);
		setConversionService(conversionService);
		addService(new EntityMessageConverter(messageConverters));
		addService(new InputMessageConverter());
		addService(new ResourceMessageConverter());
		addService(new AnnotationMessageConverter(defaultValueFactory));
		addService(new RequestBodyMessageConverter());

		// jaxrs2
		addService(new Jaxrs2ParamMessageConverter(conversionService, defaultValueFactory));
		addService(new Jaxrs2HeaderParamMessageConverter(conversionService, defaultValueFactory));
		addService(new Jaxrs2HeaderParamMessageConverter(conversionService, defaultValueFactory));
	}

	@Override
	public void configure(ServiceLoaderFactory serviceLoaderFactory) {
		messageConverters.configure(serviceLoaderFactory);
		super.configure(serviceLoaderFactory);
	}
}
