package io.basc.framework.jedis.beans;

import io.basc.framework.factory.BeanFactory;
import io.basc.framework.factory.InstanceException;
import io.basc.framework.factory.support.FactoryBeanDefinition;
import io.basc.framework.redis.RedisConfiguration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class JedisPoolDefinition extends FactoryBeanDefinition {

	public JedisPoolDefinition(BeanFactory beanFactory) {
		super(beanFactory, JedisPool.class);
	}

	@Override
	public boolean isInstance() {
		return getBeanFactory().isInstance(RedisConfiguration.class)
				&& getBeanFactory().isInstance(JedisPoolConfig.class);
	}

	/**
	 * 不用处理销毁
	 * 
	 * @see JedisBeanFactoryPostProcessor#postProcessBeanFactory(ConfigurableBeanFactory)注册了销毁
	 */
	@Override
	public Object create() throws InstanceException {
		JedisPoolConfig jedisPoolConfig = getBeanFactory().getInstance(JedisPoolConfig.class);
		RedisConfiguration redisConfiguration = getBeanFactory().getInstance(RedisConfiguration.class);
		JedisPool jedisPool = new JedisPool(jedisPoolConfig, redisConfiguration.getHost(), redisConfiguration.getPort(),
				redisConfiguration.getTimeout(), redisConfiguration.getTimeout(), redisConfiguration.getUsername(),
				redisConfiguration.getPassword(), redisConfiguration.getDatabase(), redisConfiguration.getClientName());
		return jedisPool;
	}
}
