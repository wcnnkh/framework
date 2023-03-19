package io.basc.framework.feign.context;

import feign.Feign;
import feign.codec.Decoder;
import feign.codec.Encoder;
import io.basc.framework.env.Environment;
import io.basc.framework.env.EnvironmentBeanDefinition;
import io.basc.framework.factory.BeansException;
import io.basc.framework.feign.FeignDecoder;
import io.basc.framework.feign.FeignEncoder;
import io.basc.framework.feign.context.annotation.FeignClient;
import io.basc.framework.net.message.convert.DefaultMessageConverters;
import io.basc.framework.net.message.convert.MessageConverter;
import io.basc.framework.util.StringUtils;

public class FeignBeanDefinition extends EnvironmentBeanDefinition {
	private FeignClient feignClient;

	public FeignBeanDefinition(Environment environment, Class<?> sourceClass, FeignClient feignClient) {
		super(environment, sourceClass);
		this.feignClient = feignClient;
	}

	private String getHost() {
		String host = feignClient.host();
		if (StringUtils.isEmpty(host)) {
			host = getEnvironment().getProperties().getAsString("feign.host");
		} else {
			host = getEnvironment().replacePlaceholders(host);
		}
		return host;
	}

	public boolean isInstance() {
		return StringUtils.isNotEmpty(getHost());
	}

	private MessageConverter getMessageConverter() {
		DefaultMessageConverters messageConverters = new DefaultMessageConverters(
				getEnvironment().getConversionService());
		messageConverters.configure(getEnvironment());
		return messageConverters;
	}

	@Override
	public Object create() throws BeansException {
		Encoder encoder = getBeanFactory().isInstance(Encoder.class) ? getBeanFactory().getInstance(Encoder.class)
				: new FeignEncoder(getMessageConverter());
		Decoder decoder = getBeanFactory().isInstance(Decoder.class) ? getBeanFactory().getInstance(Decoder.class)
				: new FeignDecoder(getMessageConverter());
		Object proxy = Feign.builder().encoder(encoder).decoder(decoder).target(getTypeDescriptor().getType(),
				getHost());
		return getBeanFactory().getAop().getProxy(getTypeDescriptor().getType(), proxy).create();
	}
}
