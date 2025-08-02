package run.soeasy.framework.core.collection;

import run.soeasy.framework.core.domain.KeyValue;

/**
 * 字典包装器接口，用于对字典类型集合进行统一封装和操作。
 * 该接口继承了Dictionary、KeyValuesWrapper和ListableWrapper接口，
 * 提供了对字典元素的键值操作、集合包装和列表化能力。
 *
 * <p>设计特点：
 * <ul>
 *   <li>通过包装模式实现对底层字典的透明操作</li>
 *   <li>提供统一的键值对操作接口，屏蔽底层实现差异</li>
 *   <li>支持字典与列表视图的灵活转换</li>
 *   <li>所有方法默认委托给被包装的源字典实现</li>
 * </ul>
 *
 * <p>使用场景：
 * <ul>
 *   <li>需要统一处理不同类型的字典实现时</li>
 *   <li>需要在字典操作中添加额外功能（如缓存、监控）时</li>
 *   <li>需要在字典与列表视图之间灵活转换时</li>
 * </ul>
 *
 * @param <K> 键的类型
 * @param <V> 值的类型
 * @param <E> 键值对元素的类型，必须实现KeyValue接口
 * @param <W> 被包装的字典类型，必须实现Dictionary接口
 * @see Dictionary
 * @see KeyValuesWrapper
 * @see ListableWrapper
 * @see KeyValue
 */
public interface DictionaryWrapper<K, V, E extends KeyValue<K, V>, W extends Dictionary<K, V, E>>
        extends Dictionary<K, V, E>, KeyValuesWrapper<K, V, W>, ListableWrapper<E, W> {

    /**
     * 获取字典中所有键的集合。
     * 该方法默认委托给被包装的源字典实现。
     *
     * @return 包含所有键的元素集合
     */
    @Override
    default Elements<K> keys() {
        return getSource().keys();
    }

    /**
     * 获取指定键对应的所有值。
     * 该方法默认委托给被包装的源字典实现。
     *
     * @param key 键
     * @return 包含该键对应所有值的元素集合
     */
    @Override
    default Elements<V> getValues(K key) {
        return getSource().getValues(key);
    }

    /**
     * 获取指定索引位置的键值对元素。
     * 该方法默认委托给被包装的源字典实现。
     *
     * @param index 索引位置
     * @return 指定位置的键值对元素
     * @throws IndexOutOfBoundsException 如果索引超出范围
     */
    @Override
    default E getElement(int index) {
        return getSource().getElement(index);
    }

    /**
     * 判断字典是否以Map形式组织。
     * 该方法默认委托给被包装的源字典实现。
     *
     * @return 如果是Map形式返回true，否则返回false
     */
    @Override
    default boolean isMap() {
        return getSource().isMap();
    }

    /**
     * 判断字典是否以数组形式组织。
     * 该方法默认委托给被包装的源字典实现。
     *
     * @return 如果是数组形式返回true，否则返回false
     */
    @Override
    default boolean isArray() {
        return getSource().isArray();
    }

    /**
     * 将字典转换为Map形式。
     * 该方法默认委托给被包装的源字典实现。
     *
     * @param uniqueness 是否要求键的唯一性
     * @return 转换后的字典实例
     */
    @Override
    default Dictionary<K, V, E> asMap(boolean uniqueness) {
        return getSource().asMap(uniqueness);
    }

    /**
     * 将字典转换为数组形式。
     * 该方法默认委托给被包装的源字典实现。
     *
     * @param uniqueness 是否要求键的唯一性
     * @return 转换后的字典实例
     */
    @Override
    default Dictionary<K, V, E> asArray(boolean uniqueness) {
        return getSource().asArray(uniqueness);
    }

    /**
     * 返回字典中键值对的数量。
     * 该方法默认委托给被包装的源字典实现。
     *
     * @return 键值对的数量
     */
    @Override
    default int size() {
        return getSource().size();
    }
}