package io.basc.framework.util.alias;

import java.util.function.Predicate;

public interface NamingStrategy extends Predicate<String> {
	/**
	 * 测试名称是否符合此策略
	 */
	@Override
	boolean test(String name);

	/**
	 * 是否是指定开头
	 * 
	 * @param name
	 * @param prefix
	 * @return
	 */
	boolean startsWith(String name, String prefix);

	/**
	 * 指定开头的展示
	 * 
	 * @param name
	 * @param prefix
	 * @return
	 */
	String display(String name, String prefix);

	/**
	 * 拼接
	 * 
	 * @param left
	 * @param right
	 * @return
	 */
	String join(String left, String right);
}
