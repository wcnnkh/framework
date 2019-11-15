package scw.data;

/**
 * 计数器
 * @author shuchaowen
 *
 */
public interface Counter {
	long incr(String key, long delta);

	long incr(String key, long delta, long initialValue);

	long decr(String key, long delta);

	long decr(String key, long delta, long initialValue);
}
