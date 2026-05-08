package run.soeasy.framework.tree;

import lombok.NonNull;

/**
 * 树形结构事件监听器，用于监听树形结构遍历/解析过程中的各类核心事件（如对象开始/结束、数组开始/结束、键/值触发等）。
 * <p>
 * 事件触发默认顺序（以对象节点为例）： onObjectBegin() -&gt; onObjectKey() -&gt; onNodeValue() -&gt; onObjectEnd()
 * <p>
 * 以数组节点为例： onArrayBegin() -&gt; onNodeValue()（数组元素）-&gt; onArrayEnd()
 *
 * @author soeasy.run
 * @param <T> 树形节点值的类型
 */
public interface TreeListener<T> {

	/**
	 * 当开始遍历/解析一个数组类型节点时触发。
	 */
	void onArrayBegin();

	/**
	 * 当结束遍历/解析一个数组类型节点时触发。
	 */
	default void onArrayEnd() {}

	/**
	 * 当树形遍历/解析发生异常时触发
	 *
	 * @param error 遍历/解析过程中抛出的异常
	 */
	default void onError(Throwable error) {}

	/**
	 * 当遍历/解析到树形节点的核心值时触发（包括对象属性值、数组元素值、根节点独立值等）。
	 *
	 * @param value 树形节点的核心值
	 */
	void onNodeValue(T value);;

	/**
	 * 当开始遍历/解析一个对象类型节点时触发。
	 */
	void onObjectBegin();

	/**
	 * 当结束遍历/解析一个对象类型节点时触发。
	 */
	default void onObjectEnd() {};

	/**
	 * 当遍历/解析到对象节点的键值对键（属性名）时触发。
	 *
	 * @param key 对象节点的属性名称（非空）
	 */
	void onObjectKey(@NonNull String key);;
}