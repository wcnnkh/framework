package shuchaowen.core.db.storage.redis;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtobufIOUtil;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import shuchaowen.core.db.proxy.BeanProxy;
import shuchaowen.core.db.proxy.BeanProxyMethodInterceptor;
import shuchaowen.core.db.storage.AbstractHotSpotDataCacheStorage;
import shuchaowen.core.util.XTime;

public class JedisPoolHotSpotDataCacheFactory extends AbstractHotSpotDataCacheStorage{
	private JedisPool jedisPool;
	
	public JedisPoolHotSpotDataCacheFactory(){
		this(new LocalJedisPool().getJedisPool());
	}
	
	public JedisPoolHotSpotDataCacheFactory(JedisPool jedisPool){
		this((int) ((7 * XTime.ONE_DAY) / 1000), jedisPool);
	}
	
	public JedisPoolHotSpotDataCacheFactory(int exp, JedisPool jedisPool){
		this("", exp, jedisPool);
	}
	
	public JedisPoolHotSpotDataCacheFactory(String prefix, int exp, JedisPool jedisPool) {
		super(prefix, exp);
		this.jedisPool = jedisPool;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public <T> T getAndTouch(Class<T> type, String key, int exp) throws Exception {
		byte[] keyByte = key.getBytes("UTF-8");
		

		Jedis jedis = jedisPool.getResource();
		byte[] data = jedis.get(keyByte);
		if(data != null){
			if(exp > 0){
				jedis.expire(keyByte, exp);
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

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void set(String key, int exp, Object data) throws Exception {
		byte[] keyByte = key.getBytes("UTF-8");
		Schema schema = RuntimeSchema.getSchema(data.getClass());
		byte[] dataByte = ProtobufIOUtil.toByteArray(data, schema, LinkedBuffer.allocate(512));
		if(dataByte == null){
			return ;
		}
		
		Jedis jedis = jedisPool.getResource();
		if(exp > 0){
			jedis.setex(keyByte, exp, dataByte);
		}else{
			jedis.set(keyByte, dataByte);
		}
		jedis.close();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public boolean add(String key, int exp, Object data) throws Exception {
		byte[] keyByte = key.getBytes("UTF-8");
		Schema schema = RuntimeSchema.getSchema(data.getClass());
		byte[] dataByte = ProtobufIOUtil.toByteArray(data, schema, LinkedBuffer.allocate(512));
		if(dataByte == null){
			return false;
		}
		
		Jedis jedis = jedisPool.getResource();
		if(exp > 0){
			jedis.setex(keyByte, exp, dataByte);
		}else{
			jedis.set(keyByte, dataByte);
		}
		jedis.close();
		return true;
	}

	@Override
	public boolean delete(String key) throws Exception {
		byte[] keyByte = key.getBytes("UTF-8");
		Jedis jedis = jedisPool.getResource();
		jedis.del(keyByte);
		jedis.close();
		return true;
	}
}
