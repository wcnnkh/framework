package shuchaowen.core.cache;

public interface Redis {
	String get(String key);
	
	byte[] get(byte[] key);
	
	String set(String key, String value);
	
	String set(byte[] key, byte[] value);
	
	String setex(String key, int seconds, String value);
	
	String setex(byte[] key, int seconds, byte[] value);
	
	boolean exists(String key);
	
	boolean exists(byte[] key);
	
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
}
