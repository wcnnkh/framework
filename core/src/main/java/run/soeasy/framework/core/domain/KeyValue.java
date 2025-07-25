package run.soeasy.framework.core.domain;

import java.util.Map.Entry;

import lombok.NonNull;

/**
 * 键值对接口，用于表示具有键和值的关联关系，支持键值对的反转操作，
 * 并提供静态工厂方法以简化键值对的创建和包装。该接口提供了键值对的基本操作，
 * 适用于各种需要表示二元关联关系的场景。
 *
 * <p>核心特性：
 * <ul>
 *   <li>键值访问：提供获取键和值的方法</li>
 *   <li>反转操作：通过{@link #reversed()}支持键值对的反转</li>
 *   <li>灵活创建：通过静态工厂方法{@link #of(Object, Object)}创建实例</li>
 *   <li>兼容性：通过{@link #wrap(Entry)}支持包装标准Map.Entry</li>
 * </ul>
 *
 * <p>使用场景：
 * <ul>
 *   <li>配置项表示（键=值）</li>
 *   <li>数据映射和转换</li>
 *   <li>函数式编程中的二元组</li>
 *   <li>需要反转键值关系的场景</li>
 * </ul>
 *
 * <p>示例用法：
 * <pre class="code">
 * // 创建键值对
 * KeyValue&lt;String, Integer&gt; pair = KeyValue.of("age", 25);
 * 
 * // 获取键和值
 * String key = pair.getKey();    // "age"
 * Integer value = pair.getValue(); // 25
 * 
 * // 反转键值对
 * KeyValue&lt;Integer, String&gt; reversed = pair.reversed(); // (25, "age")
 * 
 * // 包装Map.Entry
 * Map.Entry&lt;String, Integer&gt; entry = new AbstractMap.SimpleEntry&lt;&gt;("score", 90);
 * KeyValue&lt;String, Integer&gt; wrapped = KeyValue.wrap(entry);
 * </pre>
 *
 * @param <K> 键的类型
 * @param <V> 值的类型
 * @see Entry
 */
public interface KeyValue<K, V> {

    /**
     * 获取键值对的键。
     *
     * @return 键，可能为null（取决于具体实现）
     */
    K getKey();

    /**
     * 获取键值对的值。
     *
     * @return 值，可能为null（取决于具体实现）
     */
    V getValue();

    /**
     * 创建当前键值对的反转版本，即键变为值，值变为键。
     * <p>
     * 反转后的键值对类型为{@code KeyValue<V, K>}。
     * 该操作不会修改原始键值对，而是返回一个新的实例。
     *
     * @return 反转后的键值对
     */
    default KeyValue<V, K> reversed() {
        return new ReversedKeyValue<>(this);
    }

    /**
     * 创建一个包含指定键和值的键值对实例。
     * <p>
     * 该方法返回一个不可变的键值对实现，支持键和值为null。
     *
     * @param <K>   键的类型
     * @param <V>   值的类型
     * @param key   键，可为null
     * @param value 值，可为null
     * @return 包含指定键值的键值对实例
     */
    public static <K, V> KeyValue<K, V> of(K key, V value) {
        return new CustomizeKeyValue<>(key, value);
    }

    /**
     * 将标准的{@link Entry}包装为{@link KeyValue}接口实例。
     * <p>
     * 该方法提供了与Java标准库的兼容性，允许将Map中的Entry转换为KeyValue接口。
     * 包装后的实例会直接代理{@link Entry}的getKey()和getValue()方法。
     *
     * @param <K>   键的类型
     * @param <V>   值的类型
     * @param entry 要包装的Entry，不可为null
     * @return 包装后的KeyValue实例
     * @throws NullPointerException 如果entry为null
     */
    public static <K, V> KeyValue<K, V> wrap(@NonNull Entry<K, V> entry) {
        return (EntryWrapper<K, V, Entry<K, V>>) () -> entry;
    }
}