package run.soeasy.framework.core.collection;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;

import run.soeasy.framework.core.domain.Wrapper;

/**
 * 提供对Iterable对象的包装功能，允许对可迭代对象进行封装并操作原始对象。
 * 实现此接口的类可以将Iterable对象包装起来，提供额外的功能或修改默认行为。
 *
 * @param <E> 迭代元素的类型
 * @param <W> 被包装的Iterable类型
 * 
 * @author soeasy.run
 */
public interface IterableWrapper<E, W extends Iterable<E>> extends Iterable<E>, Wrapper<W> {
    
    /**
     * 返回一个迭代器，用于遍历被包装的Iterable中的元素。
     * 
     * @return 一个迭代器实例
     */
    @Override
    default Iterator<E> iterator() {
        return getSource().iterator();
    }

    /**
     * 对被包装的Iterable中的每个元素执行给定的操作，直到所有元素都被处理或操作抛出异常。
     * 
     * @param action 要对每个元素执行的操作
     */
    @Override
    default void forEach(Consumer<? super E> action) {
        getSource().forEach(action);
    }

    /**
     * 为被包装的Iterable创建一个Spliterator。
     * 
     * @return 一个Spliterator实例
     */
    @Override
    default Spliterator<E> spliterator() {
        return getSource().spliterator();
    }
}