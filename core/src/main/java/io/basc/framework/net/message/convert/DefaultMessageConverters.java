package io.basc.framework.net.message.convert;

import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.ConversionServiceAware;
import io.basc.framework.convert.lang.ConversionServices;
import io.basc.framework.convert.support.DefaultConversionServices;
import io.basc.framework.factory.ServiceLoaderFactory;
import io.basc.framework.net.InetUtils;
import io.basc.framework.net.message.multipart.MultipartMessageConverter;

public class DefaultMessageConverters extends MessageConverters {
	private final ConversionServices conversionServices;

	public DefaultMessageConverters() {
		this.conversionServices = new DefaultConversionServices();
		afterConfigure();
	}

	public DefaultMessageConverters(ConversionService conversionService) {
		this.conversionServices = new ConversionServices();
		this.conversionServices.setAfterService(conversionService);
		afterConfigure();
	}

	protected void afterConfigure() {
		addService(new JsonMessageConverter());
		addService(new StringMessageConverter(conversionServices));
		addService(new ByteArrayMessageConverter());
		addService(new HttpFormMessageConveter());
		addService(new MultipartMessageConverter(
				InetUtils.getMultipartMessageResolver()));
		addService(new ResourceMessageConverter());
	}

	@Override
	public void configure(ServiceLoaderFactory serviceLoaderFactory) {
		if (conversionServices.getAfterService() == null) {
			conversionServices.configure(serviceLoaderFactory);
		}
		super.configure(serviceLoaderFactory);
	}

	public ConversionServices getConversionServices() {
		return conversionServices;
	}

	@Override
	protected void aware(MessageConverter messageConverter) {
		if (messageConverter instanceof ConversionServiceAware) {
			((ConversionServiceAware) messageConverter)
					.setConversionService(conversionServices);
		}
		super.aware(messageConverter);
	}
}
