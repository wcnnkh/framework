package scw.data;

import scw.beans.annotation.AutoImpl;

/**
 * 临时缓存
 * 
 * @author shuchaowen
 *
 */
@AutoImpl({ DataTemplete.class })
public interface TemporaryCache extends Cache {
	<T> T getAndTouch(String key, int exp);

	boolean touch(String key, int exp);

	boolean add(String key, int exp, Object value);

	void set(String key, int exp, Object value);
}
