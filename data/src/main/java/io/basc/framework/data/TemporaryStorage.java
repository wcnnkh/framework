package io.basc.framework.data;

/**
 * 临时存储
 * 
 * @author shuchaowen
 *
 */
public interface TemporaryStorage extends Storage {

	default <T> T getAndTouch(String key, long exp) {
		T value = get(key);
		if (value != null) {
			touch(key, exp);
		}
		return value;
	}

	boolean touch(String key, long exp);

	/**
	 * 如果数据不存在就添加
	 * 
	 * @param key
	 * @param exp   过期时间(秒)
	 * @param value
	 * @return
	 */
	boolean add(String key, long exp, Object value);

	/**
	 * @param key
	 * @param exp   过期时间(秒)
	 * @param value
	 */
	void set(String key, long exp, Object value);
}
