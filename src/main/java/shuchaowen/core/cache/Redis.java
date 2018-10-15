package shuchaowen.core.cache;

import java.util.List;
import java.util.Map;

public interface Redis {
	String get(String key);
	
	byte[] get(byte[] key);
	
	String set(String key, String value);
	
	String set(byte[] key, byte[] value);
	
	Long setnx(String key, String value);
	
	Long setnx(byte[] key, byte[] value);
	
	String setex(String key, int seconds, String value);
	
	String setex(byte[] key, int seconds, byte[] value);
	
	Boolean exists(String key);
	
	Boolean exists(byte[] key);
	
	Long expire(String key, int seconds);
	
	Long expire(byte[] key, int seconds);
	
	Long delete(String key);
	
	Long delete(byte[] key);
	
	Long delete(String ...key);
	
	Long delete(byte[] ...key);
	
	Long hset(String key, String field, String value);
	
	Long hset(byte[] key, byte[] field, byte[] value);
	
	Long hsetnx(String key, String field, String value);
	
	Long hsetnx(byte[] key, byte[] field, byte[] value);
	
	Map<String, String> get(String ...key);
	
	Map<byte[], byte[]> get(byte[] ...key);
	
	Long hdel(String key, String ...fields);
	
	Long hdel(byte[] key, byte[] ...fields);
	
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
}
