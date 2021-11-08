package io.basc.framework.web.message.support;

import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.ConversionServiceAware;
import io.basc.framework.convert.lang.ConversionServices;
import io.basc.framework.core.parameter.ParameterFactory;
import io.basc.framework.factory.ServiceLoaderFactory;
import io.basc.framework.net.message.convert.DefaultMessageConverters;
import io.basc.framework.net.message.convert.MessageConverterAware;
import io.basc.framework.net.message.convert.MessageConverters;
import io.basc.framework.web.message.WebMessageConverter;
import io.basc.framework.web.message.WebMessageConverters;
import io.basc.framework.web.message.annotation.AttributeWebMessageConverter;
import io.basc.framework.web.message.annotation.IpAddressWebMessageConverter;
import io.basc.framework.web.message.annotation.QueryParamsWebMessageConverter;
import io.basc.framework.web.message.annotation.RequestBodyMessageConverter;

public class DefaultWebMessageConverters extends WebMessageConverters {
	private final DefaultMessageConverters messageConverters;
	private final ParameterFactory defaultValueFactory;
	private final WebMessageConverters afters = new WebMessageConverters();

	public DefaultWebMessageConverters(ConversionService conversionService,
			ParameterFactory defaultValueFactory) {
		super.setAfterService(afters);
		this.messageConverters = new DefaultMessageConverters(conversionService);
		this.defaultValueFactory = defaultValueFactory;
		LastWebMessageConverter lastWebMessageConverter = new LastWebMessageConverter();
		aware(lastWebMessageConverter);
		afters.setAfterService(lastWebMessageConverter);
		addService(new EntityMessageConverter());
		addService(new InputMessageConverter());
		addService(new ResourceMessageConverter());
		addService(new RequestBodyMessageConverter());
		addService(new QueryParamsWebMessageConverter());
		addService(new IpAddressWebMessageConverter());
		addService(new AttributeWebMessageConverter());
	}

	@Override
	public void setAfterService(WebMessageConverter afterService) {
		afters.addService(afterService);
	}

	public MessageConverters getMessageConverters() {
		return messageConverters;
	}

	public ConversionServices getConversionServices() {
		return messageConverters.getConversionServices();
	}

	public ParameterFactory getDefaultValueFactory() {
		return defaultValueFactory;
	}

	@Override
	public void configure(ServiceLoaderFactory serviceLoaderFactory) {
		messageConverters.configure(serviceLoaderFactory);
		super.configure(serviceLoaderFactory);
	}

	@Override
	protected void aware(WebMessageConverter converter) {
		if (converter instanceof ConversionServiceAware) {
			((ConversionServiceAware) converter)
					.setConversionService(getConversionServices());
		}

		if (converter instanceof DefaultValueFactoryAware) {
			((DefaultValueFactoryAware) converter)
					.setDefaultValueFactory(getDefaultValueFactory());
		}

		if (converter instanceof MessageConverterAware) {
			((MessageConverterAware) converter)
					.setMessageConverter(getMessageConverters());
		}
		super.aware(converter);
	}
}
