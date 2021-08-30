package io.basc.framework.web.message.support;

import io.basc.framework.convert.ConversionService;
import io.basc.framework.factory.ServiceLoaderFactory;
import io.basc.framework.net.message.convert.DefaultMessageConverters;
import io.basc.framework.net.message.convert.MessageConverter;
import io.basc.framework.web.message.WebMessageConverter;
import io.basc.framework.web.message.WebMessageConverters;

public class DefaultWebMessageConverters extends WebMessageConverters {

	public DefaultWebMessageConverters(ConversionService conversionService, ServiceLoaderFactory serviceLoaderFactory) {
		super(new ConversionMessageConverter(conversionService));
		setConversionService(conversionService);
		MessageConverter messageConverter = new DefaultMessageConverters(conversionService, serviceLoaderFactory);
		addMessageConverter(new EntityMessageConverter(messageConverter));
		addMessageConverter(new InputMessageConverter());
		addMessageConverter(new ResourceMessageConverter());
		addMessageConverter(new AnnotationMessageConverter());
		addMessageConverter(new RequestBodyMessageConverter());

		for (WebMessageConverter converter : serviceLoaderFactory.getServiceLoader(WebMessageConverter.class)) {
			addMessageConverter(converter);
		}
	}
}
