package scw.redis.core.beans;

import scw.beans.BeanFactoryPostProcessor;
import scw.beans.BeansException;
import scw.beans.ConfigurableBeanFactory;
import scw.context.annotation.Provider;
import scw.core.Ordered;
import scw.redis.core.RedisConfiguration;

@Provider(order = Ordered.HIGHEST_PRECEDENCE)
public class RedisBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

	@Override
	public void postProcessBeanFactory(ConfigurableBeanFactory beanFactory) throws BeansException {
		if (!beanFactory.containsDefinition(RedisConfiguration.class.getName())) {
			beanFactory.registerDefinition(new RedisConfigBeanDefinition(beanFactory));
		}
	}
}
