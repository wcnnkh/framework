package io.basc.framework.net.message.convert;

import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.ConversionServiceAware;
import io.basc.framework.factory.ServiceLoaderFactory;
import io.basc.framework.net.InetUtils;
import io.basc.framework.net.message.multipart.MultipartMessageConverter;

public class DefaultMessageConverters extends MessageConverters {
	private final ConversionService conversionService;

	public DefaultMessageConverters(ConversionService conversionService) {
		this.conversionService = conversionService;
		addService(new JsonMessageConverter());
		addService(new StringMessageConverter(conversionService));
		addService(new ByteArrayMessageConverter());
		addService(new HttpFormMessageConveter());
		addService(new MultipartMessageConverter(InetUtils.getMultipartMessageResolver()));
		addService(new ResourceMessageConverter());
	}

	@Override
	public void configure(ServiceLoaderFactory serviceLoaderFactory) {
		super.configure(serviceLoaderFactory);
	}

	public ConversionService getConversionService() {
		return conversionService;
	}

	@Override
	protected void aware(MessageConverter messageConverter) {
		if (messageConverter instanceof ConversionServiceAware) {
			((ConversionServiceAware) messageConverter).setConversionService(conversionService);
		}
		super.aware(messageConverter);
	}
}
