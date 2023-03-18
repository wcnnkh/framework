package io.basc.framework.feign.context;

import java.util.Arrays;
import java.util.Collection;

import io.basc.framework.context.Context;
import io.basc.framework.context.ContextResolver;
import io.basc.framework.context.ContextResolverExtend;
import io.basc.framework.context.annotation.Provider;
import io.basc.framework.factory.BeanDefinition;
import io.basc.framework.feign.context.annotation.FeignClient;

@Provider
public class FeignContextResolverExtend implements ContextResolverExtend {
	private final Context context;

	public FeignContextResolverExtend(Context context) {
		this.context = context;
	}

	@Override
	public Collection<BeanDefinition> resolveBeanDefinitions(Class<?> clazz, ContextResolver chain) {
		FeignClient feignClient = clazz.getAnnotation(FeignClient.class);
		if (feignClient == null) {
			return chain.resolveBeanDefinitions(clazz);
		}

		return Arrays.asList(new FeignBeanDefinition(context, clazz, feignClient));
	}
}
