package shuchaowen.core.db.cache.redis;

import java.io.UnsupportedEncodingException;

import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import shuchaowen.core.db.cache.Cache;
import shuchaowen.core.db.cache.CacheUtils;
import shuchaowen.core.db.proxy.BeanProxy;
import shuchaowen.core.db.proxy.BeanProxyMethodInterceptor;
import shuchaowen.core.exception.ShuChaoWenRuntimeException;
import shuchaowen.core.util.XTime;

/**
 * 总是不推荐使用此类，因为redis的结构丰富，应该根据业务来实现缓存
 * @author shuchaowen
 *
 */
public class JedisPoolCache implements Cache{
	private JedisPool jedisPool;
	private String prefix;
	private int exp;
	
	public JedisPoolCache(JedisPool jedisPool){
		this(jedisPool, (int)((7 * XTime.ONE_DAY)/1000));
	}
	
	public JedisPoolCache(JedisPool jedisPool, int exp){
		this(jedisPool, "", exp);
	}
	
	public JedisPoolCache(JedisPool jedisPool, String prefix, int exp){
		this.jedisPool = jedisPool;
		this.prefix = prefix;
		this.exp = exp;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <T> T getById(Class<T> type, String tableName, Object... params) {
		byte[] key;
		try {
			key = (prefix + CacheUtils.getObjectKey(type, params)).getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new ShuChaoWenRuntimeException(e);
		}

		Jedis jedis = jedisPool.getResource();
		byte[] data = jedis.get(key);
		if(data != null){
			if(exp > 0){
				jedis.expire(key, exp);
			}
		}
		jedis.close();
		
		if(data == null || data.length == 0){
			return null;
		}
		
		T t = BeanProxyMethodInterceptor.newInstance(type);
		Schema schema = RuntimeSchema.getSchema(type);
		ProtostuffIOUtil.mergeFrom(data, t, schema);
		((BeanProxy)t).startListen();
		return t;
	}
	
	public void save(Object bean) {
		//ignore
	}

	public void update(Object bean) {
		delete(bean);
	}

	public void delete(Object bean) {
		byte[] key;
		try {
			key = (prefix + CacheUtils.getObjectKey(bean)).getBytes("UTF-8");
		} catch (Exception e) {
			throw new ShuChaoWenRuntimeException(e);
		}
		
		Jedis jedis = jedisPool.getResource();
		jedis.del(key);
		jedis.close();
	}

	public void saveOrUpdate(Object bean) {
		delete(bean);
	}
}
