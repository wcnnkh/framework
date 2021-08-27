package io.basc.framework.data;

public interface TemporaryCounter extends Counter{
	
	long incr(String key, long delta, long initialValue, int exp);

	long decr(String key, long delta, long initialValue, int exp);
}
