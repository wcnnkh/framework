package io.basc.framework.redis.core.beans;

import io.basc.framework.beans.ConfigurableBeanFactory;
import io.basc.framework.beans.support.DefaultBeanDefinition;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.instance.InstanceException;
import io.basc.framework.orm.convert.EntityConversionService;
import io.basc.framework.orm.convert.PropertyFactoryToEntityConversionService;
import io.basc.framework.redis.core.RedisConfiguration;
import io.basc.framework.value.PropertyFactory;
import io.basc.framework.value.support.PropertiesPropertyFactory;

import java.util.Properties;

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
		io.basc.framework.event.Observable<Properties> observable = beanFactory.getEnvironment().getProperties(DEFAULT_CONFIGURATION);
		EntityConversionService entityConfigure = new PropertyFactoryToEntityConversionService();
		entityConfigure.setConversionService(beanFactory.getEnvironment().getConversionService());
		PropertyFactory properties = new PropertiesPropertyFactory(observable.get());
		entityConfigure.configurationProperties(properties, TypeDescriptor.forObject(properties), redisConfiguration,
				TypeDescriptor.forObject(redisConfiguration));
		entityConfigure.setPrefix("redis");
		entityConfigure.setStrict(true);
		entityConfigure.configurationProperties(properties, TypeDescriptor.forObject(properties), redisConfiguration,
				TypeDescriptor.forObject(redisConfiguration));
		return redisConfiguration;
	}
}