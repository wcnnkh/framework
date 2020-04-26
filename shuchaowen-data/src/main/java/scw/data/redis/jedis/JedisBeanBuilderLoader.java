package scw.data.redis.jedis;

import java.util.Properties;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import scw.beans.builder.AbstractBeanBuilder;
import scw.beans.builder.BeanBuilder;
import scw.beans.builder.BeanBuilderLoader;
import scw.beans.builder.BeanBuilderLoaderChain;
import scw.beans.builder.LoaderContext;
import scw.core.Constants;
import scw.core.instance.annotation.Configuration;
import scw.core.utils.StringUtils;
import scw.data.redis.RedisConstants;
import scw.io.ResourceUtils;
import scw.util.ConfigUtils;
import scw.util.value.property.PropertiesPropertyFactory;
import scw.util.value.property.PropertyFactory;

@Configuration(order = Integer.MIN_VALUE, value = BeanBuilderLoader.class)
public class JedisBeanBuilderLoader implements BeanBuilderLoader, RedisConstants {

	public BeanBuilder loading(LoaderContext context, BeanBuilderLoaderChain loaderChain) {
		if (context.getTargetClass() == JedisPool.class) {
			return new JedisPoolBeanBuilder(context);
		}
		else if (context.getTargetClass() == JedisPoolConfig.class) {
			return new JedisPoolConfigBeanBuilder(context);
		} 
		return loaderChain.loading(context);
	}

	private static String getConfigName(PropertyFactory propertyFactory) {
		return propertyFactory.getValue(CONFIG_KEY, String.class, DEFAULT_CONFIG);
	}

	private static final class JedisPoolBeanBuilder extends AbstractBeanBuilder {

		public JedisPoolBeanBuilder(LoaderContext context) {
			super(context);
		}

		public boolean isInstance() {
			return getHost() != null;
		}

		private String getHost() {
			String host = propertyFactory.getString(HOST_CONFIG_KEY);
			if (host == null) {
				Properties properties = ResourceUtils.getResourceOperations()
						.getFormattedProperties(getConfigName(propertyFactory), Constants.DEFAULT_CHARSET_NAME);
				host = properties.getProperty(HOST_CONFIG_KEY);
			}
			return host;
		}

		public Object create() throws Exception {
			String host = getHost();
			if (beanFactory.isInstance(JedisPoolConfig.class)) {
				return new JedisPool(beanFactory.getInstance(JedisPoolConfig.class), host);
			} else {
				return new JedisPool(host);
			}
		}

		@Override
		public void destroy(Object instance) throws Exception {
			super.destroy(instance);
			if (instance instanceof JedisPool) {
				if (!((JedisPool) instance).isClosed()) {
					((JedisPool) instance).close();
				}
			}
		}
	}

	private static final class JedisPoolConfigBeanBuilder extends AbstractBeanBuilder {
		public JedisPoolConfigBeanBuilder(LoaderContext context) {
			super(context);
		}

		public boolean isInstance() {
			return ResourceUtils.getResourceOperations().isExist(getConfigName(propertyFactory));
		}

		public Object create() throws Exception {
			Properties properties = ResourceUtils.getResourceOperations()
					.getFormattedProperties(getConfigName(propertyFactory), Constants.DEFAULT_CHARSET_NAME);
			PropertyFactory propertyFactory = new PropertiesPropertyFactory(properties);
			String host = propertyFactory.getString(HOST_CONFIG_KEY);
			if (StringUtils.isEmpty(host)) {
				host = "127.0.0.1";
			}

			JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
			// 兼容老版本
			ConfigUtils.invokeSetterByProeprties(jedisPoolConfig, null, propertyFactory);
			ConfigUtils.invokeSetterByProeprties(jedisPoolConfig, "redis.", propertyFactory);
			return jedisPoolConfig;
		}
	}
}
