package shuchaowen.redis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import shuchaowen.core.exception.ShuChaoWenRuntimeException;

public class RedisByJedisPool implements Redis{
	private static final String SUCCESS = "OK";
	
	private final JedisPool jedisPool;
	//发生异常时是否中断
	private final boolean abnormalInterruption;
	
	public RedisByJedisPool(){
		this(false);
	}
	
	public RedisByJedisPool(boolean abnormalInterruption){
		JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
		jedisPoolConfig.setMaxTotal(512);
		jedisPoolConfig.setMaxIdle(200);
		jedisPoolConfig.setTestOnBorrow(true);
		this.jedisPool = new JedisPool(jedisPoolConfig , "localhost");
		this.abnormalInterruption = abnormalInterruption;
	}
	
	/**
	 * @param jedisPool
	 * @param abnormalInterruption 发生异常时是否中断
	 */
	public RedisByJedisPool(JedisPool jedisPool, boolean abnormalInterruption){
		this.jedisPool = jedisPool;
		this.abnormalInterruption = abnormalInterruption;
	}
	
	public JedisPool getJedisPool() {
		return jedisPool;
	}

	public String get(String key) {
		Jedis jedis = jedisPool.getResource();
		try {
			return jedis.get(key);
		} catch (Exception e) {
			if(abnormalInterruption){
				throw new ShuChaoWenRuntimeException(e);
			}else{
				e.printStackTrace();
			}
		}finally{
			if(jedis != null){
				jedis.close();
			}
		}
		return null;
	}

	public byte[] get(byte[] key) {
		Jedis jedis = jedisPool.getResource();
		try {
			return jedis.get(key);
		} catch (Exception e) {
			if(abnormalInterruption){
				throw new ShuChaoWenRuntimeException(e);
			}else{
				e.printStackTrace();
			}
		}finally{
			if(jedis != null){
				jedis.close();
			}
		}
		return null;
	}

	public String set(String key, String value) {
		Jedis jedis = jedisPool.getResource();
		try {
			return jedis.set(key, value);
		} catch (Exception e) {
			if(abnormalInterruption){
				throw new ShuChaoWenRuntimeException(e);
			}else{
				e.printStackTrace();
			}
		}finally{
			if(jedis != null){
				jedis.close();
			}
		}
		return null;
	}

	public String set(byte[] key, byte[] value) {
		Jedis jedis = jedisPool.getResource();
		try {
			return jedis.set(key, value);
		} catch (Exception e) {
			if(abnormalInterruption){
				throw new ShuChaoWenRuntimeException(e);
			}else{
				e.printStackTrace();
			}
		}finally{
			if(jedis != null){
				jedis.close();
			}
		}
		return null;
	}

	public String setex(String key, int seconds, String value) {
		Jedis jedis = jedisPool.getResource();
		try {
			return jedis.setex(key, seconds, value);
		} catch (Exception e) {
			if(abnormalInterruption){
				throw new ShuChaoWenRuntimeException(e);
			}else{
				e.printStackTrace();
			}
		}finally{
			if(jedis != null){
				jedis.close();
			}
		}
		return null;
	}
	

	public String setex(byte[] key, int seconds, byte[] value) {
		Jedis jedis = jedisPool.getResource();
		try {
			return jedis.setex(key, seconds, value);
		} catch (Exception e) {
			if(abnormalInterruption){
				throw new ShuChaoWenRuntimeException(e);
			}else{
				e.printStackTrace();
			}
		}finally{
			if(jedis != null){
				jedis.close();
			}
		}
		return null;
	}

	public Boolean exists(String key) {
		Jedis jedis = jedisPool.getResource();
		try {
			return jedis.exists(key);
		} catch (Exception e) {
			if(abnormalInterruption){
				throw new ShuChaoWenRuntimeException(e);
			}else{
				e.printStackTrace();
			}
		}finally{
			if(jedis != null){
				jedis.close();
			}
		}
		return null;
	}

