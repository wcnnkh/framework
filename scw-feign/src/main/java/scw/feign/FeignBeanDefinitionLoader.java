package scw.feign;

import scw.beans.BeanDefinition;
import scw.beans.BeanDefinitionLoader;
import scw.beans.BeanDefinitionLoaderChain;
import scw.beans.BeanFactory;
import scw.context.annotation.Provider;
import scw.feign.annotation.FeignClient;

@Provider(order=Integer.MIN_VALUE)
public class FeignBeanDefinitionLoader implements BeanDefinitionLoader {

	public BeanDefinition load(BeanFactory beanFactory, Class<?> sourceClass, BeanDefinitionLoaderChain loaderChain) {
		FeignClient feignClient = sourceClass.getAnnotation(FeignClient.class);
		if (feignClient != null) {
			return new FeignBeanDefinition(beanFactory, sourceClass, feignClient);
		}
		return loaderChain.load(beanFactory, sourceClass);
	}
}
