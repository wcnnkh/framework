package scw.data.redis.jedis;

import java.util.Properties;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import scw.core.Constants;
import scw.core.Destroy;
import scw.core.MapPropertyFactory;
import scw.core.PropertyFactory;
import scw.core.annotation.DefaultValue;
import scw.core.annotation.Order;
import scw.core.annotation.ParameterName;
import scw.core.instance.annotation.ResourceParameter;
import scw.core.utils.StringUtils;
import scw.data.redis.RedisConstants;
import scw.data.redis.RedisUtils;
import scw.lang.Nullable;
import scw.resource.ResourceUtils;
import scw.util.ConfigUtils;

/**
 * 实现自动化配置
 * 
 * @author shuchaowen
 *
 */
public final class JedisPoolResourceFactory implements JedisResourceFactory,
		Destroy, RedisConstants {
	private JedisPool jedisPool;
	private String auth;

	public JedisPoolResourceFactory(PropertyFactory propertyFactory,
			@ParameterName(HOST_CONFIG_KEY) String host,
			@ParameterName(PORT_CONFIG_KEY) @Nullable int port,
			@Nullable String auth) {
		JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
		init(jedisPoolConfig, propertyFactory, host, port, auth);
	}

	private void init(JedisPoolConfig jedisPoolConfig,
			PropertyFactory propertyFactory, String host, int port, String auth) {
		if (propertyFactory != null) {
			ConfigUtils.invokeSetterByProeprties(jedisPoolConfig, "redis.",
					propertyFactory);
		}

		this.auth = auth;
		if (port <= 0) {
			this.jedisPool = new JedisPool(jedisPoolConfig, host);
		} else {
			this.jedisPool = new JedisPool(jedisPoolConfig, host, port);
		}

		if (RedisUtils.startingFlushAll()) {
			JedisUtils.flushAll(this);
		}
	}

	@Order
	public JedisPoolResourceFactory(
			@ParameterName(CONFIG_KEY) @ResourceParameter @DefaultValue(DEFAULT_CONFIG) String configuration) {
		Properties properties = ResourceUtils.getResourceOperations()
				.getProperties(configuration, Constants.DEFAULT_CHARSET_NAME);
		PropertyFactory propertyFactory = new MapPropertyFactory(properties,
				true);
		String host = propertyFactory.getProperty(HOST_CONFIG_KEY);
		if (StringUtils.isEmpty(host)) {
			host = "127.0.0.1";
		}

		JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
		// 兼容老版本
		ConfigUtils.invokeSetterByProeprties(jedisPoolConfig, null,
				new MapPropertyFactory(properties, false));
		init(jedisPoolConfig, propertyFactory, host,
				StringUtils.parseInt(propertyFactory
						.getProperty(PORT_CONFIG_KEY)),
				propertyFactory.getProperty(AUTH_CONFIG_KEY));
	}

	public void destroy() {
		jedisPool.destroy();
	}

	public Jedis getResource() {
		Jedis jedis = jedisPool.getResource();
		if (jedis != null && auth != null) {
			jedis.auth(auth);
		}
		return jedis;
	}

	public void release(Jedis resource) {
		resource.close();
	}
}
