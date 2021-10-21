package io.basc.framework.mvc.message.support;

import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.ConversionServiceAware;
import io.basc.framework.convert.lang.ConversionServices;
import io.basc.framework.core.parameter.ParameterFactory;
import io.basc.framework.factory.ServiceLoaderFactory;
import io.basc.framework.mvc.jaxrs2.Jaxrs2HeaderParamMessageConverter;
import io.basc.framework.mvc.jaxrs2.Jaxrs2ParamMessageConverter;
import io.basc.framework.mvc.message.WebMessageConverter;
import io.basc.framework.mvc.message.WebMessageConverters;
import io.basc.framework.net.message.convert.DefaultMessageConverters;
import io.basc.framework.net.message.convert.MessageConverters;

public class DefaultWebMessageConverters extends WebMessageConverters {
	private final DefaultMessageConverters messageConverters;

	public DefaultWebMessageConverters(ConversionService conversionService, ParameterFactory defaultValueFactory) {
		this.messageConverters = new DefaultMessageConverters(conversionService);
		setAfterService(new ConversionMessageConverter(getConversionServices(), defaultValueFactory));
		addService(new EntityMessageConverter(getMessageConverters()));
		addService(new InputMessageConverter());
		addService(new ResourceMessageConverter());
		addService(new AnnotationMessageConverter(defaultValueFactory));
		addService(new RequestBodyMessageConverter());
		addService(new QueryParamsMessageConverter());

		// jaxrs2
		addService(new Jaxrs2ParamMessageConverter(getConversionServices(), defaultValueFactory));
		addService(new Jaxrs2HeaderParamMessageConverter(getConversionServices(), defaultValueFactory));
		addService(new Jaxrs2HeaderParamMessageConverter(getConversionServices(), defaultValueFactory));
	}

	public MessageConverters getMessageConverters() {
		return messageConverters;
	}
	
	public ConversionServices getConversionServices(){
		return messageConverters.getConversionServices();
	}

	@Override
	public void configure(ServiceLoaderFactory serviceLoaderFactory) {
		messageConverters.configure(serviceLoaderFactory);
		super.configure(serviceLoaderFactory);
	}

	@Override
	protected void aware(WebMessageConverter converter) {
		if (converter instanceof ConversionServiceAware) {
			((ConversionServiceAware) converter).setConversionService(getConversionServices());
		}
		super.aware(converter);
	}
}
