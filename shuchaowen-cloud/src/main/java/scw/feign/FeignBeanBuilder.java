package scw.feign;

import scw.beans.builder.ConstructorBeanDefinition;
import scw.beans.builder.LoaderContext;
import scw.core.instance.ConstructorBuilder;
import scw.core.utils.StringUtils;
import scw.net.InetUtils;
import feign.Feign;
import feign.codec.Decoder;
import feign.codec.Encoder;

public class FeignBeanBuilder extends ConstructorBeanDefinition {
	private scw.feign.annotation.FeignClient feignClient;

	public FeignBeanBuilder(LoaderContext context, scw.feign.annotation.FeignClient feignClient) {
		super(context);
		this.feignClient = feignClient;
	}

	@Override
	protected ConstructorBuilder getConstructorBuilder() {
		return null;
	}
	
	private String getHost(){
		String host = feignClient.host();
		if(StringUtils.isEmpty(host)){
			host = propertyFactory.getString("feign.host");
		}else{
			host = propertyFactory.format(host, true);
		}
		return host;
	}

	public boolean isInstance() {
		return StringUtils.isNotEmpty(getHost());
	}

	@Override
	public Object create() throws Exception {
		FeignCodec codec = new FeignCodec(InetUtils.getMessageConverter());
		Encoder encoder = beanFactory.isInstance(Encoder.class) ? beanFactory.getInstance(Encoder.class) : codec;
		Decoder decoder = beanFactory.isInstance(Decoder.class) ? beanFactory.getInstance(Decoder.class) : codec;
		Object proxy = Feign.builder().encoder(encoder).decoder(decoder).target(getTargetClass(), getHost());
		return beanFactory.getAop().getProxyInstance(getTargetClass(), proxy, null).create();
	}
}
