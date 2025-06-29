package run.soeasy.framework.core.collection;

import run.soeasy.framework.core.ObjectUtils;
import run.soeasy.framework.core.domain.KeyValue;

/**
 * 字典接口<br>
 * 定义键值对集合的操作规范，继承自KeyValues和Listable
 * 
 * @author soeasy.run
 * @param <K> 键的类型
 * @param <V> 值的类型
 * @param <E> 键值对元素类型，继承自KeyValue&lt;K, V&gt;
 */
@FunctionalInterface
public interface Dictionary<K, V, E extends KeyValue<K, V>> extends KeyValues<K, V>, Listable<E> {
    
    /**
     * 转换为数组结构的字典
     * 
     * @param uniqueness 是否要求键唯一
     * @return 数组结构的字典实例
     */
    default Dictionary<K, V, E> asArray(boolean uniqueness) {
        return new ArrayDictionary<>(this, uniqueness);
    }

    /**
     * 判断是否为Map结构
     * 
     * @return 默认返回false，由实现类决定
     */
    default boolean isMap() {
        return false;
    }

    /**
     * 转换为Map结构的字典
     * 
     * @param uniqueness 是否要求键唯一
     * @return Map结构的字典实例
     */
    default Dictionary<K, V, E> asMap(boolean uniqueness) {
        return new MapDictionary<>(this, true, uniqueness);
    }

    /**
     * 获取指定索引的元素
     * 
     * @param index 元素索引
     * @return 键值对元素
     */
    default E getElement(int index) {
        return getElements().get(index);
    }

    /**
     * 根据键获取对应的值集合
     * 
     * @param key 查找的键
     * @return 对应的值集合（可能包含多个值，若键不唯一）
     */
    @Override
    default Elements<V> getValues(K key) {
        return getElements().filter((e) -> ObjectUtils.equals(key, e.getKey())).map((e) -> e.getValue());
    }

    /**
     * 判断是否为数组结构
     * 
     * @return 默认返回false，由实现类决定
     */
    default boolean isArray() {
        return false;
    }

    /**
     * 获取所有键的集合
     * 
     * @return 键的元素集合
     */
    @Override
    default Elements<K> keys() {
        return getElements().map((e) -> e.getKey());
    }

    /**
     * 获取元素数量
     * 
     * @return 元素数量
     */
    default int size() {
        return Math.toIntExact(getElements().count());
    }
}