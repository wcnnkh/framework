package run.soeasy.framework.core.collection;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.function.Function;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Enumerable到Iterable的适配器，将基于Enumeration的集合转换为支持Iterator的集合。
 * 该类允许使用Java 5之前的Enumeration接口实现与现代Java集合框架进行交互，
 * 并支持在转换过程中对元素进行类型转换。
 *
 * @author soeasy.run
 * @param <S> 源元素类型
 * @param <T> 目标元素类型
 * @see Enumerable
 * @see Iterable
 * @see Enumeration
 * @see Iterator
 */
@RequiredArgsConstructor
public class EnumerableToIterable<S, T> implements Iterable<T> {
    
    /**
     * 源Enumerable对象，提供Enumeration迭代器。
     * 不可为null，通过构造函数注入。
     */
    @NonNull
    private final Enumerable<? extends S> enumerable;
    
    /**
     * 元素转换器，用于将源元素类型转换为目标元素类型。
     * 不可为null，通过构造函数注入。
     */
    @NonNull
    private final Function<? super S, ? extends T> converter;

    /**
     * 返回适配后的Iterator实例，该Iterator包装了源Enumerable的Enumeration。
     * 迭代过程中会自动应用转换器函数，将源元素转换为目标类型。
     *
     * @return 适配后的Iterator实例
     */
    @Override
    public Iterator<T> iterator() {
        Enumeration<? extends S> enumeration = enumerable.enumeration();
        if (enumeration == null) {
            return null;
        }
        return new EnumerationToIterator<>(enumeration, converter);
    }
}