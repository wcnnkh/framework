package run.soeasy.framework.core.streaming.function;

import java.util.Collections;
import java.util.Map;

import javax.lang.model.util.Elements;

import run.soeasy.framework.core.collection.CollectionUtils;
import run.soeasy.framework.core.streaming.Streamable;

/**
 * Map合并器，用于将多个Map实例合并为一个Map。 该合并器会按顺序处理输入的Map集合，将每个Map中的键值对依次添加到结果Map中。
 * 如果存在相同的键，后出现的Map中的值会覆盖先出现的Map中的值。
 *
 * @author soeasy.run
 * @param <K> 键类型
 * @param <V> 值类型
 * @see Merger
 * @see Elements
 */
public class MapMerger<K, V> implements Merger<Map<K, V>> {

	/**
	 * 单例实例，用于全局共享的Map合并器。 该实例为无泛型参数的原始类型，可通过类型转换安全地用于任何键值类型。
	 */
	static final MapMerger<?, ?> INSTANCE = new MapMerger<>();

	/**
	 * 将多个Map合并为一个Map。 该方法执行以下操作： 1. 遍历输入的Map集合，跳过null或空的Map 2.
	 * 使用第一个非空Map创建结果Map，以保留其实现类型特性 3. 按顺序将后续Map中的所有键值对添加到结果Map中（重复键会被覆盖） 4.
	 * 如果没有找到非空Map，返回空Map
	 *
	 * @param elements 待合并的Map集合
	 * @return 合并后的Map，如果输入为空则返回空Map
	 */
	@Override
	public Map<K, V> select(Streamable<Map<K, V>> elements) {
		Map<K, V> target = null;
		for (Map<K, V> map : elements.toCollection()) {
			if (map == null || map.isEmpty()) {
				continue;
			}

			if (target == null) {
				target = CollectionUtils.createApproximateMap(map);
			}

			target.putAll(map);
		}
		return target == null ? Collections.emptyMap() : target;
	}
}