package run.soeasy.framework.core.streaming;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BinaryOperator;
import java.util.function.Supplier;

import lombok.NonNull;
import run.soeasy.framework.core.domain.KeyValue;

/**
 * 流式键值映射核心接口，基于 {@link Streamable} 扩展多值映射能力，支持存储结构灵活转换与高效访问。
 * <p>
 * 核心定位：融合「流式遍历」与「键值映射」特性，提供多存储结构（Map/List）的无缝转换，
 * 同时支持按键（O(1)/O(n)）、按索引（O(1)）两种高效访问方式，适配多值/单值映射场景。
 * </p>
 * <h3>核心能力</h3>
 * <ul>
 * <li>灵活创建：支持基于单值Map、多值Map、索引List、Streamable快速创建（无数据拷贝，直接适配）；</li>
 * <li>结构转换：一键转为索引型（优化按位置访问）、单值Map型（优化按键访问）、多值Map型（多值映射）；</li>
 * <li>高效访问：按键获取值集合（{@link #getValues(K)}）、判断键存在（{@link #hasKey(K)}）， 可通过
 * {@link #isMapped()} 识别底层Map存储以优化访问复杂度（Map型O(1)，非Map型O(n)）；</li>
 * <li>数据重载：支持 {@link #reload()} 重写实现数据刷新，默认返回自身；</li>
 * <li>流式特性：继承 {@link Streamable} 所有流式操作（过滤、映射、遍历等），无额外内存开销。</li>
 * </ul>
 * <h3>设计特点</h3>
 * <ul>
 * <li>无拷贝适配：所有静态创建方法（ofMapped/ofIndexed等）均直接适配已有集合，不拷贝数据；</li>
 * <li>懒加载遍历：默认流式遍历实现，避免提前加载全量数据；</li>
 * <li>类型安全：泛型约束键值类型，支持null键匹配（仅匹配键为null的元素）；</li>
 * <li>扩展友好：默认方法提供基础实现，子类可重写核心方法（如hasKey/isMapped）优化性能。</li>
 * </ul>
 * <h3>实现类说明</h3>
 * <ul>
 * <li>{@link EmptyMapping}：空映射单例（全局唯一，无数据）；</li>
 * <li>{@link MappedMapping}：单值Map型实现（底层Map存储，按键O(1)访问）；</li>
 * <li>{@link MultiMappedMapping}：多值Map型实现（底层多值Map存储，支持一键多值）；</li>
 * <li>{@link IndexedMapping}：索引型实现（底层List存储，按位置O(1)访问）；</li>
 * <li>{@link StreamableMapping}：Streamable适配实现（桥接流式数据）。</li>
 * </ul>
 *
 * @author soeasy.run
 * @param <K> 键类型（支持null值，null键仅匹配null键元素）
 * @param <V> 值类型
 * @see Streamable
 * @see KeyValue
 * @see EmptyMapping
 * @see MappedMapping
 * @see MultiMappedMapping
 * @see IndexedMapping
 * @see StreamableMapping
 */
@FunctionalInterface
public interface Mapping<K, V> extends Streamable<KeyValue<K, V>> {
	/**
	 * 返回空的Mapping单例（全局唯一，无数据）。
	 *
	 * @param <K> 键类型
	 * @param <V> 值类型
	 * @return 空Mapping单例
	 */
	@SuppressWarnings("unchecked")
	public static <K, V> Mapping<K, V> empty() {
		return (Mapping<K, V>) EmptyMapping.INSTANCE;
	}

	/**
	 * 基于已有多值Map快速创建Mapping（无数据拷贝，直接适配）。
	 *
	 * @param multiValueMap 多值Map（键对应值集合）
	 * @param <K>           键类型
	 * @param <V>           值类型
	 * @return 多值Map型Mapping实例
	 */
	public static <K, V> Mapping<K, V> ofMultiMapped(@NonNull Map<? extends K, ? extends Collection<V>> multiValueMap) {
		MultiMappedMapping<K, V, Mapping<K, V>> mapping = new MultiMappedMapping<>(null, null, null);
		mapping.map = multiValueMap;
		return mapping;
	}

	/**
	 * 基于已有单值Map快速创建Mapping（无数据拷贝，直接适配）。
	 *
	 * @param map 单值Map（键对应单个值）
	 * @param <K> 键类型
	 * @param <V> 值类型
	 * @return 单值Map型Mapping实例
	 */
	public static <K, V> Mapping<K, V> ofMapped(@NonNull Map<? extends K, ? extends V> map) {
		MappedMapping<K, V, Mapping<K, V>> mapping = new MappedMapping<>(null, null, null);
		mapping.map = map;
		return mapping;
	}

	/**
	 * 基于已有索引List快速创建Mapping（无数据拷贝，直接适配）。
	 *
	 * @param list 索引化的键值对列表
	 * @param <K>  键类型
	 * @param <V>  值类型
	 * @return 索引型Mapping实例
	 */
	public static <K, V> Mapping<K, V> ofIndexed(@NonNull List<KeyValue<K, V>> list) {
		IndexedMapping<K, V, Mapping<K, V>> mapping = new IndexedMapping<>(null, null);
		mapping.collection = list;
		return mapping;
	}

	/**
	 * 将Streamable<KeyValue<K,V>>适配为Mapping（接口桥接，无数据拷贝）。
	 *
	 * @param streamable Streamable类型的键值对集合
	 * @param <K>        键类型
	 * @param <V>        值类型
	 * @return Mapping实例
	 */
	public static <K, V> Mapping<K, V> forStreamable(@NonNull Streamable<KeyValue<K, V>> streamable) {
		return new StreamableMapping<>(streamable);
	}

