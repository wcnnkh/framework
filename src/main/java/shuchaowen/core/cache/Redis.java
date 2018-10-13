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
	
	boolean delete(String key);
	
	boolean delete(byte[] key);
}
