package run.soeasy.framework.core.collection;

import java.util.Iterator;
import java.util.function.Consumer;
import java.util.function.Function;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * 支持类型转换的迭代器实现，用于将一种类型的元素迭代器转换为另一种类型的元素迭代器。
 * 该类实现了Iterator接口，通过应用给定的转换函数，将源迭代器中的元素转换为目标类型。
 *
 * <p>设计特点：
 * <ul>
 *   <li>支持通过Function函数式接口定义元素转换规则</li>
 *   <li>惰性转换，仅在调用next()或forEachRemaining()时进行转换</li>
 *   <li>完整支持Iterator接口的所有方法，包括remove()操作</li>
 *   <li>转换过程中的空值处理由转换函数决定</li>
 * </ul>
 *
 * <p>使用示例：
 * <pre>{@code
 * Iterator<Integer> intIterator = ...;
 * Iterator<String> strIterator = new ConvertibleIterator<>(
 *     intIterator,
 *     Object::toString
 * );
 * }</pre>
 *
 * @param <S> 源元素类型
 * @param <E> 目标元素类型
 * @see Iterator
 * @see Function
 */
@RequiredArgsConstructor
class ConvertibleIterator<S, E> implements Iterator<E> {

    /** 被转换的源迭代器 */
    @NonNull
    private final Iterator<? extends S> iterator;

    /** 元素转换函数，将源类型元素转换为目标类型元素 */
    @NonNull
    private final Function<? super S, ? extends E> converter;

    /**
     * 判断迭代器中是否还有更多元素。
     * 该方法直接委托给源迭代器的对应方法实现。
     *
     * @return 如果迭代器中还有更多元素返回true，否则返回false
     */
    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    /**
     * 获取下一个元素，并将其转换为目标类型。
     * 该方法从源迭代器获取下一个元素，然后应用转换函数进行类型转换。
     *
     * @return 转换后的下一个元素
     * @throws java.util.NoSuchElementException 如果没有更多元素可用
     */
    @Override
    public E next() {
        S s = iterator.next();
        return converter.apply(s);
    }

    /**
     * 从底层集合中移除最后一个返回的元素。
     * 该方法直接委托给源迭代器的对应方法实现。
     * 注意：此操作会影响源集合，调用前应确保源迭代器支持remove()操作。
     *
     * @throws UnsupportedOperationException 如果源迭代器不支持remove操作
     * @throws IllegalStateException         如果next方法尚未调用，或者remove方法已在最后一次调用next之后被调用
     */
    @Override
    public void remove() {
        iterator.remove();
    }

    /**
     * 对剩余的每个元素执行给定的操作，直到所有元素都被处理或操作抛出异常。
     * 该方法会先将源元素转换为目标类型，再应用给定的消费函数。
     *
     * @param action 要对每个元素执行的操作
     * @throws NullPointerException 如果指定的操作为null
     */
    @Override
    public void forEachRemaining(@NonNull Consumer<? super E> action) {
        iterator.forEachRemaining((s) -> {
            E e = converter.apply(s);
            action.accept(e);
        });
    }
}