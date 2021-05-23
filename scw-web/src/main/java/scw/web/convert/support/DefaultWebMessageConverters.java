package scw.web.convert.support;

import scw.convert.ConversionService;
import scw.instance.ServiceLoaderFactory;
import scw.net.message.convert.DefaultMessageConverters;
import scw.net.message.convert.MessageConverter;
import scw.web.convert.WebMessageConverter;
import scw.web.convert.WebMessageConverters;

public class DefaultWebMessageConverters extends WebMessageConverters {

	public DefaultWebMessageConverters(ConversionService conversionService, ServiceLoaderFactory serviceLoaderFactory) {
		super(new ConversionWebMessageConverters(conversionService));
		MessageConverter messageConverter = new DefaultMessageConverters(conversionService, serviceLoaderFactory);
		addMessageConverter(new EntityMessageConverter(messageConverter));
		addMessageConverter(new InputMessageConverter());
		addMessageConverter(new ResourceMessageConverter());

		for (WebMessageConverter converter : serviceLoaderFactory.getServiceLoader(WebMessageConverter.class)) {
			addMessageConverter(converter);
		}
	}
}
