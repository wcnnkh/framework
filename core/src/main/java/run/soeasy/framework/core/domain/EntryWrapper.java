package run.soeasy.framework.core.domain;

import java.util.Map.Entry;

/**
 * 映射条目包装器接口，用于将标准的{@link Entry}包装为同时实现{@link KeyValue}接口的对象，
 * 实现装饰器模式以支持对映射条目的透明增强。该接口继承自{@link Entry}、{@link KeyValue}和{@link Wrapper}，
 * 允许在不修改原始条目的前提下添加额外功能。
 *
 * <p>核心特性：
 * <ul>
 *   <li>接口融合：同时实现{@link Entry}和{@link KeyValue}接口，统一键值对表示</li>
 *   <li>透明委托：所有方法调用均转发给被包装的{@link Entry}实例</li>
 *   <li>装饰扩展：支持通过包装器添加日志记录、验证、缓存等额外功能</li>
 *   <li>双向兼容：既可以作为标准Entry使用，也可以作为KeyValue使用</li>
 * </ul>
 *
 * <p>使用场景：
 * <ul>
 *   <li>Map操作适配：将标准Map.Entry转换为KeyValue接口</li>
 *   <li>条目访问增强：添加日志记录、权限验证等功能</li>
 *   <li>数据转换：在不修改原始条目的情况下转换键或值</li>
 *   <li>框架集成：使标准Map与框架中的KeyValue体系无缝对接</li>
 * </ul>
 *
 * <p>示例用法：
 * <pre class="code">
 * // 原始Map.Entry
 * Map.Entry<String, Integer> originalEntry = new AbstractMap.SimpleEntry<>("age", 25);
 * 
 * // 包装为KeyValue接口
 * EntryWrapper<String, Integer, Map.Entry<String, Integer>> wrapped = () -> originalEntry;
 * 
 * // 作为KeyValue使用
 * KeyValue<String, Integer> keyValue = wrapped;
 * String key = keyValue.getKey();    // "age"
 * Integer value = keyValue.getValue(); // 25
 * 
 * // 作为Entry使用
 * Map.Entry<String, Integer> entry = wrapped;
 * entry.setValue(30); // 修改原始条目值
 * </pre>
 *
 * @param <K> 键的类型
 * @param <V> 值的类型
 * @param <W> 被包装的条目类型，必须是{@link Entry}的子类型
 * @see Entry
 * @see KeyValue
 * @see Wrapper
 */
@FunctionalInterface
public interface EntryWrapper<K, V, W extends Entry<K, V>> extends Entry<K, V>, KeyValue<K, V>, Wrapper<W> {
    
    /**
     * 获取键值对的键，转发给被包装的Entry实例。
     *
     * @return 被包装条目的键
     * @see Entry#getKey()
     * @see KeyValue#getKey()
     */
    @Override
    default K getKey() {
        return getSource().getKey();
    }
    
    /**
     * 获取键值对的值，转发给被包装的Entry实例。
     *
     * @return 被包装条目的值
     * @see Entry#getValue()
     * @see KeyValue#getValue()
     */
    @Override
    default V getValue() {
        return getSource().getValue();
    }
    
    /**
     * 设置键值对的值，转发给被包装的Entry实例。
     * <p>
     * 注意：此操作会修改原始条目值。若需要不可变版本，请使用只读的KeyValue接口。
     *
     * @param value 新值
     * @return 被替换的旧值
     * @see Entry#setValue(Object)
     */
    @Override
    default V setValue(V value) {
        return getSource().setValue(value);
    }
}