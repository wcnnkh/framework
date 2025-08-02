package run.soeasy.framework.core.domain;

import java.io.Serializable;
import java.util.Map.Entry;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 自定义键值对实现，同时实现了{@link KeyValue}和{@link Entry}接口，
 * 提供了一个简单的、可序列化的键值对实现。该类使用Lombok注解简化代码，
 * 支持通过构造函数初始化或默认构造后通过setter方法设置键值。
 *
 * <p>核心特性：
 * <ul>
 *   <li>双向兼容：同时实现{@link KeyValue}和{@link Entry}接口，无缝集成两种体系</li>
 *   <li>可变性：提供setValue方法允许修改值，支持动态更新键值对</li>
 *   <li>可序列化：实现{@link Serializable}接口，支持在分布式环境中传输</li>
 *   <li>空安全：默认构造函数允许创建空键值对，运行时可设置非空值</li>
 * </ul>
 *
 * <p>使用场景：
 * <ul>
 *   <li>数据传输对象：在组件间传递键值对数据</li>
 *   <li>Map替代方案：当需要简单的键值对集合时，无需依赖完整的Map实现</li>
 *   <li>序列化场景：需要在网络或持久化存储中传递键值对</li>
 *   <li>框架集成：作为框架内部的标准键值对实现</li>
 * </ul>
 *
 * <p>示例用法：
 * <pre class="code">
 * // 使用构造函数创建键值对
 * CustomizeKeyValue&lt;String, Integer&gt; pair = new CustomizeKeyValue&lt;&gt;("age", 25);
 * 
 * // 使用默认构造函数后设置值
 * CustomizeKeyValue&lt;String, String&gt; config = new CustomizeKeyValue&lt;&gt;();
 * config.setKey("timeout");
 * config.setValue("3000");
 * 
 * // 作为Entry使用
 * Map&lt;String, Integer&gt; map = new HashMap&lt;&gt;();
 * map.put(pair.getKey(), pair.getValue());
 * 
 * // 作为KeyValue使用
 * KeyValue&lt;String, Integer&gt; kv = pair;
 * System.out.println(kv.getValue()); // 输出: 25
 * </pre>
 *
 * @param <K> 键的类型
 * @param <V> 值的类型
 * @see KeyValue
 * @see Entry
 * @see Serializable
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomizeKeyValue<K, V> implements KeyValue<K, V>, Entry<K, V>, Serializable {
    private static final long serialVersionUID = 1L;
    private K key;
    private V value;

    /**
     * 设置键值对的值，并返回旧值。
     * <p>
     * 此方法实现了{@link Entry}接口的要求，允许修改键值对的值。
     * 注意：修改操作会直接影响当前对象的状态。
     *
     * @param value 新值
     * @return 被替换的旧值
     */
    @Override
    public V setValue(V value) {
        V old = this.value;
        this.value = value;
        return old;
    }
}