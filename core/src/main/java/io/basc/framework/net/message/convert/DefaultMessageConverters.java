package io.basc.framework.net.message.convert;

import io.basc.framework.beans.factory.ServiceLoaderFactory;
import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.config.ConversionServiceAware;
import io.basc.framework.convert.config.support.ConfigurableConversionService;
import io.basc.framework.convert.support.DefaultConversionService;
import io.basc.framework.net.InetUtils;
import io.basc.framework.net.message.multipart.MultipartMessageConverter;
import io.basc.framework.util.Registration;

public class DefaultMessageConverters extends MessageConverters {
	private final ConfigurableConversionService conversionService;

	public DefaultMessageConverters() {
		this.conversionService = new DefaultConversionService();
		afterConfigure();
	}

	public DefaultMessageConverters(ConversionService conversionService) {
		this.conversionService = new ConfigurableConversionService();
		this.conversionService.getRegistry().registerLast(conversionService);
		afterConfigure();
	}

	protected void afterConfigure() {
		getServiceInjectorRegistry().register((service) -> {
			if (service instanceof ConversionServiceAware) {
				((ConversionServiceAware) service).setConversionService(conversionService);
			}
			return Registration.EMPTY;
		});
		register(new JsonMessageConverter());
		register(new StringMessageConverter(conversionService));
		register(new ByteArrayMessageConverter());
		register(new HttpFormMessageConveter());
		register(new MultipartMessageConverter(InetUtils.getMultipartMessageResolver()));
		register(new ResourceMessageConverter());
	}

	@Override
	public void configure(ServiceLoaderFactory serviceLoaderFactory) {
		if (!conversionService.isConfigured()) {
			conversionService.configure(serviceLoaderFactory);
		}
		super.configure(serviceLoaderFactory);
	}

	public ConfigurableConversionService getConversionService() {
		return conversionService;
	}
}
