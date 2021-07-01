package scw.redis.core.beans;

import java.util.Properties;

import scw.beans.ConfigurableBeanFactory;
import scw.beans.support.DefaultBeanDefinition;
import scw.convert.TypeDescriptor;
import scw.instance.InstanceException;
import scw.orm.convert.EntityConversionService;
import scw.orm.convert.PropertyFactoryToEntityConversionService;
import scw.redis.core.RedisConfiguration;
import scw.value.PropertyFactory;
import scw.value.support.PropertiesPropertyFactory;

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
		scw.event.Observable<Properties> observable = beanFactory.getEnvironment().getProperties(DEFAULT_CONFIGURATION);
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