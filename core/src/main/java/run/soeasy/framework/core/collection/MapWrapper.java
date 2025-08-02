package run.soeasy.framework.core.collection;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

import run.soeasy.framework.core.domain.Wrapper;

/**
 * MapWrapper接口提供了对Map的包装功能，允许对Map进行封装并操作原始Map对象。
 * 实现此接口的类可以将Map对象包装起来，提供额外的功能或修改默认行为。
 *
 * @param <K> 键的类型
 * @param <V> 值的类型
 * @param <W> 被包装的Map类型
 * 
 * @author soeasy.run
 */
@FunctionalInterface
public interface MapWrapper<K, V, W extends Map<K, V>> extends Map<K, V>, Wrapper<W> {
    
    /**
     * 清除此映射中的所有映射关系。
     * 此调用返回后，映射将为空。
     */
    @Override
    default void clear() {
        getSource().clear();
    }

    /**
     * 如果此映射包含指定键的映射关系，则返回true。
     *
     * @param key 要测试其是否存在于此映射中的键
     * @return 如果此映射包含指定键的映射关系，则返回true
     */
    @Override
    default boolean containsKey(Object key) {
        return getSource().containsKey(key);
    }

    /**
     * 如果此映射将一个或多个键映射到指定值，则返回true。
     *
     * @param value 要测试其是否存在于此映射中的值
     * @return 如果此映射将一个或多个键映射到指定值，则返回true
     */
    @Override
    default boolean containsValue(Object value) {
        return getSource().containsValue(value);
    }

    /**
     * 返回此映射中包含的映射关系的Set视图。
     *
     * @return 此映射中包含的映射关系的Set视图
     */
    @Override
    default Set<Entry<K, V>> entrySet() {
        return getSource().entrySet();
    }

    /**
     * 返回此映射中指定键所映射的值；如果此映射不包含该键的映射关系，则返回null。
     *
     * @param key 要返回其关联值的键
     * @return 此映射中指定键所映射的值，或null（如果映射不包含该键的映射关系）
     */
    @Override
    default V get(Object key) {
        return getSource().get(key);
    }

    /**
     * 如果此映射不包含键-值映射关系，则返回true。
     *
     * @return 如果此映射不包含键-值映射关系，则返回true
     */
    @Override
    default boolean isEmpty() {
        return getSource().isEmpty();
    }

    /**
     * 返回此映射中包含的键的Set视图。
     *
     * @return 此映射中包含的键的Set视图
     */
    @Override
    default Set<K> keySet() {
        return getSource().keySet();
    }

    /**
     * 将指定的值与此映射中的指定键关联。
     *
     * @param key 与指定值关联的键
     * @param value 要与指定键关联的值
     * @return 此映射中指定键的前一个值，或null（如果没有该键的映射关系）
     */
    @Override
    default V put(K key, V value) {
        return getSource().put(key, value);
    }

    /**
     * 将指定映射中的所有映射关系复制到此映射。
     *
     * @param m 其映射关系将被存储在此映射中的映射
     */
    @Override
    default void putAll(Map<? extends K, ? extends V> m) {
        getSource().putAll(m);
    }

    /**
     * 如果存在此键的映射关系，则将其从映射中移除。
     *
     * @param key 要从映射中移除其映射关系的键
     * @return 与key关联的前一个值，或null（如果没有该键的映射关系）
     */
    @Override
    default V remove(Object key) {
        return getSource().remove(key);
    }

    /**
     * 返回此映射中的键-值映射关系数。
     *
     * @return 此映射中的键-值映射关系数
     */
    @Override
    default int size() {
        return getSource().size();
    }

    /**
     * 返回此映射中包含的值的Collection视图。
     *
     * @return 此映射中包含的值的Collection视图
     */
    @Override
    default Collection<V> values() {
        return getSource().values();
    }

