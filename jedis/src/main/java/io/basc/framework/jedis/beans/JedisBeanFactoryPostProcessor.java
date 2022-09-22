package io.basc.framework.jedis.beans;

import io.basc.framework.context.annotation.Provider;
import io.basc.framework.core.Ordered;
import io.basc.framework.factory.BeanFactoryPostProcessor;
import io.basc.framework.factory.BeanlifeCycleEvent.Step;
import io.basc.framework.factory.BeansException;
import io.basc.framework.factory.ConfigurableBeanFactory;
import io.basc.framework.redis.RedisClient;
import redis.clients.jedis.JedisPool;

@Provider(order = Ordered.LOWEST_PRECEDENCE)
public class JedisBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

	public void postProcessBeanFactory(ConfigurableBeanFactory beanFactory) throws BeansException {
		if (!beanFactory.containsDefinition(JedisPool.class.getName())) {
			beanFactory.registerDefinition(new JedisPoolDefinition(beanFactory));
		}

		JedisConnectionFactoryDefinition jedisConnectionFactoryDefinition = new JedisConnectionFactoryDefinition(
				beanFactory);
		if (!beanFactory.containsDefinition(jedisConnectionFactoryDefinition.getId())) {
			beanFactory.registerDefinition(jedisConnectionFactoryDefinition);

			if (beanFactory.isAlias(RedisClient.class.getName())) {
				beanFactory.registerAlias(jedisConnectionFactoryDefinition.getId(), RedisClient.class.getName());
			}
		}

		beanFactory.registerListener((e) -> {
			if (e.getStep() == Step.AFTER_DESTROY) {
				if (e.getBean() != null && e.getBean() instanceof JedisPool) {
					JedisPool jedisPool = (JedisPool) e.getBean();
					if (!jedisPool.isClosed()) {
						jedisPool.close();
					}
				}
			}
		});
	}
}
