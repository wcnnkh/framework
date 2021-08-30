package io.basc.framework.redis.core.beans;

import io.basc.framework.beans.BeanFactoryPostProcessor;
import io.basc.framework.beans.BeansException;
import io.basc.framework.beans.ConfigurableBeanFactory;
import io.basc.framework.context.annotation.Provider;
import io.basc.framework.core.Ordered;
import io.basc.framework.redis.core.RedisConfiguration;

@Provider(order = Ordered.HIGHEST_PRECEDENCE)
public class RedisBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

	@Override
	public void postProcessBeanFactory(ConfigurableBeanFactory beanFactory) throws BeansException {
		if (!beanFactory.containsDefinition(RedisConfiguration.class.getName())) {
			beanFactory.registerDefinition(new RedisConfigBeanDefinition(beanFactory));
		}
	}
}