	public Boolean exists(byte[] key) {
		Jedis jedis = jedisPool.getResource();
		try {
			return jedis.exists(key);
		} catch (Exception e) {
			if(abnormalInterruption){
				throw new ShuChaoWenRuntimeException(e);
			}else{
				e.printStackTrace();
			}
		}finally{
			if(jedis != null){
				jedis.close();
			}
		}
		return null;
	}

	public Long expire(String key, int seconds) {
		Jedis jedis = jedisPool.getResource();
		try {
			return jedis.expire(key, seconds);
		} catch (Exception e) {
			if(abnormalInterruption){
				throw new ShuChaoWenRuntimeException(e);
			}else{
				e.printStackTrace();
			}
		}finally{
			if(jedis != null){
				jedis.close();
			}
		}
		return null;
	}

	public Long expire(byte[] key, int seconds) {
		Jedis jedis = jedisPool.getResource();
		try {
			return jedis.expire(key, seconds);
		} catch (Exception e) {
			if(abnormalInterruption){
				throw new ShuChaoWenRuntimeException(e);
			}else{
				e.printStackTrace();
			}
		}finally{
			if(jedis != null){
				jedis.close();
			}
		}
		return null;
	}

	public Long delete(String key) {
		Jedis jedis = jedisPool.getResource();
		try {
			return jedis.del(key);
		} catch (Exception e) {
			if(abnormalInterruption){
				throw new ShuChaoWenRuntimeException(e);
			}else{
				e.printStackTrace();
			}
		}finally{
			if(jedis != null){
				jedis.close();
			}
		}
		return null;
	}

	public Long delete(byte[] key) {
		Jedis jedis = jedisPool.getResource();
		try {
			return jedis.del(key);
		} catch (Exception e) {
			if(abnormalInterruption){
				throw new ShuChaoWenRuntimeException(e);
			}else{
				e.printStackTrace();
			}
		}finally{
			if(jedis != null){
				jedis.close();
			}
		}
		return null;
	}

	public Long delete(String... key) {
		Jedis jedis = jedisPool.getResource();
		try {
			return jedis.del(key);
		} catch (Exception e) {
			if(abnormalInterruption){
				throw new ShuChaoWenRuntimeException(e);
			}else{
				e.printStackTrace();
			}
		}finally{
			if(jedis != null){
				jedis.close();
			}
		}
		return null;
	}

	public Long delete(byte[]... key) {
		Jedis jedis = jedisPool.getResource();
		try {
			return jedis.del(key);
		} catch (Exception e) {
			if(abnormalInterruption){
				throw new ShuChaoWenRuntimeException(e);
			}else{
				e.printStackTrace();
			}
		}finally{
			if(jedis != null){
				jedis.close();
			}
		}
		return null;
	}

	public Long hset(String key, String field, String value) {
		Jedis jedis = jedisPool.getResource();
		try {
			return jedis.hset(key, field, value);
		} catch (Exception e) {
			if(abnormalInterruption){
				throw new ShuChaoWenRuntimeException(e);
			}else{
				e.printStackTrace();
			}
		}finally{
			if(jedis != null){
				jedis.close();
			}
		}
		return null;
	}

	public Long hset(byte[] key, byte[] field, byte[] value) {
		Jedis jedis = jedisPool.getResource();
		try {
			return jedis.hset(key, field, value);
		} catch (Exception e) {
			if(abnormalInterruption){
				throw new ShuChaoWenRuntimeException(e);
			}else{
				e.printStackTrace();
			}
		}finally{
			if(jedis != null){
				jedis.close();
			}
		}
		return null;
	}

	public Long hsetnx(String key, String field, String value) {
		Jedis jedis = jedisPool.getResource();
		try {
			return jedis.hsetnx(key, field, value);
		} catch (Exception e) {
			if(abnormalInterruption){
				throw new ShuChaoWenRuntimeException(e);
			}else{
				e.printStackTrace();
			}
		}finally{
			if(jedis != null){
				jedis.close();
			}
		}
		return null;
	}

