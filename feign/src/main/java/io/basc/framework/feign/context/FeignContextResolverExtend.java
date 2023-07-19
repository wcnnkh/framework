package io.basc.framework.feign.context;

import io.basc.framework.beans.factory.config.BeanDefinition;
import io.basc.framework.context.Context;
import io.basc.framework.context.ContextResolver;
import io.basc.framework.context.ContextResolverExtend;
import io.basc.framework.context.annotation.ConditionalOnParameters;
import io.basc.framework.feign.context.annotation.FeignClient;

@ConditionalOnParameters
public class FeignContextResolverExtend implements ContextResolverExtend {
	private final Context context;

	public FeignContextResolverExtend(Context context) {
		this.context = context;
	}

	@Override
	public BeanDefinition resolveBeanDefinition(Class<?> sourceClass, ContextResolver chain) {
		FeignClient feignClient = sourceClass.getAnnotation(FeignClient.class);
		if (feignClient != null) {
			return new FeignBeanDefinition(context, sourceClass, feignClient);
		}
		return ContextResolverExtend.super.resolveBeanDefinition(sourceClass, chain);
	}
}
