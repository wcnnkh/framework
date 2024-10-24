package io.basc.framework.net.message.convert;

import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.ConversionServiceAware;
import io.basc.framework.convert.lang.ConfigurableConversionService;
import io.basc.framework.convert.support.DefaultConversionService;
import io.basc.framework.factory.ServiceLoaderFactory;
import io.basc.framework.net.InetUtils;
import io.basc.framework.net.message.multipart.MultipartMessageConverter;

public class DefaultMessageConverters extends MessageConverters {
	private final ConfigurableConversionService conversionService;

	public DefaultMessageConverters() {
		this.conversionService = new DefaultConversionService();
		afterConfigure();
	}

	public DefaultMessageConverters(ConversionService conversionService) {
		this.conversionService = new ConfigurableConversionService();
		this.conversionService.setAfterService(conversionService);
		afterConfigure();
	}

	protected void afterConfigure() {
		addService(new JsonMessageConverter());
		addService(new StringMessageConverter(conversionService));
		addService(new ByteArrayMessageConverter());
		addService(new HttpFormMessageConveter());
		addService(new MultipartMessageConverter(InetUtils.getMultipartMessageResolver()));
		addService(new ResourceMessageConverter());
	}

	@Override
	public void configure(ServiceLoaderFactory serviceLoaderFactory) {
		if (conversionService.getAfterService() == null) {
			conversionService.configure(serviceLoaderFactory);
		}
		super.configure(serviceLoaderFactory);
	}

	public ConfigurableConversionService getConversionService() {
		return conversionService;
	}

	@Override
	public void accept(MessageConverter service) {
		if (service instanceof ConversionServiceAware) {
			((ConversionServiceAware) service).setConversionService(conversionService);
		}
		super.accept(service);
	}

}
