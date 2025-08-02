package run.soeasy.framework.core.collection.factory;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

/**
 * 集合工厂接口，继承自CollectionFactory，专门用于创建、展示和克隆Set实例。
 * 该接口优化了集合展示和克隆逻辑，针对Set类型的源集合提供更高效的处理方式，
 * 并确保克隆后的集合容量能够合理避免扩容操作。
 *
 * @author soeasy.run
 * @param <E> 集合元素类型
 * @param <T> 具体Set实现类型
 * @see CollectionFactory
 */
public interface SetFactory<E, T extends Set<E>> extends CollectionFactory<E, T> {
    
    /**
     * 将源集合转换为只读形式的Set，用于安全地对外展示。
     * 如果源集合本身是Set类型，则直接返回其不可修改视图；
     * 否则调用clone方法创建一个新的Set并返回其不可修改视图。
     * 此方法优化了Set类型的处理，避免不必要的克隆操作。
     *
     * @param source 源集合，不可为null
     * @return 只读的Set视图
     */
    @Override
    default Set<E> display(Collection<E> source) {
        return (source instanceof Set) ? 
            Collections.unmodifiableSet((Set<E>) source) : 
            clone(source);
    }

    /**
     * 克隆源集合，创建一个具有相同元素的新Set实例。
     * 该方法根据源集合的大小计算合适的初始容量，
     * 并使用默认负载因子创建新Set，然后复制所有元素。
     * 容量计算公式为：max(源大小, 向上取整((源大小 + 1) * (负载因子 * 2 - 1)))
     * 此公式确保新Set在复制元素后不会立即触发扩容操作。
     *
     * @param source 源集合，不可为null
     * @return 克隆的新Set实例
     */
    @Override
    default T clone(Collection<E> source) {
        int newCapacity = Math.max(source.size(), Math.round((source.size() + 1) * (DEFAULT_LOAD_FACTOR * 2 - 1)));
        T target = createCollection(newCapacity, DEFAULT_LOAD_FACTOR);
        target.addAll(source);
        return target;
    }
}