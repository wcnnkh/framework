package scw.redis.jedis.beans;

import redis.clients.jedis.JedisPool;
import scw.beans.ConfigurableBeanFactory;
import scw.beans.support.DefaultBeanDefinition;
import scw.instance.InstanceException;
import scw.redis.jedis.JedisConnectionFactory;

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
