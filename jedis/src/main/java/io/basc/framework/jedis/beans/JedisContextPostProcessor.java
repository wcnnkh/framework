package io.basc.framework.jedis.beans;

import io.basc.framework.beans.factory.BeanLifecycleEvent.Step;
import io.basc.framework.context.annotation.Provider;
import io.basc.framework.context.config.ConfigurableContext;
import io.basc.framework.context.config.ContextPostProcessor;
import io.basc.framework.core.Ordered;
import io.basc.framework.redis.RedisClient;
import redis.clients.jedis.JedisPool;

@Provider(order = Ordered.LOWEST_PRECEDENCE)
public class JedisContextPostProcessor implements ContextPostProcessor {

	@Override
	public void postProcessContext(ConfigurableContext context) throws Throwable {
		if (!context.containsDefinition(JedisPool.class.getName())) {
			context.registerDefinition(new JedisPoolDefinition(context));
		}

		JedisConnectionFactoryDefinition jedisConnectionFactoryDefinition = new JedisConnectionFactoryDefinition(
				context);
		if (!context.containsDefinition(jedisConnectionFactoryDefinition.getId())) {
			context.registerDefinition(jedisConnectionFactoryDefinition);

			if (context.isAlias(RedisClient.class.getName())) {
				context.registerAlias(jedisConnectionFactoryDefinition.getId(), RedisClient.class.getName());
			}
		}

		context.registerListener((e) -> {
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
