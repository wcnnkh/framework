package scw.redis.jedis;

import java.util.Properties;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import scw.beans.BeanDefinition;
import scw.beans.BeanFactory;
import scw.beans.BeanFactoryPostProcessor;
import scw.beans.BeansException;
import scw.beans.ConfigurableBeanFactory;
import scw.beans.support.DefaultBeanDefinition;
import scw.context.annotation.Provider;
import scw.convert.TypeDescriptor;
import scw.convert.support.EntityConversionService;
import scw.convert.support.PropertyFactoryToEntityConversionService;
import scw.core.utils.StringUtils;
import scw.io.event.ObservableProperties;

@Provider
public class JedisBeanFactoryPostProcessor implements BeanFactoryPostProcessor {
	private static final String HOST_CONFIG_KEY = "redis.host";
	private static final String CONFIG_KEY = "redis.configuration";
	private static final String DEFAULT_CONFIG = "/redis/redis.properties";
	
	public void postProcessBeanFactory(ConfigurableBeanFactory beanFactory)
			throws BeansException {
		JedisPoolConfigBeanDefinition configBeanDefinition = new JedisPoolConfigBeanDefinition(beanFactory);
		if(!beanFactory.containsDefinition(configBeanDefinition.getId())){
			beanFactory.registerDefinition(configBeanDefinition);
		}
		
		BeanDefinition poolDefinition = new JedisPoolBeanDefinition(beanFactory);
		if(!beanFactory.containsDefinition(poolDefinition.getId())){
			beanFactory.registerDefinition(poolDefinition);
		}
	}
	
	private static final class JedisPoolBeanDefinition extends DefaultBeanDefinition {

		public JedisPoolBeanDefinition(BeanFactory beanFactory) {
			super(beanFactory, JedisPool.class);
		}
		
		public String getConfigName(){
			return beanFactory.getEnvironment().getValue(CONFIG_KEY, String.class, DEFAULT_CONFIG);
		}

		public boolean isInstance() {
			return getHost() != null;
		}

		private String getHost() {
			String host = beanFactory.getEnvironment().getString(HOST_CONFIG_KEY);
			String configName = getConfigName();
			if (host == null && beanFactory.getEnvironment().exists(configName)) {
				Properties properties = beanFactory.getEnvironment().getProperties(configName).get();
				host = properties.getProperty(HOST_CONFIG_KEY);
				if (host == null) {
					host = properties.getProperty("host");// 兼容老版本
				}
			}
			return host;
		}

		public Object create() throws BeansException {
			String host = getHost();
			if (beanFactory.isInstance(JedisPoolConfig.class)) {
				return new JedisPool(beanFactory.getInstance(JedisPoolConfig.class), host);
			} else {
				return new JedisPool(host);
			}
		}

		@Override
		public void destroy(Object instance) throws BeansException {
			super.destroy(instance);
			if (instance instanceof JedisPool) {
				if (!((JedisPool) instance).isClosed()) {
					((JedisPool) instance).close();
				}
			}
		}
	}

	private static final class JedisPoolConfigBeanDefinition extends DefaultBeanDefinition {

		public JedisPoolConfigBeanDefinition(BeanFactory beanFactory) {
			super(beanFactory, JedisPoolConfig.class);
		}
		public String getConfigName(){
			return beanFactory.getEnvironment().getValue(CONFIG_KEY, String.class, DEFAULT_CONFIG);
		}

		public boolean isInstance() {
			return beanFactory.getEnvironment().exists(getConfigName());
		}

		public Object create() throws BeansException {
			ObservableProperties properties = new ObservableProperties();
			scw.event.Observable<Properties> observable = beanFactory.getEnvironment().getProperties(getConfigName());
			properties.combine(observable);
			String host = properties.getString(HOST_CONFIG_KEY);
			if (StringUtils.isEmpty(host)) {
				host = "127.0.0.1";
			}

			JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
			// 兼容老版本
			EntityConversionService entityConfigure = new PropertyFactoryToEntityConversionService(beanFactory.getEnvironment().getConversionService());
			entityConfigure.configurationProperties(properties, TypeDescriptor.forObject(properties), jedisPoolConfig, TypeDescriptor.forObject(jedisPoolConfig));
			entityConfigure.setPrefix("redis");
			entityConfigure.setStrict(true);
			entityConfigure.configurationProperties(properties, TypeDescriptor.forObject(properties), jedisPoolConfig, TypeDescriptor.forObject(jedisPoolConfig));
			return jedisPoolConfig;
		}
	}
}
