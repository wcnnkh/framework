package io.basc.framework.web.message.support;

import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.ConversionServiceAware;
import io.basc.framework.core.parameter.ParameterFactory;
import io.basc.framework.factory.ServiceLoaderFactory;
import io.basc.framework.net.message.convert.DefaultMessageConverters;
import io.basc.framework.net.message.convert.MessageConverters;
import io.basc.framework.web.jaxrs2.Jaxrs2HeaderParamMessageConverter;
import io.basc.framework.web.jaxrs2.Jaxrs2ParamMessageConverter;
import io.basc.framework.web.message.WebMessageConverter;
import io.basc.framework.web.message.WebMessageConverters;

public class DefaultWebMessageConverters extends WebMessageConverters {
	private final DefaultMessageConverters messageConverters;

	public DefaultWebMessageConverters(ConversionService conversionService, ParameterFactory defaultValueFactory) {
		this.messageConverters = new DefaultMessageConverters(conversionService);
		setAfterService(new ConversionMessageConverter(messageConverters.getConversionServices(), defaultValueFactory));
		addService(new EntityMessageConverter(this.messageConverters));
		addService(new InputMessageConverter());
		addService(new ResourceMessageConverter());
		addService(new AnnotationMessageConverter(defaultValueFactory));
		addService(new RequestBodyMessageConverter());

		// jaxrs2
		addService(new Jaxrs2ParamMessageConverter(conversionService, defaultValueFactory));
		addService(new Jaxrs2HeaderParamMessageConverter(conversionService, defaultValueFactory));
		addService(new Jaxrs2HeaderParamMessageConverter(conversionService, defaultValueFactory));
	}

	public MessageConverters getMessageConverters() {
		return messageConverters;
	}

	@Override
	public void configure(ServiceLoaderFactory serviceLoaderFactory) {
		messageConverters.configure(serviceLoaderFactory);
		super.configure(serviceLoaderFactory);
	}

	@Override
	protected void aware(WebMessageConverter converter) {
		if (converter instanceof ConversionServiceAware) {
			((ConversionServiceAware) converter).setConversionService(messageConverters.getConversionServices());
		}
		super.aware(converter);
	}
}
