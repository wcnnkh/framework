package io.basc.framework.web.message.support;

import io.basc.framework.convert.ConversionServiceAware;
import io.basc.framework.env.Environment;
import io.basc.framework.env.EnvironmentAware;
import io.basc.framework.factory.DefaultParameterFactoryAware;
import io.basc.framework.factory.ServiceLoaderFactory;
import io.basc.framework.net.InetUtils;
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
	private final Environment environment;
	private final DefaultMessageConverters messageConverters;
	private final WebMessageConverters afters = new WebMessageConverters();

	public DefaultWebMessageConverters(Environment environment) {
		super.setLast(afters);
		afters.getConsumers().registerService(this);
		this.messageConverters = new DefaultMessageConverters(environment.getConversionService());
		this.environment = environment;
		LastWebMessageConverter lastWebMessageConverter = new LastWebMessageConverter();
		accept(lastWebMessageConverter);
		afters.setLast(lastWebMessageConverter);
		registerService(new MultipartMessageWebMessageConverter(InetUtils.getMultipartMessageResolver()));
		registerService(new EntityMessageConverter());
		registerService(new InputMessageConverter());
		registerService(new ResourceMessageConverter());
		registerService(new RequestBodyMessageConverter());
		registerService(new QueryParamsWebMessageConverter());
		registerService(new IpAddressWebMessageConverter());
		registerService(new AttributeWebMessageConverter());
	}

	@Override
	public void setLast(WebMessageConverter afterService) {
		afters.registerService(afterService);
	}

	public MessageConverters getMessageConverters() {
		return messageConverters;
	}

	@Override
	public void configure(ServiceLoaderFactory serviceLoaderFactory) {
		messageConverters.configure(serviceLoaderFactory);
		super.configure(serviceLoaderFactory);
	}

	@Override
	public void accept(WebMessageConverter service) {
		if (service instanceof EnvironmentAware) {
			((EnvironmentAware) service).setEnvironment(environment);
		}

		super.accept(service);

		if (service instanceof MessageConverterAware) {
			((MessageConverterAware) service).setMessageConverter(getMessageConverters());
		}

		if (service instanceof ConversionServiceAware) {
			((ConversionServiceAware) service).setConversionService(messageConverters.getConversionService());
		}
		
		if(service instanceof DefaultParameterFactoryAware) {
			((DefaultParameterFactoryAware) service).setDefaultParameterFactory(environment.getBeanResolver());
		}
	}
}
