package io.basc.framework.data.memory;

public interface MemoryData {
	long incr(long delta);

	long incr(long delta, long initialValue);

	long decr(long delta);

	long decr(long delta, long initialValue);

	void setExpire(long exp);

	boolean set(CAS<? extends Object> value);
	
	long getRemainingSurvivalTime();

	void touch();

	<T> CAS<T> get();

	void set(Object value);

	boolean setIfAbsent(Object value);
	
	boolean setIfPresent(Object value);

	boolean incrCasAndCompare(long cas);

	boolean setIfAbsent(CAS<Object> value);

	boolean isExpire();
}
