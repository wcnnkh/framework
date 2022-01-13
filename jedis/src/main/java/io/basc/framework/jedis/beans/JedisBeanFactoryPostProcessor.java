package io.basc.framework.jedis.beans;

import io.basc.framework.beans.BeanFactoryPostProcessor;
import io.basc.framework.beans.BeansException;
import io.basc.framework.beans.ConfigurableBeanFactory;
import io.basc.framework.beans.BeanlifeCycleEvent.Step;
import io.basc.framework.context.annotation.Provider;
import io.basc.framework.core.Ordered;
import io.basc.framework.redis.RedisConnectionFactory;
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

			if (beanFactory.isAlias(RedisConnectionFactory.class.getName())) {
				beanFactory.registerAlias(jedisConnectionFactoryDefinition.getId(),
						RedisConnectionFactory.class.getName());
			}
		}

		beanFactory.getLifecycleDispatcher().registerListener((e) -> {
			if (e.getStep() == Step.AFTER_DESTROY) {
				if (e.getSource() != null && e.getSource() instanceof JedisPool) {
					JedisPool jedisPool = (JedisPool) e.getSource();
					if (!jedisPool.isClosed()) {
						jedisPool.close();
					}
				}
			}
		});
	}
}
