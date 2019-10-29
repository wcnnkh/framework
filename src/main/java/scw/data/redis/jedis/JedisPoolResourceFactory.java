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
import scw.core.annotation.NotRequire;
import scw.core.annotation.Order;
import scw.core.annotation.ParameterName;
import scw.core.instance.annotation.ResourceParameter;
import scw.core.resource.ResourceUtils;
import scw.core.utils.ConfigUtils;
import scw.core.utils.StringUtils;
import scw.data.redis.RedisConstants;

/**
 * 实现自动化配置
 * 
 * @author shuchaowen
 *
 */
public final class JedisPoolResourceFactory implements JedisResourceFactory, Destroy, RedisConstants {
	private JedisPool jedisPool;
	private String auth;

	public JedisPoolResourceFactory(PropertyFactory propertyFactory, @ParameterName(HOST_CONFIG_KEY) String host,
			@ParameterName(PORT_CONFIG_KEY) @NotRequire int port, @NotRequire String auth) {
		init(propertyFactory, host, port, auth);
	}

	private void init(PropertyFactory propertyFactory, String host, int port, String auth) {
		JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
		if (propertyFactory != null) {
			ConfigUtils.invokeSetterByProeprties(jedisPoolConfig, "redis.", propertyFactory);
		}

		this.auth = auth;
		if (port <= 0) {
			this.jedisPool = new JedisPool(jedisPoolConfig, host);
		} else {
			this.jedisPool = new JedisPool(jedisPoolConfig, host, port);
		}
	}

	@Order
	public JedisPoolResourceFactory(
			@ResourceParameter("redis.configuration") @DefaultValue("redis.properties") String configuration) {
		Properties properties = ResourceUtils.getProperties(configuration, Constants.DEFAULT_CHARSET_NAME);
		PropertyFactory propertyFactory = new MapPropertyFactory(properties);
		String host = propertyFactory.getProperty(HOST_CONFIG_KEY);
		if (StringUtils.isEmpty(host)) {
			host = "127.0.0.1";
		}

		init(propertyFactory, host, StringUtils.parseInt(propertyFactory.getProperty(PORT_CONFIG_KEY)),
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
