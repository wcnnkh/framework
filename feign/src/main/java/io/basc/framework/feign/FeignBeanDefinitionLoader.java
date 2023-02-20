package io.basc.framework.feign;

import io.basc.framework.context.annotation.Provider;
import io.basc.framework.env.Environment;
import io.basc.framework.factory.BeanDefinition;
import io.basc.framework.factory.BeanFactory;
import io.basc.framework.factory.FactoryException;
import io.basc.framework.factory.support.BeanDefinitionLoader;
import io.basc.framework.factory.support.BeanDefinitionLoaderChain;
import io.basc.framework.feign.annotation.FeignClient;
import io.basc.framework.util.ClassUtils;

@Provider
public class FeignBeanDefinitionLoader implements BeanDefinitionLoader {

	@Override
	public BeanDefinition load(BeanFactory beanFactory, ClassLoader classLoader, String name,
			BeanDefinitionLoaderChain chain) throws FactoryException {
		Class<?> sourceClass = ClassUtils.getClass(name, classLoader);
		FeignClient feignClient = sourceClass.getAnnotation(FeignClient.class);
		if (feignClient != null) {
			return new FeignBeanDefinition(beanFactory.getInstance(Environment.class), sourceClass, feignClient);
		}
		return chain.load(beanFactory, classLoader, name);
	}
}
