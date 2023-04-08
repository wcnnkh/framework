package io.basc.framework.redis.beans;

import java.util.Properties;

import io.basc.framework.context.Context;
import io.basc.framework.context.support.ContextBeanDefinition;
import io.basc.framework.context.support.ContextConfigurator;
import io.basc.framework.event.Observable;
import io.basc.framework.factory.BeanResolver;
import io.basc.framework.factory.InstanceException;
import io.basc.framework.redis.RedisConfiguration;
import io.basc.framework.value.PropertyFactory;
import io.basc.framework.value.support.PropertiesPropertyFactory;

public class RedisConfigBeanDefinition extends ContextBeanDefinition {
	public static final String DEFAULT_CONFIGURATION = "/redis/redis.properties";

	public RedisConfigBeanDefinition(Context context) {
		super(context, RedisConfiguration.class);
	}

	@Override
	public boolean isInstance() {
		return getEnvironment().getResourceLoader().exists(DEFAULT_CONFIGURATION);
	}
	
	@Override
	public boolean isAopEnable(BeanResolver beanResolver) {
		return false;
	}

	@Override
	public Object create() throws InstanceException {
		RedisConfiguration redisConfiguration = new RedisConfiguration();
		Observable<Properties> observable = getEnvironment()
				.getProperties(DEFAULT_CONFIGURATION);
		ContextConfigurator mapper = new ContextConfigurator(getContext());
		mapper.setConversionService(getEnvironment().getConversionService());
		mapper.configure(getBeanFactory());
		PropertyFactory properties = new PropertiesPropertyFactory(observable.get());
		mapper.transform(properties, redisConfiguration);
		mapper.getContext().setNamePrefix("redis");
		mapper.transform(properties, redisConfiguration);
		return redisConfiguration;
	}
}