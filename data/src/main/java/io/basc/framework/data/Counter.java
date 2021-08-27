package io.basc.framework.data;


/**
 * 计数器
 * @author shuchaowen
 *
 */

public interface Counter {
	boolean isExist(String key);
	
	long incr(String key, long delta);

	long incr(String key, long delta, long initialValue);

	long decr(String key, long delta);

	long decr(String key, long delta, long initialValue);
}
