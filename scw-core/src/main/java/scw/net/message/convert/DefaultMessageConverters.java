package scw.net.message.convert;

import scw.convert.ConversionService;
import scw.instance.ServiceLoaderFactory;
import scw.net.InetUtils;

public class DefaultMessageConverters extends MessageConverters {

	public DefaultMessageConverters(ConversionService conversionService) {
		addMessageConverter(new JsonMessageConverter());
		addMessageConverter(new StringMessageConverter(conversionService));
		addMessageConverter(new ByteArrayMessageConverter());
		addMessageConverter(new XmlMessageConverter(conversionService));
		addMessageConverter(new HttpFormMessageConveter());
		addMessageConverter(new MultipartMessageConverter(
				InetUtils.getFileItemParser()));
		addMessageConverter(new ResourceMessageConverter());
	}

	public DefaultMessageConverters(ConversionService conversionService,
			ServiceLoaderFactory serviceLoaderFactory) {
		this(conversionService);
		for (MessageConverter messageConverter : serviceLoaderFactory
				.getServiceLoader(MessageConverter.class)) {
			addMessageConverter(messageConverter);
		}
	}
}
