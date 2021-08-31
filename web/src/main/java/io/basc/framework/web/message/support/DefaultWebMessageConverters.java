package io.basc.framework.web.message.support;

import io.basc.framework.convert.ConversionService;
import io.basc.framework.core.parameter.ParameterDefaultValueFactory;
import io.basc.framework.factory.ServiceLoaderFactory;
import io.basc.framework.net.message.convert.DefaultMessageConverters;
import io.basc.framework.net.message.convert.MessageConverter;
import io.basc.framework.web.message.WebMessageConverter;
import io.basc.framework.web.message.WebMessageConverters;

public class DefaultWebMessageConverters extends WebMessageConverters {

	public DefaultWebMessageConverters(ConversionService conversionService, ServiceLoaderFactory serviceLoaderFactory, ParameterDefaultValueFactory defaultValueFactory) {
		super(new ConversionMessageConverter(conversionService, defaultValueFactory));
		setConversionService(conversionService);
		MessageConverter messageConverter = new DefaultMessageConverters(conversionService, serviceLoaderFactory);
		addMessageConverter(new EntityMessageConverter(messageConverter));
		addMessageConverter(new InputMessageConverter());
		addMessageConverter(new ResourceMessageConverter());
		addMessageConverter(new AnnotationMessageConverter(defaultValueFactory));
		addMessageConverter(new RequestBodyMessageConverter());

		for (WebMessageConverter converter : serviceLoaderFactory.getServiceLoader(WebMessageConverter.class)) {
			addMessageConverter(converter);
		}
	}
}
