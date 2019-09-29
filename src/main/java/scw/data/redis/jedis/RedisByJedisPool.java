package scw.data.redis.jedis;

import java.util.Arrays;
import java.util.Properties;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import scw.beans.auto.annotation.ResourceParameter;
import scw.core.Constants;
import scw.core.annotation.NotRequire;
import scw.core.annotation.ParameterName;
import scw.core.utils.PropertiesUtils;
import scw.core.utils.StringUtils;
import scw.data.redis.AbstractRedisWrapper;
import scw.data.redis.Redis;
import scw.data.redis.RedisImpl;
import scw.data.redis.RedisOperations;
import scw.data.redis.ResourceManager;
import scw.io.SerializerUtils;
import scw.io.serializer.Serializer;

public final class RedisByJedisPool extends AbstractRedisWrapper implements ResourceManager<Jedis>, scw.core.Destroy {
	private final JedisPool jedisPool;
	private final String auth;
	private final Redis redis;

	public RedisByJedisPool(String propertiesFile) {
		this(propertiesFile, SerializerUtils.DEFAULT_SERIALIZER);
	}

	public RedisByJedisPool(
			@ParameterName("redis.configuration") @ResourceParameter("classpath:/redis.properties") String propertiesFile,
			Serializer serializer) {
		JedisPoolConfig config = createConfig(propertiesFile);
		Properties properties = PropertiesUtils.getProperties(propertiesFile, Constants.DEFAULT_CHARSET_NAME);
		String host = PropertiesUtils.getProperty(properties, "host", "address");
		String port = PropertiesUtils.getProperty(properties, "port");
		this.auth = PropertiesUtils.getProperty(properties, "auth", "password", "pwd");
		String keyPrefix = PropertiesUtils.getProperty(properties, "prefix", "keyPrefix");
		String charsetName = PropertiesUtils.getProperty(properties, "charsetName", "charset");
		if (StringUtils.isEmpty(charsetName)) {
			charsetName = Constants.DEFAULT_CHARSET_NAME;
		}

		if (StringUtils.isEmpty(port)) {
			this.jedisPool = new JedisPool(config, host);
		} else {
			this.jedisPool = new JedisPool(config, host, Integer.parseInt(port));
		}
		this.redis = createRedis(this, keyPrefix, charsetName, serializer);
	}

	private static Redis createRedis(ResourceManager<Jedis> resourceManager, String keyPrefix, String charsetName,
			Serializer serializer) {
		RedisOperations<String, String> stringOperations = new JedisStringOperations(resourceManager);
		RedisOperations<byte[], byte[]> binaryOperations = new JedisBinaryOperations(resourceManager);
		return new RedisImpl(binaryOperations, stringOperations, keyPrefix, charsetName, serializer);
	}

	private static JedisPoolConfig createConfig(String propertiesFile) {
		JedisPoolConfig config = new JedisPoolConfig();
		PropertiesUtils.loadProperties(config, propertiesFile, Arrays.asList("maxWait,maxWaitMillis"));
		return config;
	}

	public RedisByJedisPool(int maxTotal, int maxIdle, boolean testOnBorrow, String host) {
		this(maxTotal, maxIdle, testOnBorrow, host, null, Constants.DEFAULT_CHARSET_NAME,
				SerializerUtils.DEFAULT_SERIALIZER);
	}

	public RedisByJedisPool(int maxTotal, int maxIdle, boolean testOnBorrow, String host, @NotRequire String keyPrefix,
			String charsetName, Serializer serializer) {
		JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
		jedisPoolConfig.setMaxTotal(maxTotal);
		jedisPoolConfig.setMaxIdle(maxIdle);
		jedisPoolConfig.setTestOnBorrow(testOnBorrow);
		this.jedisPool = new JedisPool(jedisPoolConfig, host);
		this.auth = null;
		this.redis = createRedis(this, keyPrefix, charsetName, serializer);
	}

	public void destroy() {
		jedisPool.close();
	}

	public Jedis getResource() {
		Jedis jedis = jedisPool.getResource();
		if (jedis != null && auth != null) {
			jedis.auth(auth);
		}
		return jedis;
	}

	public void close(Jedis resource) {
		if (resource != null) {
			resource.close();
		}
	}

	@Override
	public Redis getTargetRedis() {
		return redis;
	}
}
