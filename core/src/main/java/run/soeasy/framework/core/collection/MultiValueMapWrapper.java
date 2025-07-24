package run.soeasy.framework.core.collection;

import java.util.List;
import java.util.Map;

/**
 * 多值映射包装器接口，用于对多值映射进行统一封装和操作委托。
 * 该接口继承自MultiValueMap和MapWrapper，提供了对底层多值映射的透明包装，
 * 所有操作默认委托给被包装的源多值映射对象，支持多值映射的所有标准操作。
 *
 * <p>设计特点：
 * <ul>
 *   <li>通过包装模式实现对MultiValueMap实例的功能增强和操作代理</li>
 *   <li>所有MultiValueMap接口方法默认委派给被包装的源多值映射实现</li>
 *   <li>保持与原始多值映射完全一致的行为和语义</li>
 *   <li>类型安全的泛型设计，确保包装器与被包装类型的一致性</li>
 * </ul>
 *
 * <p>使用场景：
 * <ul>
 *   <li>需要对现有多值映射添加额外功能（如日志记录、权限控制）</li>
 *   <li>需要统一处理不同类型的多值映射实现</li>
 *   <li>需要在不修改原始实现的情况下添加自定义行为</li>
 * </ul>
 *
 * @param <K> 键的类型
 * @param <V> 值的类型
 * @param <W> 被包装的多值映射类型，必须实现MultiValueMap接口
 * @see MultiValueMap
 * @see MapWrapper
 */
@FunctionalInterface
public interface MultiValueMapWrapper<K, V, W extends MultiValueMap<K, V>>
        extends MultiValueMap<K, V>, MapWrapper<K, List<V>, W> {

    /**
     * 获取指定键的第一个值。
     * 该方法默认委派给被包装的源多值映射实现。
     *
     * @param key 要查询的键
     * @return 指定键的第一个值，若键不存在或没有值则返回null
     */
    @Override
    default V getFirst(Object key) {
        return getSource().getFirst(key);
    }

    /**
     * 为指定键添加多个值。
     * 该方法默认委派给被包装的源多值映射实现。
     *
     * @param key   要添加值的键
     * @param values 要添加的值列表
     */
    @Override
    default void adds(K key, List<V> values) {
        getSource().adds(key, values);
    }

    /**
     * 设置指定键的单一值（替换原有值）。
     * 该方法默认委派给被包装的源多值映射实现。
     *
     * @param key   要设置值的键
     * @param value 要设置的值
     */
    @Override
    default void set(K key, V value) {
        getSource().set(key, value);
    }

    /**
     * 为指定键添加一个值。
     * 该方法默认委派给被包装的源多值映射实现。
     *
     * @param key   要添加值的键
     * @param value 要添加的值
     */
    @Override
    default void add(K key, V value) {
        getSource().add(key, value);
    }

    /**
     * 设置多个键值对（替换原有值）。
     * 该方法默认委派给被包装的源多值映射实现。
     *
     * @param map 包含键值对的Map
     */
    @Override
    default void setAll(Map<? extends K, ? extends V> map) {
        getSource().setAll(map);
    }

    /**
     * 添加多个键值对（追加到现有值）。
     * 该方法默认委派给被包装的源多值映射实现。
     *
     * @param map 包含键值对列表的Map
     */
    @Override
    default void addAll(Map<? extends K, ? extends List<V>> map) {
        getSource().addAll(map);
    }

    /**
     * 将多值映射转换为单值映射（每个键只保留最后一个值）。
     * 该方法默认委派给被包装的源多值映射实现。
     *
     * @return 转换后的单值映射
     */
    @Override
    default Map<K, V> toSingleValueMap() {
        return getSource().toSingleValueMap();
    }
}