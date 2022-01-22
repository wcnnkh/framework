package io.basc.framework.jedis.beans;

import io.basc.framework.beans.ConfigurableBeanFactory;
import io.basc.framework.beans.support.DefaultBeanDefinition;
import io.basc.framework.factory.InstanceException;
import io.basc.framework.jedis.JedisClient;
import redis.clients.jedis.JedisPool;

public class JedisConnectionFactoryDefinition extends DefaultBeanDefinition {

	public JedisConnectionFactoryDefinition(ConfigurableBeanFactory beanFactory) {
		super(beanFactory, JedisClient.class);
	}

	@Override
	public boolean isInstance() {
		return beanFactory.isInstance(JedisPool.class);
	}

	@Override
	public Object create() throws InstanceException {
		JedisPool jedisPool = beanFactory.getInstance(JedisPool.class);
		return new JedisClient(jedisPool);
	}
}
