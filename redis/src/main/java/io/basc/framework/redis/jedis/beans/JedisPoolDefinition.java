package io.basc.framework.redis.jedis.beans;

import io.basc.framework.beans.ConfigurableBeanFactory;
import io.basc.framework.beans.support.DefaultBeanDefinition;
import io.basc.framework.factory.InstanceException;
import io.basc.framework.redis.core.RedisConfiguration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class JedisPoolDefinition extends DefaultBeanDefinition {

	public JedisPoolDefinition(ConfigurableBeanFactory beanFactory) {
		super(beanFactory, JedisPool.class);
	}

	@Override
	public boolean isInstance() {
		return beanFactory.isInstance(RedisConfiguration.class) && beanFactory.isInstance(JedisPoolConfig.class);
	}

	/**
	 * 不用处理销毁
	 * 
	 * @see JedisBeanFactoryPostProcessor#postProcessBeanFactory(ConfigurableBeanFactory)注册了销毁
	 */
	@Override
	public Object create() throws InstanceException {
		JedisPoolConfig jedisPoolConfig = beanFactory.getInstance(JedisPoolConfig.class);
		RedisConfiguration redisConfiguration = beanFactory.getInstance(RedisConfiguration.class);
		JedisPool jedisPool = new JedisPool(jedisPoolConfig, redisConfiguration.getHost(), redisConfiguration.getPort(),
				redisConfiguration.getTimeout(), redisConfiguration.getTimeout(), redisConfiguration.getUsername(),
				redisConfiguration.getPassword(), redisConfiguration.getDatabase(), redisConfiguration.getClientName());
		return jedisPool;
	}
}
