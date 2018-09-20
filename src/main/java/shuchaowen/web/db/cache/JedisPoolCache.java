package shuchaowen.web.db.cache;

import com.alibaba.fastjson.JSONObject;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import shuchaowen.core.db.cache.Cache;
import shuchaowen.core.db.cache.CacheUtils;
import shuchaowen.core.db.proxy.BeanProxy;
import shuchaowen.core.db.proxy.BeanProxyMethodInterceptor;

/**
 * 总是不推荐使用此类，因为redis的结构丰富，应该根据业务来实现缓存
 * @author shuchaowen
 *
 */
public class JedisPoolCache implements Cache{
	private JedisPool jedisPool;
	private String prefix;
	private int exp;
	
	public JedisPoolCache(JedisPool jedisPool, int exp){
		this(jedisPool, "", exp);
	}
	
	public JedisPoolCache(JedisPool jedisPool, String prefix, int exp){
		this.jedisPool = jedisPool;
		this.prefix = prefix;
		this.exp = exp;
	}
	
	public <T> T getById(Class<T> type, String tableName, Object... params) {
		String key = prefix + CacheUtils.getObjectKey(type, params);
		Jedis jedis = jedisPool.getResource();
		String content = jedis.get(key);
		if(content == null){
			return null;
		}
		
		if(exp > 0){
			jedis.expire(key, exp);
		}
		jedis.close();
		T t = JSONObject.parseObject(content, BeanProxyMethodInterceptor.getCglibProxyBean(type));
		((BeanProxy)t).startListen();
		return t;
	}

	public void save(Object bean) {
		String data = JSONObject.toJSONString(bean);
		if(data == null){
			return ;
		}
		
		Jedis jedis = jedisPool.getResource();
		if(exp > 0){
			jedis.setex(prefix + CacheUtils.getObjectKey(bean), exp, data);
		}else{
			jedis.set(prefix + CacheUtils.getObjectKey(bean), data);
		}
		jedis.close();
	}

	public void update(Object bean) {
		save(bean);
	}

	public void delete(Object bean) {
		Jedis jedis = jedisPool.getResource();
		jedis.del(prefix + CacheUtils.getObjectKey(bean));
		jedis.close();
	}

	public void saveOrUpdate(Object bean) {
		update(bean);
	}
}
