package io.basc.framework.feign;

import io.basc.framework.beans.BeansException;
import io.basc.framework.beans.ConfigurableBeanFactory;
import io.basc.framework.beans.support.DefaultBeanDefinition;
import io.basc.framework.net.message.convert.DefaultMessageConverters;
import io.basc.framework.net.message.convert.MessageConverter;
import io.basc.framework.util.StringUtils;
import feign.Feign;
import feign.codec.Decoder;
import feign.codec.Encoder;

public class FeignBeanDefinition extends DefaultBeanDefinition {
	private io.basc.framework.feign.annotation.FeignClient feignClient;

	public FeignBeanDefinition(ConfigurableBeanFactory beanFactory, Class<?> sourceClass,
			io.basc.framework.feign.annotation.FeignClient feignClient) {
		super(beanFactory, sourceClass);
		this.feignClient = feignClient;
	}

	private String getHost() {
		String host = feignClient.host();
		if (StringUtils.isEmpty(host)) {
			host = beanFactory.getEnvironment().getString("feign.host");
		} else {
			host = beanFactory.getEnvironment().resolvePlaceholders(host);
		}
		return host;
	}

	public boolean isInstance() {
		return StringUtils.isNotEmpty(getHost());
	}
	
	private MessageConverter getMessageConverter() {
		DefaultMessageConverters messageConverters = new DefaultMessageConverters(getEnvironment()
				.getConversionService());
		messageConverters.configure(beanFactory);
		return messageConverters;
	}

	@Override
	public Object create() throws BeansException {
		Encoder encoder = beanFactory.isInstance(Encoder.class) ? beanFactory
				.getInstance(Encoder.class) : new FeignEncoder(getMessageConverter());
		Decoder decoder = beanFactory.isInstance(Decoder.class) ? beanFactory
				.getInstance(Decoder.class) : new FeignDecoder(getMessageConverter());
		Object proxy = Feign.builder().encoder(encoder).decoder(decoder)
				.target(getTargetClass(), getHost());
		return beanFactory.getAop().getProxy(getTargetClass(), proxy).create();
	}
}
