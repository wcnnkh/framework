package io.basc.framework.web.message.support;

import io.basc.framework.beans.factory.ServiceLoaderFactory;
import io.basc.framework.core.convert.config.ConversionServiceAware;
import io.basc.framework.core.env.Environment;
import io.basc.framework.core.env.EnvironmentAware;
import io.basc.framework.net.convert.MessageConverterAware;
import io.basc.framework.net.convert.support.DefaultMessageConverters;
import io.basc.framework.util.register.Registration;
import io.basc.framework.web.message.WebMessageConverter;
import io.basc.framework.web.message.WebMessageConverters;
import io.basc.framework.web.message.annotation.AttributeWebMessageConverter;
import io.basc.framework.web.message.annotation.IpAddressWebMessageConverter;
import io.basc.framework.web.message.annotation.QueryParamsWebMessageConverter;
import io.basc.framework.web.message.annotation.RequestBodyMessageConverter;

public class DefaultWebMessageConverters extends WebMessageConverters {
	private final DefaultMessageConverters messageConverters;
	private final WebMessageConverters afters = new WebMessageConverters();

	public DefaultWebMessageConverters(Environment environment) {
		getServiceInjectors().register((service) -> {
			if (service instanceof EnvironmentAware) {
				((EnvironmentAware) service).setEnvironment(environment);
			}

			if (service instanceof MessageConverterAware) {
				((MessageConverterAware) service).setMessageConverter(getMessageConverters());
			}

			if (service instanceof ConversionServiceAware) {
				((ConversionServiceAware) service).setConversionService(getMessageConverters().getConversionService());
			}
			return Registration.EMPTY;
		});
		super.registerLast(afters);
		afters.getServiceInjectors().register(getServiceInjectors());
		this.messageConverters = new DefaultMessageConverters(environment.getConversionService());
		LastWebMessageConverter lastWebMessageConverter = new LastWebMessageConverter();
		getServiceInjectors().inject(lastWebMessageConverter);
		afters.registerLast(lastWebMessageConverter);
		register(new MultipartMessageWebMessageConverter());
		register(new EntityMessageConverter());
		register(new InputMessageConverter());
		register(new ResourceMessageConverter());
		register(new RequestBodyMessageConverter());
		register(new QueryParamsWebMessageConverter());
		register(new IpAddressWebMessageConverter());
		register(new AttributeWebMessageConverter());
	}

	@Override
	public Registration registerLast(WebMessageConverter afterService) {
		return afters.register(afterService);
	}

	public DefaultMessageConverters getMessageConverters() {
		return messageConverters;
	}

	@Override
	public void configure(ServiceLoaderFactory serviceLoaderFactory) {
		messageConverters.configure(serviceLoaderFactory);
		super.configure(serviceLoaderFactory);
	}
}
