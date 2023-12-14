package io.basc.framework.data;

public interface Data<T> extends CAS<T> {
	long decr(long delta);

	long decr(long delta, long initialValue);

	long incr(long delta);

	long incr(long delta, long initialValue);

	void set(T value);

	boolean set(T value, long lastModified);

	/**
	 * 如果不存在就插入
	 * 
	 * @param value
	 * @return
	 */
	boolean setIfAbsent(T value);

	/**
	 * 如果存在就插入
	 * 
	 * @param value
	 * @return
	 */
	boolean setIfPresent(T value);

	/**
	 * 刷新过期时间
	 */
	void touch();
}
