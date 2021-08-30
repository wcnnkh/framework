package io.basc.framework.feign;

import io.basc.framework.beans.BeanDefinition;
import io.basc.framework.beans.BeanDefinitionLoader;
import io.basc.framework.beans.BeanDefinitionLoaderChain;
import io.basc.framework.beans.ConfigurableBeanFactory;
import io.basc.framework.context.annotation.Provider;
import io.basc.framework.feign.annotation.FeignClient;

@Provider
public class FeignBeanDefinitionLoader implements BeanDefinitionLoader {

	public BeanDefinition load(ConfigurableBeanFactory beanFactory, Class<?> sourceClass, BeanDefinitionLoaderChain loaderChain) {
		FeignClient feignClient = sourceClass.getAnnotation(FeignClient.class);
		if (feignClient != null) {
			return new FeignBeanDefinition(beanFactory, sourceClass, feignClient);
		}
		return loaderChain.load(beanFactory, sourceClass);
	}
}
