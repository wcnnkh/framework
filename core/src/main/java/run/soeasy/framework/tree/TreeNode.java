package run.soeasy.framework.tree;

import java.util.stream.Stream;

import lombok.NonNull;
import run.soeasy.framework.core.domain.KeyValue;

/**
 * 通用树形节点接口，适配「List/Map 可复用容器」和「单向数据流」两种底层实现。
 *
 * @author soeasy.run
 * @param <T> 节点值类型
 */
public interface TreeNode<T> {
	/**
	 * 判断当前节点是否为「数据流支撑的节点」（底层是单向、一次性数据流，如 JSON 字符流解析节点）。
	 * <p>
	 * 不同底层实现的约束说明：
	 * 1.  返回 true（数据流底层）：
	 *    -  节点数据仅可单向消费一次，不可重复遍历；
	 *    -  子节点的 {@link #arrayStream()} 和 {@link #objectStream()} 不可同时遍历（排他性）；
	 *    -  流实例为单例，重复调用返回同一个流，遍历后即耗尽失效；
	 * 2.  返回 false（List/Map 等容器底层）：
	 *    -  节点数据可重复使用，支持多次遍历；
	 *    -  子节点的两个流可同时遍历（非排他）；
	 *    -  流实例可重复生成（或单例），每次遍历均从容器中重新获取数据。
	 * <p>
	 * 举例场景：JSON 字符流解析时，节点为数据流支撑（返回true）；内存中构建的树形节点为容器支撑（返回false）。
	 *
	 * @return true=数据流支撑节点，false=容器（List/Map等）支撑节点
	 */
	boolean isStreamNode();

	/**
	 * 获取当前树形节点的核心值。
	 *
	 * @return 节点值（具体实现可定义空值场景，如 null 或默认值）
	 */
	T getValue();

	/**
	 * 判断当前节点是否为数组类型节点（仅当返回true时，{@link #arrayStream()} 返回有效流）。
	 *
	 * @return true=数组节点，false=非数组节点
	 */
	boolean isArray();

	/**
	 * 判断当前节点是否为对象类型节点（仅当返回true时，{@link #objectStream()} 返回有效流）。
	 *
	 * @return true=对象节点，false=非对象节点
	 */
	boolean isObject();

	/**
	 * 获取数组节点的子节点流。
	 * <p>
	 * 流的特性依赖底层实现（参考 {@link #isStreamNode()} 的约束说明）。
	 *
	 * @return 数组子节点流（非数组节点返回的流可能为空或无效，由实现类保证）
	 */
	Stream<TreeNode<T>> arrayStream();

	/**
	 * 获取对象节点的键值对子节点流。
	 * <p>
	 * 流的特性依赖底层实现（参考 {@link #isStreamNode()} 的约束说明）。
	 *
	 * @return 对象子节点键值对流（非对象节点返回的流可能为空或无效，由实现类保证）
	 */
	Stream<KeyValue<String, TreeNode<T>>> objectStream();

	/**
	 * 遍历当前树形节点及其所有子节点，触发 {@link TreeListener} 对应的回调事件。
	 * <p>
	 * 触发顺序遵循树形结构遍历规范：
	 * 1.  对象节点：onObjectBegin() -&gt; 遍历键值对（onObjectKey() -&gt; 子节点read()） -&gt; onObjectEnd()
	 * 2.  数组节点：onArrayBegin() -&gt; 遍历数组元素（子节点read()） -&gt; onArrayEnd()
	 * 3.  普通值节点：直接触发 onNodeValue()
	 *
	 * @param listener 树形结构事件监听器，不可为null
	 */
	default void read(@NonNull TreeListener<T> listener) {
		// 分支1：当前节点是对象类型节点
		if (this.isObject()) {
			// 触发对象开始事件
			listener.onObjectBegin();
			// 遍历对象键值对流，处理每个键和子节点
			this.objectStream().forEach(keyValue -> {
				// 触发对象键的回调（传递属性名）
				listener.onObjectKey(keyValue.getKey());
				// 递归遍历子节点，触发子节点的对应事件
				keyValue.getValue().read(listener);
			});
			// 触发对象结束事件（遍历完所有键值对后）
			listener.onObjectEnd();
		}
		// 分支2：当前节点是数组类型节点
		else if (this.isArray()) {
			// 触发数组开始事件
			listener.onArrayBegin();
			// 遍历数组子节点流，递归处理每个数组元素
			this.arrayStream().forEach(childNode -> childNode.read(listener));
			// 触发数组结束事件（遍历完所有数组元素后）
			listener.onArrayEnd();
		}
		// 分支3：当前节点是普通值节点（非对象、非数组）
		else {
			// 触发值事件，传递当前节点的核心值
			listener.onNodeValue(this.getValue());
		}
	}
}