package run.soeasy.framework.core.collection;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Map接口的扩展，用于存储多值映射关系。
 * 与普通Map不同，MultiValueMap中的每个键可以对应多个值。
 * 
 * @author soeasy.run
 * @param <K> 键的类型
 * @param <V> 值的类型
 */
public interface MultiValueMap<K, V> extends Map<K, List<V>> {
    /**
     * 获取指定键的第一个值。
     * 
     * @param key 键对象
     * @return 键对应的第一个值，如果键不存在或值列表为空则返回null
     */
    default V getFirst(Object key) {
        List<V> values = get(key);
        if (values == null || values.isEmpty()) {
            return null;
        }
        return values.get(0);
    }

    /**
     * 向指定键的值列表添加单个值。
     * 如果键不存在，会创建一个新的列表并添加该值。
     * 
     * @param key 键对象
     * @param value 要添加的值
     */
    default void add(K key, V value) {
        adds(key, Arrays.asList(value));
    }

    /**
     * 向指定键的值列表添加多个值。
     * 如果键不存在，会创建一个新的列表并添加这些值。
     * 
     * @param key 键对象
     * @param values 要添加的值列表
     */
    void adds(K key, List<V> values);

    /**
     * 设置指定键的单个值。
     * 此操作会覆盖键原有的所有值，只保留新设置的值。
     * 
     * @param key 键对象
     * @param value 要设置的值
     */
    void set(K key, V value);

    /**
     * 将Map中的所有键值对设置到当前MultiValueMap中。
     * 每个键对应的值会覆盖当前MultiValueMap中相同键的所有现有值。
     * 
     * @param map 包含键值对的Map
     */
    default void setAll(Map<? extends K, ? extends V> map) {
        for (Entry<? extends K, ? extends V> entry : map.entrySet()) {
            set(entry.getKey(), entry.getValue());
        }
    }

    /**
     * 将另一个MultiValueMap中的所有键值对添加到当前MultiValueMap中。
     * 对于相同的键，会将值列表合并而不是覆盖。
     * 
     * @param map 要添加的MultiValueMap
     */
    default void addAll(Map<? extends K, ? extends List<V>> map) {
        for (Entry<? extends K, ? extends List<V>> entry : map.entrySet()) {
            adds(entry.getKey(), entry.getValue());
        }
    }

    /**
     * 将MultiValueMap转换为单值Map。
     * 每个键对应的值列表中的第一个值会被提取出来作为单值Map中的值。
     * 
     * @return 包含单值映射关系的Map
     */
    default Map<K, V> toSingleValueMap() {
        if (isEmpty()) {
            return Collections.emptyMap();
        }

        Map<K, V> singleValueMap = new LinkedHashMap<K, V>(size());
        for (java.util.Map.Entry<K, List<V>> entry : entrySet()) {
            List<V> values = entry.getValue();
            if (CollectionUtils.isEmpty(values)) {
                continue;
            }

            singleValueMap.put(entry.getKey(), values.get(0));
        }
        return singleValueMap;
    }

}