package scw.web.message.support;

import scw.convert.ConversionService;
import scw.instance.ServiceLoaderFactory;
import scw.net.message.convert.DefaultMessageConverters;
import scw.net.message.convert.MessageConverter;
import scw.web.message.WebMessageConverter;
import scw.web.message.WebMessageConverters;

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