	public Long hsetnx(byte[] key, byte[] field, byte[] value) {
		Jedis jedis = jedisPool.getResource();
		try {
			return jedis.hsetnx(key, field, value);
		} catch (Exception e) {
			if(abnormalInterruption){
				throw new ShuChaoWenRuntimeException(e);
			}else{
				e.printStackTrace();
			}
		}finally{
			if(jedis != null){
				jedis.close();
			}
		}
		return null;
	}

	public Map<String, String> get(String... key) {
		Map<String, String> map = new HashMap<String, String>();
		Jedis jedis = jedisPool.getResource();
		try {
			for(String k : key){
				map.put(k, jedis.get(k));
			}
			return map;
		} catch (Exception e) {
			if(abnormalInterruption){
				throw new ShuChaoWenRuntimeException(e);
			}else{
				e.printStackTrace();
			}
		}finally{
			if(jedis != null){
				jedis.close();
			}
		}
		return null;
	}

	public Map<byte[], byte[]> get(byte[]... key) {
		Map<byte[], byte[]> map = new HashMap<byte[], byte[]>();
		Jedis jedis = jedisPool.getResource();
		try {
			for(byte[] k : key){
				map.put(k, jedis.get(k));
			}
			return map;
		} catch (Exception e) {
			if(abnormalInterruption){
				throw new ShuChaoWenRuntimeException(e);
			}else{
				e.printStackTrace();
			}
		}finally{
			if(jedis != null){
				jedis.close();
			}
		}
		return null;
	}

	public Long hdel(String key, String... fields) {
		Jedis jedis = jedisPool.getResource();
		try {
			return jedis.hdel(key, fields);
		} catch (Exception e) {
			if(abnormalInterruption){
				throw new ShuChaoWenRuntimeException(e);
			}else{
				e.printStackTrace();
			}
		}finally{
			if(jedis != null){
				jedis.close();
			}
		}
		return null;
	}

	public Long hdel(byte[] key, byte[]... fields) {
		Jedis jedis = jedisPool.getResource();
		try {
			return jedis.hdel(key, fields);
		} catch (Exception e) {
			if(abnormalInterruption){
				throw new ShuChaoWenRuntimeException(e);
			}else{
				e.printStackTrace();
			}
		}finally{
			if(jedis != null){
				jedis.close();
			}
		}
		return null;
	}

	public Boolean hexists(String key, String field) {
		Jedis jedis = jedisPool.getResource();
		try {
			return jedis.hexists(key, field);
		} catch (Exception e) {
			if(abnormalInterruption){
				throw new ShuChaoWenRuntimeException(e);
			}else{
				e.printStackTrace();
			}
		}finally{
			if(jedis != null){
				jedis.close();
			}
		}
		return null;
	}

	public Boolean hexists(byte[] key, byte[] field) {
		Jedis jedis = jedisPool.getResource();
		try {
			return jedis.hexists(key, field);
		} catch (Exception e) {
			if(abnormalInterruption){
				throw new ShuChaoWenRuntimeException(e);
			}else{
				e.printStackTrace();
			}
		}finally{
			if(jedis != null){
				jedis.close();
			}
		}
		return null;
	}
	
	public Long ttl(byte[] key) {
		Jedis jedis = jedisPool.getResource();
		try {
			return jedis.ttl(key);
		} catch (Exception e) {
			if(abnormalInterruption){
				throw new ShuChaoWenRuntimeException(e);
			}else{
				e.printStackTrace();
			}
		}finally{
			if(jedis != null){
				jedis.close();
			}
		}
		return null;
	}

	public Long ttl(String key) {
		Jedis jedis = jedisPool.getResource();
		try {
			return jedis.ttl(key);
		} catch (Exception e) {
			if(abnormalInterruption){
				throw new ShuChaoWenRuntimeException(e);
			}else{
				e.printStackTrace();
			}
		}finally{
			if(jedis != null){
				jedis.close();
			}
		}
		return null;
	}

