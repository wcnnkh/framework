package io.basc.framework.jedis.beans;

import io.basc.framework.beans.factory.BeanFactory;
import io.basc.framework.beans.factory.InstanceException;
import io.basc.framework.beans.factory.support.FactoryBeanDefinition;
import io.basc.framework.redis.RedisConfiguration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * JedisPool的定义
 * 
 * @author wcnnkh
 * @see JedisContextPostProcessor#postProcessContext(ConfigurableContext)
 */
public class JedisPoolDefinition extends FactoryBeanDefinition {

	public JedisPoolDefinition(BeanFactory beanFactory) {
		super(beanFactory, JedisPool.class);
	}

	@Override
	public boolean isInstance() {
		return getBeanFactory().isInstance(RedisConfiguration.class)
				&& getBeanFactory().isInstance(JedisPoolConfig.class);
	}

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