    /**
     * 尝试计算指定键及其当前映射值的映射关系（如果没有当前映射关系，则为null）。
     *
     * @param key 与指定值关联的键
     * @param remappingFunction 计算值的函数
     * @return 与指定键关联的新值，如果结果为null，则为null
     */
    @Override
    default V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        return getSource().compute(key, remappingFunction);
    }

    /**
     * 如果指定的键尚未与值关联（或映射到null），则尝试使用给定的映射函数计算其值，并将其输入到此映射中，除非为null。
     *
     * @param key 与指定值关联的键
     * @param mappingFunction 计算值的函数
     * @return 与指定键关联的当前（现有或计算）值，如果计算值为null，则为null
     */
    @Override
    default V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
        return getSource().computeIfAbsent(key, mappingFunction);
    }

    /**
     * 如果指定的键的值存在且非null，则尝试计算给定键及其当前映射值的新映射关系。
     *
     * @param key 与指定值关联的键
     * @param remappingFunction 计算值的函数
     * @return 与指定键关联的新值，如果结果为null，则为null
     */
    @Override
    default V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        return getSource().computeIfPresent(key, remappingFunction);
    }

    /**
     * 对此映射中的每个条目执行给定的操作，直到所有条目都被处理或操作抛出异常。
     *
     * @param action 要对每个条目执行的操作
     */
    @Override
    default void forEach(BiConsumer<? super K, ? super V> action) {
        getSource().forEach(action);
    }

    /**
     * 返回此映射中指定键所映射的值，或defaultValue（如果此映射不包含该键的映射关系）。
     *
     * @param key 要返回其关联值的键
     * @param defaultValue 如果此映射不包含该键的映射关系时返回的默认值
     * @return 此映射中指定键所映射的值，或defaultValue（如果此映射不包含该键的映射关系）
     */
    @Override
    default V getOrDefault(Object key, V defaultValue) {
        return getSource().getOrDefault(key, defaultValue);
    }

    /**
     * 如果指定的键尚未与值关联或与null关联，则将其与给定的非null值关联。
     * 否则，将关联值替换为给定重映射函数的结果，如果结果为null，则移除该映射关系。
     *
     * @param key 与指定值关联的键
     * @param value 如果不存在映射关系，则与指定键关联的值
     * @param remappingFunction 重新映射值的函数，如果存在现有值
     * @return 与指定键关联的新值，如果没有值则为null
     */
    @Override
    default V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        return getSource().merge(key, value, remappingFunction);
    }

    /**
     * 如果指定的键尚未与值关联（或映射到null），则将其与给定值关联并返回null，否则返回当前值。
     *
     * @param key 与指定值关联的键
     * @param value 如果不存在映射关系，则与指定键关联的值
     * @return 与指定键关联的先前值，如果没有该键的映射关系，则为null
     */
    @Override
    default V putIfAbsent(K key, V value) {
        return getSource().putIfAbsent(key, value);
    }

    /**
     * 仅当指定键当前映射到指定值时，才移除该键的条目。
     *
     * @param key 与指定值关联的键
     * @param value 预期与指定键关联的值
     * @return 如果值被移除，则返回true
     */
    @Override
    default boolean remove(Object key, Object value) {
        return getSource().remove(key, value);
    }

    /**
     * 仅当指定键当前映射到指定值时，才替换该键的条目。
     *
     * @param key 与指定值关联的键
     * @param oldValue 预期与指定键关联的值
     * @param newValue 要与指定键关联的值
     * @return 如果值被替换，则返回true
     */
    @Override
    default boolean replace(K key, V oldValue, V newValue) {
        return getSource().replace(key, oldValue, newValue);
    }

    /**
     * 仅当指定键当前映射到某个值时，才替换该键的条目。
     *
     * @param key 与指定值关联的键
     * @param value 要与指定键关联的值
     * @return 与指定键关联的先前值，如果没有该键的映射关系，则为null
     */
    @Override
    default V replace(K key, V value) {
        return getSource().replace(key, value);
    }

    /**
     * 对每个条目执行给定的操作，直到所有条目都被处理或操作抛出异常。
     *
     * @param function 要对每个条目执行的操作
     */
    @Override
    default void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
        getSource().replaceAll(function);
    }
}