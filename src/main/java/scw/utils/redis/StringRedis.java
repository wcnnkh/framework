package scw.utils.redis;

import java.util.List;
import java.util.Map;

public interface StringRedis {
	String get(String key);

	String getAndTouch(String key, int exp);
	
	String set(String key, String value);
	
	Long setnx(String key, String value);
	
	String setex(String key, int seconds, String value);
	
	/**
	 * EX second ：设置键的过期时间为 second 秒。 SET key value EX second 效果等同于 SETEX key second value 。 
	 * PX millisecond ：设置键的过期时间为 millisecond 毫秒。 SET key value PX millisecond 效果等同于 PSETEX key millisecond value 。
	 * NX ：只在键不存在时，才对键进行设置操作。 SET key value NX 效果等同于 SETNX key value 。 
	 * XX ：只在键已经存在时，才对键进行设置操作。
	 * @param key
	 * @param value
	 * @param nxxx
	 * @param expe
	 * @param time
	 * @return
	 */
	boolean set(String key, String value, String nxxx, String expe, long time);
	
	Boolean exists(String key);
	
	Long expire(String key, int seconds);
	
	Long delete(String key);
	
	Long delete(String... key);
	
	Long hset(String key, String field, String value);
	
	Long hsetnx(String key, String field, String value);
	
	Map<String, String> get(String... key);
	
	Long hdel(String key, String... fields);
	
	Boolean hexists(String key, String field);
	
	Long ttl(String key);
	
	Long incr(String key);
	
	Long decr(String key);
	
	List<String> hvals(String key);
	
	String hget(String key, String field);
	
	Long lpush(String key, String... value);
	
	Long rpush(String key, String ...value);
	
	String rpop(String key);
	
	List<String> blpop(String ...key);
	
	List<String> brpop(String... key);
	
	Object eval(String script, List<String> keys, List<String> args);
	
	String lpop(String key);
	
	String rpoplpush(String srckey, String dstkey);
	
	String brpoplpush(String source, String destination, int timeout);
	
	String lindex(String key, int index);

	Long llen(String key);
}
