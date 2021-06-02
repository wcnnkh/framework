package scw.redis.core.beans;

import java.util.Properties;

import scw.beans.BeanFactoryPostProcessor;
import scw.beans.BeansException;
import scw.beans.ConfigurableBeanFactory;
import scw.beans.support.DefaultBeanDefinition;
import scw.context.annotation.Provider;
import scw.convert.TypeDescriptor;
import scw.core.Ordered;
import scw.instance.InstanceException;
import scw.orm.convert.EntityConversionService;
import scw.orm.convert.PropertyFactoryToEntityConversionService;
import scw.redis.core.RedisConfiguration;

@Provider(order = Ordered.HIGHEST_PRECEDENCE)
public class RedisBeanFactoryPostProcessor implements BeanFactoryPostProcessor {
	private static final String HST_KEY = "redis.host";
	private static final String PORT_KEY = "redis.port";
	private static final String CONFIG_KEY = "redis.configuration";
	public static final String DEFAULT_CONFIG = "/redis/redis.properties";

	@Override
	public void postProcessBeanFactory(ConfigurableBeanFactory beanFactory) throws BeansException {
		if (!beanFactory.containsDefinition(RedisConfiguration.class.getName())) {
			beanFactory.registerDefinition(new RedisConfigBeanDefinition(beanFactory));
		}
	}

	private static class RedisConfigBeanDefinition extends DefaultBeanDefinition {

		public RedisConfigBeanDefinition(ConfigurableBeanFactory beanFactory) {
			super(beanFactory, RedisConfiguration.class);
		}

		public String getConfigName() {
			return beanFactory.getEnvironment().getValue(CONFIG_KEY, String.class, DEFAULT_CONFIG);
		}

		@Override
		public boolean isInstance() {
			return true;
		}

		@Override
		public boolean isAopEnable() {
			return false;
		}

		@Override
		public Object create() throws InstanceException {
			RedisConfiguration redisConfiguration = new RedisConfiguration();
			redisConfiguration.setHost(beanFactory.getEnvironment().getString(HST_KEY));
			redisConfiguration.setPort(beanFactory.getEnvironment().getInteger(PORT_KEY));

			scw.event.Observable<Properties> observable = beanFactory.getEnvironment().getProperties(getConfigName());
			EntityConversionService entityConfigure = new PropertyFactoryToEntityConversionService();
			entityConfigure.setConversionService(beanFactory.getEnvironment().getConversionService());
			Properties properties = observable.get();
			entityConfigure.configurationProperties(properties, TypeDescriptor.forObject(properties),
					redisConfiguration, TypeDescriptor.forObject(redisConfiguration));
			entityConfigure.setPrefix("redis");
			entityConfigure.setStrict(true);
			entityConfigure.configurationProperties(properties, TypeDescriptor.forObject(properties),
					redisConfiguration, TypeDescriptor.forObject(redisConfiguration));
			return entityConfigure;
		}
	}
}
