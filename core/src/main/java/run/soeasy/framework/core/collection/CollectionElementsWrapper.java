package run.soeasy.framework.core.collection;

import java.util.Collection;
import java.util.Iterator;
import java.util.function.IntFunction;
import java.util.stream.Stream;

/**
 * 集合元素包装器接口，继承自CollectionWrapper和IterableElementsWrapper，
 * 用于提供对集合元素的统一操作和增强功能。
 * 该接口整合了集合操作、迭代器操作和元素操作的能力，
 * 并通过默认方法实现了对基础集合操作的代理调用。
 *
 * @author soeasy.run
 * @param <E> 元素类型
 * @param <W> 被包装的集合类型，必须实现Collection<E>接口
 * @see CollectionWrapper
 * @see IterableElementsWrapper
 */
public interface CollectionElementsWrapper<E, W extends Collection<E>>
        extends CollectionWrapper<E, W>, IterableElementsWrapper<E, W> {

    /**
     * 判断集合是否包含指定元素。
     * 该方法代理调用被包装集合的contains方法。
     *
     * @param element 待检查的元素
     * @return 如果集合包含该元素，返回true；否则返回false
     */
    @Override
    default boolean contains(Object element) {
        return CollectionWrapper.super.contains(element);
    }

    /**
     * 判断集合是否为空。
     * 该方法代理调用被包装集合的isEmpty方法。
     *
     * @return 如果集合不包含任何元素，返回true；否则返回false
     */
    @Override
    default boolean isEmpty() {
        return CollectionWrapper.super.isEmpty();
    }

    /**
     * 判断集合是否只包含一个唯一元素。
     * 该方法通过比较集合大小是否为1来实现。
     *
     * @return 如果集合大小为1，返回true；否则返回false
     */
    @Override
    default boolean isUnique() {
        return size() == 1;
    }

    /**
     * 获取集合的迭代器。
     * 该方法代理调用被包装集合的iterator方法。
     *
     * @return 集合的迭代器
     */
    @Override
    default Iterator<E> iterator() {
        return CollectionWrapper.super.iterator();
    }

    /**
     * 将集合转换为顺序流。
     * 该方法代理调用被包装集合的stream方法。
     *
     * @return 集合的顺序流
     */
    @Override
    default Stream<E> stream() {
        return CollectionWrapper.super.stream();
    }

    /**
     * 将集合转换为对象数组。
     * 该方法代理调用被包装集合的toArray方法。
     *
     * @return 包含集合所有元素的对象数组
     */
    @Override
    default Object[] toArray() {
        return CollectionWrapper.super.toArray();
    }

    /**
     * 将集合转换为指定类型的数组。
     * 该方法代理调用被包装集合的toArray(T[] array)方法。
     *
     * @param <T>   数组元素类型
     * @param array 目标数组，如果足够大则存储集合元素，否则创建新数组
     * @return 包含集合所有元素的数组
     */
    @Override
    default <T> T[] toArray(T[] array) {
        return CollectionWrapper.super.toArray(array);
    }
    
    /**
     * 使用数组生成器将集合转换为指定类型的数组。
     * 该方法代理调用IterableElementsWrapper的默认实现。
     *
     * @param <A>       数组元素类型
     * @param generator 数组生成器函数
     * @return 包含集合所有元素的数组
     */
    @Override
    default <A> A[] toArray(IntFunction<A[]> generator) {
        return IterableElementsWrapper.super.toArray(generator);
    }
}