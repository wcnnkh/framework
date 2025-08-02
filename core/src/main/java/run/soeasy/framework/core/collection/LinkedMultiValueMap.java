package run.soeasy.framework.core.collection;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;

import lombok.NonNull;

/**
 * 基于LinkedHashMap的多值映射实现，支持一个键对应多个值。
 * 该类将每个键映射到一个LinkedList值列表，保持插入顺序，并提供了便捷的多值操作方法。
 *
 * <p>核心特性：
 * <ul>
 *   <li>有序性：使用LinkedHashMap存储键值对，保持插入顺序</li>
 *   <li>多值支持：每个键对应一个LinkedList值列表，允许重复值</li>
 *   <li>便捷操作：提供add/removeFirst/set等多值专用方法</li>
 *   <li>线程不安全：设计用于单线程环境，如HTTP请求参数处理</li>
 * </ul>
 *
 * <p>使用示例：
 * <pre>{@code
 * LinkedMultiValueMap<String, String> map = new LinkedMultiValueMap<>();
 * map.add("key", "value1");
 * map.add("key", "value2"); // 同一个键添加多个值
 *
 * List<String> values = map.get("key"); // 返回包含"value1"和"value2"的列表
 * String firstValue = map.getFirst("key"); // 返回"value1"
 * }</pre>
 *
 * @param <K> 键的类型
 * @param <V> 值的类型
 * @see MultiValueMap
 * @see DefaultMultiValueMap
 */
public class LinkedMultiValueMap<K, V> extends DefaultMultiValueMap<K, V, LinkedHashMap<K, List<V>>>
        implements Serializable {
    private static final long serialVersionUID = 3801124242820219131L;

    /**
     * 创建空的LinkedMultiValueMap实例，使用默认初始容量(16)和负载因子(0.75)。
     */
    public LinkedMultiValueMap() {
        this(new LinkedHashMap<>());
    }

    /**
     * 创建指定初始容量的LinkedMultiValueMap实例。
     *
     * @param initialCapacity 初始容量
     */
    public LinkedMultiValueMap(int initialCapacity) {
        this(new LinkedHashMap<>(initialCapacity));
    }

    /**
     * 创建指定初始容量和负载因子的LinkedMultiValueMap实例。
     *
     * @param initialCapacity 初始容量
     * @param loadFactor      负载因子
     */
    public LinkedMultiValueMap(int initialCapacity, float loadFactor) {
        this(new LinkedHashMap<>(initialCapacity, loadFactor));
    }

    /**
     * 创建指定初始容量、负载因子和访问顺序的LinkedMultiValueMap实例。
     *
     * @param initialCapacity 初始容量
     * @param loadFactor      负载因子
     * @param accessOrder     访问顺序(true表示按访问顺序，false表示按插入顺序)
     */
    public LinkedMultiValueMap(int initialCapacity, float loadFactor, boolean accessOrder) {
        this(new LinkedHashMap<>(initialCapacity, loadFactor, accessOrder));
    }

    /**
     * 基于现有LinkedHashMap创建LinkedMultiValueMap实例，共享底层数据结构。
     *
     * @param source 源LinkedHashMap，不可为null
     */
    public LinkedMultiValueMap(@NonNull LinkedHashMap<K, List<V>> source) {
        super(source);
    }
}