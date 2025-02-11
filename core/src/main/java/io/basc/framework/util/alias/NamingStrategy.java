package io.basc.framework.util.alias;

import java.util.function.Predicate;

public interface NamingStrategy<K> extends Predicate<K> {
	/**
	 * 测试名称是否符合此策略
	 */
	@Override
	boolean test(K name);

	/**
	 * 是否是指定开头
	 * 
	 * @param name
	 * @param prefix
	 * @return
	 */
	boolean startsWith(K name, K prefix);

	/**
	 * 指定开头的展示
	 * 
	 * @param name
	 * @param prefix
	 * @return
	 */
	K display(K name, K prefix);

	/**
	 * 拼接
	 * 
	 * @param left
	 * @param right
	 * @return
	 */
	K join(K left, K right);
}