	public Long setnx(String key, String value) {
		Jedis jedis = jedisPool.getResource();
		try {
			return jedis.setnx(key, value);
		} catch (Exception e) {
			if(abnormalInterruption){
				throw new ShuChaoWenRuntimeException(e);
			}else{
				e.printStackTrace();
			}
		}finally{
			if(jedis != null){
				jedis.close();
			}
		}
		return null;
	}

	public Long setnx(byte[] key, byte[] value) {
		Jedis jedis = jedisPool.getResource();
		try {
			return jedis.setnx(key, value);
		} catch (Exception e) {
			if(abnormalInterruption){
				throw new ShuChaoWenRuntimeException(e);
			}else{
				e.printStackTrace();
			}
		}finally{
			if(jedis != null){
				jedis.close();
			}
		}
		return null;
	}
	
	public Long incr(String key) {
		Jedis jedis = jedisPool.getResource();
		try {
			return jedis.incr(key);
		} catch (Exception e) {
			if(abnormalInterruption){
				throw new ShuChaoWenRuntimeException(e);
			}else{
				e.printStackTrace();
			}
		}finally{
			if(jedis != null){
				jedis.close();
			}
		}
		return null;
	}
	
	public Long incr(byte[] key) {
		Jedis jedis = jedisPool.getResource();
		try {
			return jedis.incr(key);
		} catch (Exception e) {
			if(abnormalInterruption){
				throw new ShuChaoWenRuntimeException(e);
			}else{
				e.printStackTrace();
			}
		}finally{
			if(jedis != null){
				jedis.close();
			}
		}
		return null;
	}

	public Long decr(String key) {
		Jedis jedis = jedisPool.getResource();
		try {
			return jedis.decr(key);
		} catch (Exception e) {
			if(abnormalInterruption){
				throw new ShuChaoWenRuntimeException(e);
			}else{
				e.printStackTrace();
			}
		}finally{
			if(jedis != null){
				jedis.close();
			}
		}
		return null;
	}

	public Long decr(byte[] key) {
		Jedis jedis = jedisPool.getResource();
		try {
			return jedis.decr(key);
		} catch (Exception e) {
			if(abnormalInterruption){
				throw new ShuChaoWenRuntimeException(e);
			}else{
				e.printStackTrace();
			}
		}finally{
			if(jedis != null){
				jedis.close();
			}
		}
		return null;
	}
	
	public List<String> hvals(String key) {
		Jedis jedis = jedisPool.getResource();
		try {
			return jedis.hvals(key);
		} catch (Exception e) {
			if(abnormalInterruption){
				throw new ShuChaoWenRuntimeException(e);
			}else{
				e.printStackTrace();
			}
		}finally{
			if(jedis != null){
				jedis.close();
			}
		}
		return null;
	}
	
	public List<byte[]> hvals(byte[] key) {
		Jedis jedis = jedisPool.getResource();
		try {
			return jedis.hvals(key);
		} catch (Exception e) {
			if(abnormalInterruption){
				throw new ShuChaoWenRuntimeException(e);
			}else{
				e.printStackTrace();
			}
		}finally{
			if(jedis != null){
				jedis.close();
			}
		}
		return null;
	}

	public String hget(String key, String field) {
		Jedis jedis = jedisPool.getResource();
		try {
			return jedis.hget(key, field);
		} catch (Exception e) {
			if(abnormalInterruption){
				throw new ShuChaoWenRuntimeException(e);
			}else{
				e.printStackTrace();
			}
		}finally{
			if(jedis != null){
				jedis.close();
			}
		}
		return null;
	}

