package io.basc.framework.redis.beans;

import java.util.Properties;

import io.basc.framework.beans.ConfigurableBeanFactory;
import io.basc.framework.beans.support.DefaultBeanDefinition;
import io.basc.framework.factory.InstanceException;
import io.basc.framework.orm.support.DefaultObjectRelationalMapper;
import io.basc.framework.redis.RedisConfiguration;
import io.basc.framework.value.PropertyFactory;
import io.basc.framework.value.support.PropertiesPropertyFactory;

public class RedisConfigBeanDefinition extends DefaultBeanDefinition {
	public static final String DEFAULT_CONFIGURATION = "/redis/redis.properties";

	public RedisConfigBeanDefinition(ConfigurableBeanFactory beanFactory) {
		super(beanFactory, RedisConfiguration.class);
	}

	@Override
	public boolean isInstance() {
		return getEnvironment().exists(DEFAULT_CONFIGURATION);
	}

	@Override
	public boolean isAopEnable() {
		return false;
	}

	@Override
	public Object create() throws InstanceException {
		RedisConfiguration redisConfiguration = new RedisConfiguration();
		io.basc.framework.event.Observable<Properties> observable = beanFactory.getEnvironment()
				.getProperties(DEFAULT_CONFIGURATION);
		DefaultObjectRelationalMapper mapper = new DefaultObjectRelationalMapper();
		mapper.setConversionService(beanFactory.getEnvironment().getConversionService());
		PropertyFactory properties = new PropertiesPropertyFactory(observable.get());
		mapper.transform(properties, redisConfiguration);
		mapper.setNamePrefix("redis");
		mapper.transform(properties, redisConfiguration);
		return redisConfiguration;
	}
}