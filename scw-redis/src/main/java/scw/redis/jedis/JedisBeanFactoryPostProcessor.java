package scw.redis.jedis;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import scw.beans.BeanDefinition;
import scw.beans.BeanFactoryPostProcessor;
import scw.beans.BeanUtils;
import scw.beans.BeansException;
import scw.beans.ConfigurableBeanFactory;
import scw.beans.support.DefaultBeanDefinition;
import scw.context.annotation.Provider;
import scw.core.Ordered;
import scw.instance.InstanceException;
import scw.logger.Levels;
import scw.redis.core.Redis;
import scw.redis.core.RedisConfiguration;

@Provider(order = Ordered.LOWEST_PRECEDENCE)
public class JedisBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

	public void postProcessBeanFactory(ConfigurableBeanFactory beanFactory) throws BeansException {
		JedisPoolConfigBeanDefinition poolConfigBeanDefinition = new JedisPoolConfigBeanDefinition(beanFactory);
		if (!beanFactory.containsDefinition(poolConfigBeanDefinition.getId())) {
			beanFactory.registerDefinition(poolConfigBeanDefinition);
		}

		BeanDefinition poolDefinition = new JedisPoolBeanDefinition(beanFactory);
		if (!beanFactory.containsDefinition(poolDefinition.getId())) {
			beanFactory.registerDefinition(poolDefinition);
		}

		RedisBeanDefinition redisBeanDefinition = new RedisBeanDefinition(beanFactory);
		if(!beanFactory.containsDefinition(redisBeanDefinition.getId())) {
			beanFactory.registerDefinition(redisBeanDefinition);
		}
	}

	private static final class JedisPoolConfigBeanDefinition extends DefaultBeanDefinition {

		public JedisPoolConfigBeanDefinition(ConfigurableBeanFactory beanFactory) {
			super(beanFactory, JedisPoolConfig.class);
		}

		@Override
		public boolean isInstance() {
			return true;
		}

		@Override
		public Object create() throws InstanceException {
			JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
			BeanUtils.configurationProperties(jedisPoolConfig, getEnvironment(), "jedis.pool", Levels.INFO);
			return jedisPoolConfig;
		}
	}

	private static final class JedisPoolBeanDefinition extends DefaultBeanDefinition {

		public JedisPoolBeanDefinition(ConfigurableBeanFactory beanFactory) {
			super(beanFactory, JedisPool.class);
		}

		public boolean isInstance() {
			return beanFactory.isInstance(JedisPoolConfig.class) && beanFactory.isInstance(RedisConfiguration.class);
		}

		public Object create() throws BeansException {
			JedisPoolConfig jedisPoolConfig = beanFactory.getInstance(JedisPoolConfig.class);
			RedisConfiguration redisConfiguration = beanFactory.getInstance(RedisConfiguration.class);
			return new JedisPool(jedisPoolConfig, redisConfiguration.getHost(), redisConfiguration.getPort(),
					redisConfiguration.getTimeout(), redisConfiguration.getTimeout(), redisConfiguration.getUsername(),
					redisConfiguration.getPassword(), redisConfiguration.getDatabase(),
					redisConfiguration.getClientName());
		}

		@Override
		public void destroy(Object instance) throws BeansException {
			super.destroy(instance);
			if (instance instanceof JedisPool) {
				if (!((JedisPool) instance).isClosed()) {
					((JedisPool) instance).close();
				}
			}
		}
	}

	private static class RedisBeanDefinition extends DefaultBeanDefinition {

		public RedisBeanDefinition(ConfigurableBeanFactory beanFactory) {
			super(beanFactory, Redis.class);
		}

		@Override
		public boolean isInstance(Class<?>[] parameterTypes) {
			return beanFactory.isInstance(JedisConnectionFactory.class);
		}

		@Override
		public Object create() throws InstanceException {
			JedisConnectionFactory connectionFactory = beanFactory.getInstance(JedisConnectionFactory.class);
			return new Redis(connectionFactory);
		}
	}
}