	public byte[] hget(byte[] key, byte[] field) {
		Jedis jedis = jedisPool.getResource();
		try {
			return jedis.hget(key, field);
		} catch (Exception e) {
			if(abnormalInterruption){
				throw new ShuChaoWenRuntimeException(e);
			}else{
				e.printStackTrace();
			}
		}finally{
			if(jedis != null){
				jedis.close();
			}
		}
		return null;
	}

	public Long lpush(String key, String ...value) {
		Jedis jedis = jedisPool.getResource();
		try {
			return jedis.lpush(key, value);
		} catch (Exception e) {
			if(abnormalInterruption){
				throw new ShuChaoWenRuntimeException(e);
			}else{
				e.printStackTrace();
			}
		}finally{
			if(jedis != null){
				jedis.close();
			}
		}
		return null;
	}

	public Long lpush(byte[] key, byte[] ...value) {
		Jedis jedis = jedisPool.getResource();
		try {
			return jedis.lpush(key, value);
		} catch (Exception e) {
			if(abnormalInterruption){
				throw new ShuChaoWenRuntimeException(e);
			}else{
				e.printStackTrace();
			}
		}finally{
			if(jedis != null){
				jedis.close();
			}
		}
		return null;
	}

	public String rpop(String key) {
		Jedis jedis = jedisPool.getResource();
		try {
			return jedis.rpop(key);
		} catch (Exception e) {
			if(abnormalInterruption){
				throw new ShuChaoWenRuntimeException(e);
			}else{
				e.printStackTrace();
			}
		}finally{
			if(jedis != null){
				jedis.close();
			}
		}
		return null;
	}

	public byte[] rpop(byte[] key) {
		Jedis jedis = jedisPool.getResource();
		try {
			return jedis.rpop(key);
		} catch (Exception e) {
			if(abnormalInterruption){
				throw new ShuChaoWenRuntimeException(e);
			}else{
				e.printStackTrace();
			}
		}finally{
			if(jedis != null){
				jedis.close();
			}
		}
		return null;
	}
	
	public List<String> brpop(String... key) {
		Jedis jedis = jedisPool.getResource();
		try {
			return jedis.brpop(key);
		} catch (Exception e) {
			if(abnormalInterruption){
				throw new ShuChaoWenRuntimeException(e);
			}else{
				e.printStackTrace();
			}
		}finally{
			if(jedis != null){
				jedis.close();
			}
		}
		return null;
	}

	public List<byte[]> brpop(byte[]... key) {
		Jedis jedis = jedisPool.getResource();
		try {
			return jedis.brpop(key);
		} catch (Exception e) {
			if(abnormalInterruption){
				throw new ShuChaoWenRuntimeException(e);
			}else{
				e.printStackTrace();
			}
		}finally{
			if(jedis != null){
				jedis.close();
			}
		}
		return null;
	}

	public boolean set(String key, String value, String nxxx, String expx, long time) {
		Jedis jedis = jedisPool.getResource();
		try {
			return SUCCESS.equals(jedis.set(key, value, nxxx, expx, time));
		} catch (Exception e) {
			if(abnormalInterruption){
				throw new ShuChaoWenRuntimeException(e);
			}else{
				e.printStackTrace();
			}
		}finally{
			if(jedis != null){
				jedis.close();
			}
		}
		return false;
	}
	
	public boolean set(byte[] key, byte[] value, byte[] nxxx, byte[] expx, long time) {
		Jedis jedis = jedisPool.getResource();
		try {
			return SUCCESS.equals(jedis.set(key, value, nxxx, expx, time));
		} catch (Exception e) {
			if(abnormalInterruption){
				throw new ShuChaoWenRuntimeException(e);
			}else{
				e.printStackTrace();
			}
		}finally{
			if(jedis != null){
				jedis.close();
			}
		}
		return false;
	}
	
	public Object eval(String script, List<String> keys, List<String> args){
		Jedis jedis = jedisPool.getResource();
		try {
			return jedis.eval(script, keys, args);
		} catch (Exception e) {
			if(abnormalInterruption){
				throw new ShuChaoWenRuntimeException(e);
			}else{
				e.printStackTrace();
			}
		}finally{
			if(jedis != null){
				jedis.close();
			}
		}
		return null;
	}
	
