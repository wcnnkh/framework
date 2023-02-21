package io.basc.framework.data.tree;

/**
 * @author myd
 * @date 2022/10/9 17:11
 */

public interface Node<T> {

	/**
	 *
	 * 返回左节点
	 *
	 * @return
	 */
	Node<T> left();

	/**
	 *
	 * 返回右节点
	 *
	 * @return
	 */
	Node<T> right();

	/**
	 *
	 * 树节点存储的对象；
	 * <p>
	 *
	 * 如果不是数字类型的数据，根据需要重写toString()
	 *
	 * @return
	 */
	T value();

	/**
	 *
	 * 红黑树打印红色，黑色节点；
	 *
	 * 节点红色返回：true
	 *
	 * 节点黑色返回：false
	 *
	 * @return
	 */
	boolean red();
}
