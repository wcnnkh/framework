package run.soeasy.framework.core.collection;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 基于LinkedHashMap的多值映射实现，支持一个键对应多个值。
 * 该类将每个键映射到一个LinkedList值列表，保持插入顺序，并提供了便捷的多值操作方法。
 *
 * <p>
 * 核心特性：
 * <ul>
 * <li>有序性：使用LinkedHashMap存储键值对，保持插入顺序</li>
 * <li>多值支持：每个键对应一个LinkedList值列表，允许重复值</li>
 * <li>便捷操作：提供add/removeFirst/set等多值专用方法</li>
 * <li>线程不安全：设计用于单线程环境，如HTTP请求参数处理</li>
 * </ul>
 *
 * <p>
 * 使用示例：
 * 
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
 */
public class LinkedMultiValueMap<K, V> extends LinkedHashMap<K, List<V>> implements MultiValueMap<K, V> {
	private static final long serialVersionUID = 3801124242820219131L;

	/**
	 * Constructs an empty insertion-ordered <tt>LinkedHashMap</tt> instance with
	 * the specified initial capacity and load factor.
	 *
	 * @param initialCapacity the initial capacity
	 * @param loadFactor      the load factor
	 * @throws IllegalArgumentException if the initial capacity is negative or the
	 *                                  load factor is nonpositive
	 */
	public LinkedMultiValueMap(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
	}

	/**
	 * Constructs an empty insertion-ordered <tt>LinkedHashMap</tt> instance with
	 * the specified initial capacity and a default load factor (0.75).
	 *
	 * @param initialCapacity the initial capacity
	 * @throws IllegalArgumentException if the initial capacity is negative
	 */
	public LinkedMultiValueMap(int initialCapacity) {
		super(initialCapacity);
	}

	/**
	 * Constructs an empty insertion-ordered <tt>LinkedHashMap</tt> instance with
	 * the default initial capacity (16) and load factor (0.75).
	 */
	public LinkedMultiValueMap() {
		super();
	}

	/**
	 * Constructs an insertion-ordered <tt>LinkedHashMap</tt> instance with the same
	 * mappings as the specified map. The <tt>LinkedHashMap</tt> instance is created
	 * with a default load factor (0.75) and an initial capacity sufficient to hold
	 * the mappings in the specified map.
	 *
	 * @param m the map whose mappings are to be placed in this map
	 * @throws NullPointerException if the specified map is null
	 */
	public LinkedMultiValueMap(Map<? extends K, ? extends List<V>> m) {
		super(m);
	}

	/**
	 * Constructs an empty <tt>LinkedHashMap</tt> instance with the specified
	 * initial capacity, load factor and ordering mode.
	 *
	 * @param initialCapacity the initial capacity
	 * @param loadFactor      the load factor
	 * @param accessOrder     the ordering mode - <tt>true</tt> for access-order,
	 *                        <tt>false</tt> for insertion-order
	 * @throws IllegalArgumentException if the initial capacity is negative or the
	 *                                  load factor is nonpositive
	 */
	public LinkedMultiValueMap(int initialCapacity, float loadFactor, boolean accessOrder) {
		super(initialCapacity, loadFactor, accessOrder);
	}
}