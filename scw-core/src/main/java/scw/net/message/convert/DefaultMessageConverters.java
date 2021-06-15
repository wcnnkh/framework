package scw.net.message.convert;

import scw.convert.ConversionService;
import scw.convert.ConversionServiceAware;
import scw.instance.ServiceLoaderFactory;
import scw.net.InetUtils;
import scw.net.message.multipart.MultipartMessageConverter;

public class DefaultMessageConverters extends MessageConverters {
	private final ConversionService conversionService;

	public DefaultMessageConverters(ConversionService conversionService) {
		this.conversionService = conversionService;
		addMessageConverter(new JsonMessageConverter());
		addMessageConverter(new StringMessageConverter(conversionService));
		addMessageConverter(new ByteArrayMessageConverter());
		addMessageConverter(new HttpFormMessageConveter());
		addMessageConverter(new MultipartMessageConverter(InetUtils.getMultipartMessageResolver()));
		addMessageConverter(new ResourceMessageConverter());
	}

	public DefaultMessageConverters(ConversionService conversionService, ServiceLoaderFactory serviceLoaderFactory) {
		this(conversionService);
		for (MessageConverter messageConverter : serviceLoaderFactory.getServiceLoader(MessageConverter.class)) {
			addMessageConverter(messageConverter);
		}
	}

	@Override
	protected void aware(MessageConverter messageConverter) {
		if (messageConverter instanceof ConversionServiceAware) {
			((ConversionServiceAware) messageConverter).setConversionService(conversionService);
		}
		super.aware(messageConverter);
	}
}
