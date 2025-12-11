package run.soeasy.framework.core.collection;

import java.util.Collections;
import java.util.Map;

import run.soeasy.framework.core.domain.ObjectOperator;

/**
 * 映射工厂函数式接口，用于创建、展示和克隆Map实例。
 * 该接口定义了Map的工厂方法，支持自定义初始容量和负载因子，
 * 并提供了默认的创建、展示和克隆实现。
 *
 * @author soeasy.run
 * @param <K> 键类型
 * @param <V> 值类型
 * @param <T> 具体Map实现类型
 * @see ObjectOperator
 */
@FunctionalInterface
public interface MapFactory<K, V, T extends Map<K, V>> extends ObjectOperator<Map<K, V>> {
    
    /**
     * 默认初始容量，用于创建Map实例。
     * 此值参考了HashMap的默认初始容量。
     */
    public static final int DEFAULT_INITIAL_CAPACITY = 16;
    
    /**
     * 默认负载因子，用于创建Map实例。
     * 此值参考了HashMap的默认负载因子，平衡了空间和时间效率。
     */
    public static final float DEFAULT_LOAD_FACTOR = 0.75f;

    /**
     * 创建具有默认初始容量和负载因子的Map实例。
     * 该方法调用createMap(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR)实现。
     *
     * @return 新创建的Map实例
     */
    @Override
    default T create() {
        return createMap(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR);
    }

    /**
     * 将源Map转换为只读形式，用于安全地对外展示。
     * 该方法返回源Map的不可修改视图，防止外部修改内部数据。
     *
     * @param source 源Map，不可为null
     * @return 只读的Map视图
     */
    @Override
    default Map<K, V> display(Map<K, V> source) {
        return Collections.unmodifiableMap(source);
    }

    /**
     * 克隆源Map，创建一个具有相同键值对的新Map实例。
     * 该方法根据源Map的大小计算合适的初始容量，
     * 并使用默认负载因子创建新Map，然后复制所有键值对。
     *
     * @param source 源Map，不可为null
     * @return 克隆的新Map实例
     */
    @Override
    default T clone(Map<K, V> source) {
        int newCapacity = Math.max(source.size(), Math.round((source.size() + 1) * (DEFAULT_LOAD_FACTOR * 2 - 1)));
        T target = createMap(newCapacity, DEFAULT_LOAD_FACTOR);
        target.putAll(source);
        return target;
    }

    /**
     * 使用指定的初始容量和负载因子创建Map实例。
     * 这是MapFactory接口的核心方法，具体实现应返回适当类型的Map。
     *
     * @param initialCapacity 初始容量，必须大于0
     * @param loadFactor      负载因子，必须大于0且小于等于1
     * @return 新创建的Map实例
     */
    T createMap(int initialCapacity, float loadFactor);
}