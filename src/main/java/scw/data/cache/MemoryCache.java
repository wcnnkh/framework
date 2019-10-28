package scw.data.cache;

public interface MemoryCache {
	long incr(long delta);
	
	long incr(long delta, long initialValue);
	
	long decr(long delta);

	long decr(long delta, long initialValue);

	void setExpire(int exp);

	void touch();

	Object get();

	void set(Object value);

	/**
	 * 如果不存在就插入
	 * 
	 * @param value
	 * @return
	 */
	boolean setIfAbsent(Object value);

	boolean isExpire(long currentTimeMillis);
}
