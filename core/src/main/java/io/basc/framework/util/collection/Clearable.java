package io.basc.framework.util.collection;

/**
 * 定义可以清空的行为
 * 
 * @author shuchaowen
 *
 */
public interface Clearable {
	/**
	 * 是否为空
	 * 
	 * @return
	 */
	boolean isEmpty();

	/**
	 * 清空
	 */
	void clear();
}
