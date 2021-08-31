package io.basc.framework.redis.jedis.beans;

import io.basc.framework.beans.ConfigurableBeanFactory;
import io.basc.framework.beans.support.DefaultBeanDefinition;
import io.basc.framework.factory.InstanceException;
import io.basc.framework.redis.jedis.JedisConnectionFactory;
import redis.clients.jedis.JedisPool;

public class JedisConnectionFactoryDefinition extends DefaultBeanDefinition {

	public JedisConnectionFactoryDefinition(ConfigurableBeanFactory beanFactory) {
		super(beanFactory, JedisConnectionFactory.class);
	}

	@Override
	public boolean isInstance() {
		return beanFactory.isInstance(JedisPool.class);
	}

	@Override
	public Object create() throws InstanceException {
		JedisPool jedisPool = beanFactory.getInstance(JedisPool.class);
		return new JedisConnectionFactory(jedisPool);
	}
}