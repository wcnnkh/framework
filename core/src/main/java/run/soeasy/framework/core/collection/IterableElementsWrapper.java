package run.soeasy.framework.core.collection;

import java.util.Iterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * 可迭代元素包装器接口，继承自Elements和IterableWrapper，用于封装可迭代的元素集合。
 * 实现此接口的类可对Iterable类型的元素集合进行包装，提供统一的元素访问和流操作能力。
 *
 * @author soeasy.run
 * @param <E> 元素类型
 * @param <W> 被包装的Iterable类型
 * @see Elements
 * @see IterableWrapper
 */
public interface IterableElementsWrapper<E, W extends Iterable<E>> extends Elements<E>, IterableWrapper<E, W> {
    
    /**
     * 对元素集合中的每个元素执行指定操作。
     * 该方法继承自Elements接口的默认实现，确保元素处理逻辑的一致性。
     *
     * @param action 要执行的操作，不可为null
     */
    @Override
    default void forEach(Consumer<? super E> action) {
        Elements.super.forEach(action);
    }

    /**
     * 返回元素集合的迭代器。
     * 该方法继承自IterableWrapper接口的默认实现，确保迭代器获取方式的一致性。
     *
     * @return 元素迭代器
     */
    @Override
    default Iterator<E> iterator() {
        return IterableWrapper.super.iterator();
    }

    /**
     * 将元素集合转换为顺序流。
     * 该方法使用Spliterator创建流，确保与Java Stream API的兼容性。
     *
     * @return 元素的顺序流
     */
    @Override
    default Stream<E> stream() {
        return StreamSupport.stream(spliterator(), false);
    }
}