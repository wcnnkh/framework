package run.soeasy.framework.core.domain;

import java.io.Serializable;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * 键值对反转实现，用于将{@link KeyValue}的键和值进行反转，
 * 实现键值对的双向访问。该类通过包装原始键值对对象，
 * 在访问时交换键和值的角色，从而实现反转功能。
 *
 * <p>核心特性：
 * <ul>
 *   <li>键值反转：将原始键值对的键和值角色互换</li>
 *   <li>双向转换：支持通过{@link #reversed()}方法返回原始键值对</li>
 *   <li>透明代理：对原始键值对的操作会直接反映到反转对象上</li>
 *   <li>不可变性：若原始键值对是不可变的，则反转对象也是不可变的</li>
 * </ul>
 *
 * <p>使用场景：
 * <ul>
 *   <li>反向查找：当需要通过值查找键时，可反转键值对</li>
 *   <li>数据转换：在不同模块间传递数据时，根据需要调整键值顺序</li>
 *   <li>双向映射：实现双向键值映射的便捷方式</li>
 *   <li>API适配：适配需要特定键值顺序的外部API</li>
 * </ul>
 *
 * <p>示例用法：
 * <pre class="code">
 * // 原始键值对
 * KeyValue&lt;String, Integer&gt; original = KeyValue.of("age", 25);
 * 
 * // 反转键值对
 * KeyValue&lt;Integer, String&gt; reversed = new ReversedKeyValue&lt;&gt;(original);
 * 
 * // 获取反转后的键和值
 * Integer key = reversed.getKey();    // 25
 * String value = reversed.getValue(); // "age"
 * 
 * // 转回原始键值对
 * KeyValue&lt;String, Integer&gt; backToOriginal = reversed.reversed(); // 等于original
 * </pre>
 *
 * @param <V> 反转后的键类型（原始值类型）
 * @param <K> 反转后的值类型（原始键类型）
 * @param <W> 被包装的原始键值对类型
 * @see KeyValue
 */
@RequiredArgsConstructor
class ReversedKeyValue<V, K, W extends KeyValue<K, V>> implements KeyValue<V, K>, Serializable {
    private static final long serialVersionUID = 1L;
    /** 被包装的原始键值对，不可为null */
    @NonNull
    private final W reversed;

    /**
     * 获取反转后的键（即原始键值对的值）。
     *
     * @return 反转后的键
     */
    @Override
    public V getKey() {
        return reversed.getValue();
    }

    /**
     * 获取反转后的值（即原始键值对的键）。
     *
     * @return 反转后的值
     */
    @Override
    public K getValue() {
        return reversed.getKey();
    }

    /**
     * 返回原始键值对，实现反转的双向性。
     *
     * @return 原始键值对
     */
    @Override
    public KeyValue<K, V> reversed() {
        return reversed;
    }
}