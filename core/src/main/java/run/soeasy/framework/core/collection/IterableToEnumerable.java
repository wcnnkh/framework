package run.soeasy.framework.core.collection;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.function.Function;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * 将Java Iterable转换为Enumeration的适配器类。
 * 该类允许使用现代Java集合框架中的Iterable接口与旧的基于Enumeration的API进行交互，
 * 并支持在迭代过程中对元素进行类型转换。
 *
 * @author soeasy.run
 * @param <S> 源元素类型
 * @param <E> 目标元素类型
 * @see Iterable
 * @see Enumeration
 */
@RequiredArgsConstructor
class IterableToEnumerable<S, E> implements Enumerable<E> {
    
    /**
     * 源Iterable对象，不可为null。
     */
    @NonNull
    private final Iterable<? extends S> iterable;
    
    /**
     * 元素转换器，用于将源类型S的元素转换为目标类型E，不可为null。
     */
    @NonNull
    private final Function<? super S, ? extends E> converter;

    /**
     * 返回适配后的Enumeration实例，该Enumeration包装了源Iterable的Iterator。
     * 迭代过程中会自动应用转换器函数，将源元素转换为目标类型。
     *
     * @return 适配后的Enumeration实例
     */
    @Override
    public Enumeration<E> enumeration() {
        Iterator<? extends S> iterator = iterable.iterator();
        if (iterator == null) {
            return null;
        }

        return new IteratorToEnumeration<>(iterator, converter);
    }
}