	public List<byte[]> blpop(byte[]... key) {
		Jedis jedis = jedisPool.getResource();
		try {
			return jedis.blpop(key);
		} catch (Exception e) {
			if(abnormalInterruption){
				throw new ShuChaoWenRuntimeException(e);
			}else{
				e.printStackTrace();
			}
		}finally{
			if(jedis != null){
				jedis.close();
			}
		}
		return null;
	}

	public String lpop(String key) {
		Jedis jedis = jedisPool.getResource();
		try {
			return jedis.lpop(key);
		} catch (Exception e) {
			if(abnormalInterruption){
				throw new ShuChaoWenRuntimeException(e);
			}else{
				e.printStackTrace();
			}
		}finally{
			if(jedis != null){
				jedis.close();
			}
		}
		return null;
	}

	public byte[] lpop(byte[] key) {
		Jedis jedis = jedisPool.getResource();
		try {
			return jedis.lpop(key);
		} catch (Exception e) {
			if(abnormalInterruption){
				throw new ShuChaoWenRuntimeException(e);
			}else{
				e.printStackTrace();
			}
		}finally{
			if(jedis != null){
				jedis.close();
			}
		}
		return null;
	}
	
	public Long zadd(byte[] key, double score, byte[] member) {
		Jedis jedis = jedisPool.getResource();
		try {
			return jedis.zadd(key, score, member);
		} catch (Exception e) {
			if(abnormalInterruption){
				throw new ShuChaoWenRuntimeException(e);
			}else{
				e.printStackTrace();
			}
		}finally{
			if(jedis != null){
				jedis.close();
			}
		}
		return null;
	}
	
	public Long sadd(byte[] key, byte[] ...members) {
		Jedis jedis = jedisPool.getResource();
		try {
			return jedis.sadd(key, members);
		} catch (Exception e) {
			if(abnormalInterruption){
				throw new ShuChaoWenRuntimeException(e);
			}else{
				e.printStackTrace();
			}
		}finally{
			if(jedis != null){
				jedis.close();
			}
		}
		return null;
	}
	
	public Long srem(byte[] key, byte[] ...member) {
		Jedis jedis = jedisPool.getResource();
		try {
			return jedis.srem(key, member);
		} catch (Exception e) {
			if(abnormalInterruption){
				throw new ShuChaoWenRuntimeException(e);
			}else{
				e.printStackTrace();
			}
		}finally{
			if(jedis != null){
				jedis.close();
			}
		}
		return null;
	}
	
	public Set<byte[]> smembers(byte[] key) {
		Jedis jedis = jedisPool.getResource();
		try {
			return jedis.smembers(key);
		} catch (Exception e) {
			if(abnormalInterruption){
				throw new ShuChaoWenRuntimeException(e);
			}else{
				e.printStackTrace();
			}
		}finally{
			if(jedis != null){
				jedis.close();
			}
		}
		return null;
	}

	public Boolean sIsMember(byte[] key, byte[] member) {
		Jedis jedis = jedisPool.getResource();
		try {
			return jedis.sismember(key, member);
		} catch (Exception e) {
			if(abnormalInterruption){
				throw new ShuChaoWenRuntimeException(e);
			}else{
				e.printStackTrace();
			}
		}finally{
			if(jedis != null){
				jedis.close();
			}
		}
		return null;
	}
	
	public List<byte[]> hmget(byte[] key, byte[] ...fields) {
		Jedis jedis = jedisPool.getResource();
		try {
			return jedis.hmget(key, fields);
		} catch (Exception e) {
			if(abnormalInterruption){
				throw new ShuChaoWenRuntimeException(e);
			}else{
				e.printStackTrace();
			}
		}finally{
			if(jedis != null){
				jedis.close();
			}
		}
		return null;
	}
}
