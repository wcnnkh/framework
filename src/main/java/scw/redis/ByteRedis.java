package scw.redis;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ByteRedis {
	byte[] get(byte[] key);

	byte[] getAndTouch(byte[] key, int exp);

	String set(byte[] key, byte[] value);

	Long setnx(byte[] key, byte[] value);

	String setex(byte[] key, int seconds, byte[] value);

	Boolean exists(byte[] key);

	Long expire(byte[] key, int seconds);

	Long delete(byte[] key);

	Long delete(byte[]... key);

	Long hset(byte[] key, byte[] field, byte[] value);

	Long hsetnx(byte[] key, byte[] field, byte[] value);

	Map<byte[], byte[]> get(byte[]... key);

	Long hdel(byte[] key, byte[]... fields);

	Boolean hexists(byte[] key, byte[] field);

	Long ttl(byte[] key);

	Long incr(byte[] key);

	Long decr(byte[] key);

	List<byte[]> hvals(byte[] key);

	byte[] hget(byte[] key, byte[] field);

	List<byte[]> hmget(byte[] key, byte[]... fields);

	Long lpush(byte[] key, byte[]... value);

	Long rpush(byte[] key, byte[]... value);

	byte[] rpop(byte[] key);

	List<byte[]> blpop(byte[]... key);

	List<byte[]> brpop(byte[]... key);

	byte[] lpop(byte[] key);

	Set<byte[]> smembers(byte[] key);

	Long srem(byte[] key, byte[]... member);

	Long sadd(byte[] key, byte[]... members);

	Long zadd(byte[] key, double score, byte[] member);

	/**
	 * EX second ：设置键的过期时间为 second 秒。 SET key value EX second 效果等同于 SETEX key
	 * second value 。 PX millisecond ：设置键的过期时间为 millisecond 毫秒。 SET key value PX
	 * millisecond 效果等同于 PSETEX key millisecond value 。 NX ：只在键不存在时，才对键进行设置操作。
	 * SET key value NX 效果等同于 SETNX key value 。 XX ：只在键已经存在时，才对键进行设置操作。
	 * 
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

	byte[] brpoplpush(byte[] source, byte[] destination, int timeout);

	byte[] lindex(byte[] key, int index);

	Long llen(byte[] key);
}
