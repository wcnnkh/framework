package io.basc.framework.jedis.beans;

import io.basc.framework.beans.BeanFactory;
import io.basc.framework.factory.InstanceException;
import io.basc.framework.factory.support.FactoryBeanDefinition;
import io.basc.framework.jedis.JedisClient;
import redis.clients.jedis.JedisPool;

public class JedisConnectionFactoryDefinition extends FactoryBeanDefinition {

	public JedisConnectionFactoryDefinition(BeanFactory beanFactory) {
		super(beanFactory, JedisClient.class);
	}

	@Override
	public boolean isInstance() {
		return getBeanFactory().isInstance(JedisPool.class);
	}

	@Override
	public Object create() throws InstanceException {
		JedisPool jedisPool = getBeanFactory().getInstance(JedisPool.class);
		return new JedisClient(jedisPool);
	}
}
