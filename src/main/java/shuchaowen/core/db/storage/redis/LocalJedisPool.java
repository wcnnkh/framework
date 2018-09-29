package shuchaowen.core.db.storage.redis;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class LocalJedisPool {
	private JedisPool jedisPool;
	
	public LocalJedisPool(){
		JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
		jedisPoolConfig.setMaxTotal(1024);
		jedisPoolConfig.setMaxIdle(500);
		jedisPoolConfig.setTestOnBorrow(true);
		jedisPool = new JedisPool(jedisPoolConfig , "192.168.0.66");
	}
	
	public JedisPool getJedisPool(){
		return jedisPool;
	}
}
