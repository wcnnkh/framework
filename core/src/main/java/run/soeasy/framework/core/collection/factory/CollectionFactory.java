package run.soeasy.framework.core.collection.factory;

import java.util.Collection;
import java.util.Collections;

import run.soeasy.framework.core.domain.ObjectOperator;

/**
 * 集合工厂函数式接口，用于创建、展示和克隆集合实例。
 * 该接口定义了集合的工厂方法，支持自定义初始容量和负载因子，
 * 并提供了默认的创建、展示和克隆实现。
 *
 * @author soeasy.run
 * @param <E> 集合元素类型
 * @param <T> 具体集合实现类型
 * @see ObjectOperator
 */
@FunctionalInterface
public interface CollectionFactory<E, T extends Collection<E>> extends ObjectOperator<Collection<E>> {
    
    /**
     * 默认初始容量，用于创建集合实例。
     * 此值参考了常见集合实现的默认初始容量。
     */
    public static final int DEFAULT_INITIAL_CAPACITY = 16;
    
    /**
     * 默认负载因子，用于创建集合实例。
     * 此值参考了哈希表等实现的默认负载因子，平衡了空间和时间效率。
     */
    public static final float DEFAULT_LOAD_FACTOR = 0.75f;

    /**
     * 创建具有默认初始容量和负载因子的集合实例。
     * 该方法调用createCollection(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR)实现。
     *
     * @return 新创建的集合实例
     */
    @Override
    default T create() {
        return createCollection(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR);
    }

    /**
     * 将源集合转换为只读形式，用于安全地对外展示。
     * 该方法返回源集合的不可修改视图，防止外部修改内部数据。
     *
     * @param source 源集合，不可为null
     * @return 只读的集合视图
     */
    @Override
    default Collection<E> display(Collection<E> source) {
        return Collections.unmodifiableCollection(source);
    }

    /**
     * 克隆源集合，创建一个具有相同元素的新集合实例。
     * 该方法根据源集合的大小设置初始容量，使用默认负载因子创建新集合，
     * 然后复制所有元素。
     *
     * @param source 源集合，不可为null
     * @return 克隆的新集合实例
     */
    @Override
    default T clone(Collection<E> source) {
        T target = createCollection(source.size(), DEFAULT_LOAD_FACTOR);
        target.addAll(source);
        return target;
    }

    /**
     * 使用指定的初始容量和负载因子创建集合实例。
     * 这是CollectionFactory接口的核心方法，具体实现应返回适当类型的集合。
     * 注意：对于某些不使用负载因子的集合实现，该参数可能被忽略。
     *
     * @param initialCapacity 初始容量，必须大于0
     * @param loadFactor      负载因子，必须大于0
     * @return 新创建的集合实例
     */
    T createCollection(int initialCapacity, float loadFactor);
}