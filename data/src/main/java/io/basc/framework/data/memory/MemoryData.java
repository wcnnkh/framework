package io.basc.framework.data.memory;

import io.basc.framework.data.CAS;

public interface MemoryData {
	long incr(long delta);

	long incr(long delta, long initialValue);

	long decr(long delta);

	long decr(long delta, long initialValue);

	void setExpire(long exp);

	boolean set(CAS<? extends Object> value);

	void touch();

	<T> CAS<T> get();

	void set(Object value);

	/**
	 * 如果不存在就插入
	 * 
	 * @param value
	 * @return
	 */
	boolean setIfAbsent(Object value);
	
	boolean setIfPresent(Object value);

	/**
	 * 判断cas是否匹配
	 * 
	 * @param cas
	 * @return
	 */
	boolean incrCasAndCompare(long cas);

	/**
	 * 如果不存在就插入
	 * 
	 * @param value
	 * @return
	 */
	boolean setIfAbsent(CAS<Object> value);

	boolean isExpire();
}
