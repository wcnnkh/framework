package scw.redis.jedis.beans;

import redis.clients.jedis.JedisPool;
import scw.beans.BeanFactoryPostProcessor;
import scw.beans.BeansException;
import scw.beans.ConfigurableBeanFactory;
import scw.beans.BeanlifeCycleEvent.Step;
import scw.context.annotation.Provider;
import scw.core.Ordered;
import scw.redis.core.RedisConnectionFactory;

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