	/**
	 * 根据键获取对应的值集合，支持多值映射。
	 * <p>
	 * 键为null时仅匹配键为null的元素，无匹配时返回空Streamable。
	 * </p>
	 *
	 * @param key 查找的键（允许为null）
	 * @return 匹配的值集合（非null，无匹配时返回空Streamable）
	 */
	default Streamable<V> getValues(K key) {
		return filter(e -> Objects.equals(key, e.getKey())).map(KeyValue::getValue);
	}

	/**
	 * 重载数据（默认返回自身，实现类可重写以支持数据刷新）。
	 *
	 * @return 当前Mapping实例
	 */
	@Override
	default Mapping<K, V> reload() {
		return this;
	}

	/**
	 * 判断是否存在指定键。
	 * <p>
	 * 默认流式遍历实现（O(n)），大数据量场景下，Map型实现类可重写为O(1)复杂度。
	 * </p>
	 *
	 * @param key 待检查的键（允许为null）
	 * @return 存在返回true，否则返回false
	 */
	default boolean hasKey(K key) {
		return keys().contains(key);
	}

	/**
	 * 获取所有键的集合。
	 *
	 * @return 键的Streamable集合（非null）
	 */
	default Streamable<K> keys() {
		return map(KeyValue::getKey);
	}

	// ------------------------------ 索引型转换 ------------------------------
	/**
	 * 转为索引型Mapping（默认基于ArrayList，优化按位置访问性能）。
	 *
	 * @return 索引型Mapping实例
	 */
	default Mapping<K, V> toIndexed() {
		return toIndexed(ArrayList::new);
	}

	/**
	 * 转为索引型Mapping（自定义List工厂）。
	 *
	 * @param collectionFactory List工厂（如LinkedList::new）
	 * @return 索引型Mapping实例
	 */
	default Mapping<K, V> toIndexed(@NonNull Supplier<? extends List<KeyValue<K, V>>> collectionFactory) {
		return new IndexedMapping<>(this, collectionFactory);
	}

	// ------------------------------ 单值Map型转换 ------------------------------
	/**
	 * 转为单值Map型Mapping（默认LinkedHashMap，重复key抛异常）。
	 *
	 * @return 单值Map型Mapping实例
	 */
	default Mapping<K, V> toMapped() {
		return toMapped((a, b) -> {
			throw new IllegalStateException(String.format("Duplicate key (conflicting values: %s vs %s)", a, b));
		});
	}

	/**
	 * 转为单值Map型Mapping（自定义重复key合并策略，默认LinkedHashMap）。
	 *
	 * @param mergeFunction 重复key合并策略
	 * @return 单值Map型Mapping实例
	 */
	default Mapping<K, V> toMapped(@NonNull BinaryOperator<V> mergeFunction) {
		return toMapped(mergeFunction, LinkedHashMap::new);
	}

	/**
	 * 转为单值Map型Mapping（自定义重复key合并策略+Map工厂）。
	 *
	 * @param mergeFunction 重复key合并策略
	 * @param mapFactory    Map工厂（如HashMap::new）
	 * @return 单值Map型Mapping实例
	 */
	default Mapping<K, V> toMapped(@NonNull BinaryOperator<V> mergeFunction, @NonNull Supplier<Map<K, V>> mapFactory) {
		return new MappedMapping<>(this, mergeFunction, mapFactory);
	}

	// ------------------------------ 多值Map型转换 ------------------------------
	/**
	 * 转为多值Map型Mapping（默认LinkedHashMap+ArrayList）。
	 *
	 * @return 多值Map型Mapping实例
	 */
	default Mapping<K, V> toMultiMapped() {
		return toMultiMapped(LinkedHashMap::new);
	}

	/**
	 * 转为多值Map型Mapping（自定义Map工厂，默认ArrayList存储值）。
	 *
	 * @param mapFactory Map工厂（如ConcurrentHashMap::new）
	 * @return 多值Map型Mapping实例
	 */
	default Mapping<K, V> toMultiMapped(@NonNull Supplier<Map<K, Collection<V>>> mapFactory) {
		return toMultiMapped(mapFactory, ArrayList::new);
	}

	/**
	 * 转为多值Map型Mapping（自定义Map工厂+值集合工厂）。
	 *
	 * @param mapFactory        Map工厂
	 * @param collectionFactory 值集合工厂（如HashSet::new）
	 * @return 多值Map型Mapping实例
	 */
	default Mapping<K, V> toMultiMapped(@NonNull Supplier<Map<K, Collection<V>>> mapFactory,
			@NonNull Supplier<Collection<V>> collectionFactory) {
		return new MultiMappedMapping<>(this, mapFactory, collectionFactory);
	}

	/**
	 * 判断底层是否为Map型存储（单值/多值），支持O(1)复杂度的按键操作。
	 * <p>
	 * 语义说明：
	 * <ul>
	 * <li>true：底层基于Map存储（如{@link MappedMapping}/{@link MultiMappedMapping}），
	 * {@link #getValues(K)}、{@link #hasKey(K)}等按键操作为O(1)复杂度；</li>
	 * <li>false：底层非Map存储（如{@link IndexedMapping}/{@link StreamableMapping}），
	 * 按键操作为O(n)的流式遍历复杂度。</li>
	 * </ul>
	 * <p>
	 * 默认返回false，Map型实现类需重写为true。
	 *
	 * @return 底层是Map型存储返回true，否则false
	 */
	default boolean isMapped() {
		return false;
	}
}