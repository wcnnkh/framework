package run.soeasy.framework.core.collection;

/**
 * 键值对集合接口，用于表示包含多个键值对的集合结构。
 * 该接口继承自Keys接口，在键集合操作的基础上增加了值集合的访问能力，
 * 支持通过键获取对应的多个值的集合视图。
 *
 * <p>设计特点：
 * <ul>
 *   <li>继承Keys接口，同时提供键集合和值集合的访问能力</li>
 *   <li>通过getValues方法支持一个键对应多个值的集合语义</li>
 *   <li>返回Elements类型的值集合，支持流式操作和集合转换</li>
 * </ul>
 *
 * <p>使用场景：
 * <ul>
 *   <li>需要处理多值映射（如HTTP请求参数、数据库结果集）的场景</li>
 *   <li>需要统一操作键集合和值集合的场景</li>
 *   <li>需要将键值对转换为不同视图的场景</li>
 * </ul>
 *
 * @author soeasy.run
 * @param <K> 键的类型
 * @param <V> 值的类型
 * @see Keys
 * @see Elements
 */
public interface KeyValues<K, V> extends Keys<K> {

    /**
     * 获取指定键对应的所有值的集合。
     * 该方法返回的Elements集合可能包含多个值，体现多值映射的语义。
     *
     * @param key 要查询的键
     * @return 包含该键对应所有值的Elements集合，若键不存在则返回空集合
     */
    Elements<V> getValues(K key);
}