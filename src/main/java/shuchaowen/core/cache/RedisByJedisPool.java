package shuchaowen.core.cache;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import shuchaowen.core.exception.ShuChaoWenRuntimeException;

public class RedisByJedisPool implements Redis{
	private final JedisPool jedisPool;
	//发生异常时是否中断
	private final boolean abnormalInterruption;
	
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

	public boolean exists(String key) {
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
		return false;
	}

	public boolean exists(byte[] key) {
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
		return false;
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

}
