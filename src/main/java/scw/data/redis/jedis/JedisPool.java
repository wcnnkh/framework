package scw.data.redis.jedis;

import redis.clients.jedis.BinaryJedisCommands;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCommands;
import redis.clients.jedis.MultiKeyBinaryCommands;
import redis.clients.jedis.MultiKeyCommands;

public class JedisPool {
	private final scw.data.redis.Commands commands;
	private final scw.data.redis.BinaryCommands binaryCommands;
	private final scw.data.redis.BinaryScriptingCommands binaryScriptingCommands;
	private final scw.data.redis.ScriptingCommands scriptingCommands;
	private final scw.data.redis.MultiKeyBinaryCommands multiKeyBinaryCommands;
	private final scw.data.redis.MultiKeyCommands multiKeyCommands;

	protected Jedis getJedis(redis.clients.jedis.JedisPool jedisPool, String password) {
		Jedis jedis = jedisPool.getResource();
		if (jedis != null && password != null) {
			jedis.auth(password);
		}
		return jedis;
	}

	protected void closeResource(Object resource) {
		if (resource instanceof Jedis) {
			((Jedis) resource).close();
		}
	}

	public JedisPool(final redis.clients.jedis.JedisPool jedisPool, final String password) {
		this.commands = new AbstractCommands() {

			public JedisCommands getResource() {
				return getJedis(jedisPool, password);
			}

			public void close(JedisCommands resource) {
				closeResource(resource);
			}
		};
		this.binaryCommands = new AbstractBinaryCommands() {

			public BinaryJedisCommands getResource() {
				return getJedis(jedisPool, password);
			}

			public void close(BinaryJedisCommands resource) {
				closeResource(resource);
			}
		};
		this.binaryScriptingCommands = new AbstractBinaryScriptingCommands() {

			public redis.clients.jedis.BinaryScriptingCommands getResource() {
				return getJedis(jedisPool, password);
			}

			public void close(redis.clients.jedis.BinaryScriptingCommands resource) {
				closeResource(resource);
			}
		};
		this.scriptingCommands = new AbstractScriptingCommands() {

			public redis.clients.jedis.ScriptingCommands getResource() {
				return getJedis(jedisPool, password);
			}

			public void close(redis.clients.jedis.ScriptingCommands resource) {
				closeResource(resource);
			}
		};
		this.multiKeyBinaryCommands = new AbstractMultiKeyBinaryCommands() {

			public MultiKeyBinaryCommands getResource() {
				return getJedis(jedisPool, password);
			}

			public void close(MultiKeyBinaryCommands resource) {
				closeResource(resource);
			}
		};
		this.multiKeyCommands = new AbstractMultikeyCommands() {

			public MultiKeyCommands getResource() {
				return getJedis(jedisPool, password);
			}

			public void close(MultiKeyCommands resource) {
				closeResource(resource);
			}
		};
	}

	public scw.data.redis.Commands getCommands() {
		return commands;
	}

	public scw.data.redis.BinaryCommands getBinaryCommands() {
		return binaryCommands;
	}

	public scw.data.redis.BinaryScriptingCommands getBinaryScriptingCommands() {
		return binaryScriptingCommands;
	}

	public scw.data.redis.ScriptingCommands getScriptingCommands() {
		return scriptingCommands;
	}

	public scw.data.redis.MultiKeyBinaryCommands getMultiKeyBinaryCommands() {
		return multiKeyBinaryCommands;
	}

	public scw.data.redis.MultiKeyCommands getMultiKeyCommands() {
		return multiKeyCommands;
	}
}
