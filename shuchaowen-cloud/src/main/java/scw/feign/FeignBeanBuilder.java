package scw.feign;

import feign.Feign;
import feign.codec.Decoder;
import feign.codec.Encoder;
import scw.beans.builder.ConstructorBeanBuilder;
import scw.beans.builder.LoaderContext;
import scw.core.instance.ConstructorBuilder;
import scw.net.NetworkUtils;

public class FeignBeanBuilder extends ConstructorBeanBuilder {
	private scw.feign.annotation.FeignClient feignClient;

	public FeignBeanBuilder(LoaderContext context, scw.feign.annotation.FeignClient feignClient) {
		super(context);
		this.feignClient = feignClient;
	}

	@Override
	protected ConstructorBuilder getConstructorBuilder() {
		return null;
	}

	public boolean isInstance() {
		return true;
	}

	@Override
	public Object create() throws Exception {
		FeignCodec codec = new FeignCodec(NetworkUtils.getMessageConverters());
		Encoder encoder = beanFactory.isInstance(Encoder.class) ? beanFactory.getInstance(Encoder.class) : codec;
		Decoder decoder = beanFactory.isInstance(Decoder.class) ? beanFactory.getInstance(Decoder.class) : codec;
		Object proxy = Feign.builder().encoder(encoder).decoder(decoder).target(getTargetClass(), feignClient.host());
		return beanFactory.getAop().getProxyInstance(getTargetClass(), proxy, null, null).create();
	}
}
