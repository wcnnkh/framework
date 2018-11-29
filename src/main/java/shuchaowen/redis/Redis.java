package shuchaowen.redis;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface Redis {
	String get(String key);
	
	String getAndTouch(String key, int exp);

	byte[] get(byte[] key);
	
	byte[] getAndTouch(byte[] key, int exp);

	String set(String key, String value);

	String set(byte[] key, byte[] value);

	Long setnx(String key, String value);

	Long setnx(byte[] key, byte[] value);

	String setex(String key, int seconds, String value);

	String setex(byte[] key, int seconds, byte[] value);

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

	Boolean exists(byte[] key);

	Long expire(String key, int seconds);

	Long expire(byte[] key, int seconds);

	Long delete(String key);

	Long delete(byte[] key);

	Long delete(String... key);

	Long delete(byte[]... key);

	Long hset(String key, String field, String value);

	Long hset(byte[] key, byte[] field, byte[] value);

	Long hsetnx(String key, String field, String value);

	Long hsetnx(byte[] key, byte[] field, byte[] value);

	Map<String, String> get(String... key);

	Map<byte[], byte[]> get(byte[]... key);

	Long hdel(String key, String... fields);

	Long hdel(byte[] key, byte[]... fields);

	Boolean hexists(String key, String field);

	Boolean hexists(byte[] key, byte[] field);

	Long ttl(String key);

	Long ttl(byte[] key);

	Long incr(String key);

	Long incr(byte[] key);

	Long decr(String key);

	Long decr(byte[] key);

	List<String> hvals(String key);

	List<byte[]> hvals(byte[] key);

	String hget(String key, String field);

	byte[] hget(byte[] key, byte[] field);
	
	List<byte[]> hmget(byte[] key, byte[] ...fields);

	Long lpush(String key, String... value);

	Long lpush(byte[] key, byte[]... value);
	
	Long rpush(String key, String ...value);
	
	Long rpush(byte[] key, byte[] ...value);

	String rpop(String key);

	byte[] rpop(byte[] key);
	
	List<String> blpop(String ...key);
	
	List<byte[]> blpop(byte[] ...key);

	List<String> brpop(String... key);

	List<byte[]> brpop(byte[]... key);
	
	Object eval(String script, List<String> keys, List<String> args);
	
	String lpop(String key);
	
	byte[] lpop(byte[] key);
	
	Set<byte[]> smembers(byte[] key);

	Long srem(byte[] key, byte[] ...member);
	
	Long sadd(byte[] key, byte[] ...members);
	
	Long zadd(byte[] key, double score, byte[] member);
	
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
	boolean set(byte[] key, byte[] value, byte[] nxxx, byte[] expx, long time);
	
	Boolean sIsMember(byte[] key, byte[] member);
	
	byte[] rpoplpush(byte[] srckey, byte[] dstkey);
	
	String rpoplpush(String srckey, String dstkey);
	
	String brpoplpush(String source, String destination, int timeout);
	
	byte[] brpoplpush(byte[] source, byte[] destination, int timeout);
	
	String lindex(String key, int index);
	
	byte[] lindex(byte[] key, int index);
	
	Long llen(String key);
	
	Long llen(byte[] key);
}
