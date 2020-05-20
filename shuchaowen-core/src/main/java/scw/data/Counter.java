package scw.data;

import scw.beans.annotation.Bean;

/**
 * 计数器
 * @author shuchaowen
 *
 */

@Bean(proxy=false)
public interface Counter {
	boolean isExist(String key);
	
	long incr(String key, long delta);

	long incr(String key, long delta, long initialValue);

	long decr(String key, long delta);

	long decr(String key, long delta, long initialValue);
}
