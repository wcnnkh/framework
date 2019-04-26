package scw.data.redis;

import java.io.Closeable;
import java.util.Arrays;
import java.util.Properties;

import redis.clients.jedis.BinaryJedisCommands;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCommands;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.MultiKeyBinaryCommands;
import redis.clients.jedis.MultiKeyCommands;
import scw.beans.annotation.Bean;
import scw.beans.annotation.Destroy;
import scw.core.Constants;
import scw.core.utils.ConfigUtils;
import scw.core.utils.PropertiesUtils;
import scw.core.utils.StringUtils;
import scw.data.redis.jedis.AbstractBinaryCommands;
import scw.data.redis.jedis.AbstractBinaryScriptingCommands;
import scw.data.redis.jedis.AbstractCommands;
import scw.data.redis.jedis.AbstractMultiKeyBinaryCommands;
import scw.data.redis.jedis.AbstractMultikeyCommands;
import scw.data.redis.jedis.AbstractScriptingCommands;

@Bean(proxy = false)
public class RedisByJedisPool implements Redis, Closeable {

	private final JedisPool jedisPool;
	private final String auth;

	public RedisByJedisPool(String propertiesFile) {
		JedisPoolConfig config = createConfig(propertiesFile);

		Properties properties = ConfigUtils.getProperties(propertiesFile, Constants.DEFAULT_CHARSET.name());
		String host = PropertiesUtils.getProperty(properties, "host", "address");
		String port = PropertiesUtils.getProperty(properties, "port");
		this.auth = PropertiesUtils.getProperty(properties, "auth", "password", "pwd");
		if (StringUtils.isEmpty(port)) {
			this.jedisPool = new JedisPool(config, host);
		} else {
			this.jedisPool = new JedisPool(config, host, Integer.parseInt(port));
		}
	}

	public static JedisPoolConfig createConfig(String propertiesFile) {
		JedisPoolConfig config = new JedisPoolConfig();
		PropertiesUtils.loadProperties(config, propertiesFile, Arrays.asList("maxWait,maxWaitMillis"));
		return config;
	}

	public RedisByJedisPool() {
		JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
		jedisPoolConfig.setMaxTotal(512);
		jedisPoolConfig.setMaxIdle(200);
		jedisPoolConfig.setTestOnBorrow(true);
		this.jedisPool = new JedisPool(jedisPoolConfig, "localhost");
		this.auth = null;
		initCommands();
	}

	public RedisByJedisPool(int maxTotal, int maxIdle, boolean testOnBorrow, String host) {
		JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
		jedisPoolConfig.setMaxTotal(maxTotal);
		jedisPoolConfig.setMaxIdle(maxIdle);
		jedisPoolConfig.setTestOnBorrow(testOnBorrow);
		this.jedisPool = new JedisPool(jedisPoolConfig, host);
		this.auth = null;
		initCommands();
	}

	private Commands commands;
	private BinaryCommands binaryCommands;
	private BinaryScriptingCommands binaryScriptingCommands;
	private ScriptingCommands scriptingCommands;
	private scw.data.redis.MultiKeyBinaryCommands multiKeyBinaryCommands;
	private scw.data.redis.MultiKeyCommands multiKeyCommands;

	private Jedis getJedis() {
		Jedis jedis = jedisPool.getResource();
		if (jedis != null && auth != null) {
			jedis.auth(auth);
		}
		return jedis;
	}

	private void closeResource(Object resource) {
		((Jedis) resource).close();
	}

	private void initCommands() {
		this.commands = new AbstractCommands() {

			public JedisCommands getResource() {
				return getJedis();
			}

			public void close(JedisCommands resource) {
				closeResource(resource);
			}
		};
		this.binaryCommands = new AbstractBinaryCommands() {

			public BinaryJedisCommands getResource() {
				return getJedis();
			}

			public void close(BinaryJedisCommands resource) {
				closeResource(resource);
			}
		};
		this.binaryScriptingCommands = new AbstractBinaryScriptingCommands() {

			public redis.clients.jedis.BinaryScriptingCommands getResource() {
				return getJedis();
			}

			public void close(redis.clients.jedis.BinaryScriptingCommands resource) {
				closeResource(resource);
			}
		};
		this.scriptingCommands = new AbstractScriptingCommands() {

			public redis.clients.jedis.ScriptingCommands getResource() {
				return getJedis();
			}

			public void close(redis.clients.jedis.ScriptingCommands resource) {
				closeResource(resource);
			}
		};
		this.multiKeyBinaryCommands = new AbstractMultiKeyBinaryCommands() {

			public MultiKeyBinaryCommands getResource() {
				return getJedis();
			}

			public void close(MultiKeyBinaryCommands resource) {
				closeResource(resource);
			}
		};
		this.multiKeyCommands = new AbstractMultikeyCommands() {

			public MultiKeyCommands getResource() {
				return getJedis();
			}

			public void close(MultiKeyCommands resource) {
				closeResource(resource);
			}
		};
	}

	public JedisPool getJedisPool() {
		return jedisPool;
	}

	public Commands getCommands() {
		return commands;
	}

	public BinaryCommands getBinaryCommands() {
		return binaryCommands;
	}

	public BinaryScriptingCommands getBinaryScriptingCommands() {
		return binaryScriptingCommands;
	}

	public ScriptingCommands getScriptingCommands() {
		return scriptingCommands;
	}

	public scw.data.redis.MultiKeyBinaryCommands getMultiKeyBinaryCommands() {
		return multiKeyBinaryCommands;
	}

	public scw.data.redis.MultiKeyCommands getMultiKeyCommands() {
		return multiKeyCommands;
	}

	@Destroy
	public void close() {
		jedisPool.close();
	}
}
