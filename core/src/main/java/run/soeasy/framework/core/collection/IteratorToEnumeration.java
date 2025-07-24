package run.soeasy.framework.core.collection;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.function.Function;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * 将Java Iterator转换为Enumeration的适配器类。
 * 该类允许使用现代Java集合框架中的Iterator接口与旧的基于Enumeration的API进行交互，
 * 并支持在迭代过程中对元素进行类型转换。
 *
 * @author soeasy.run
 * @param <S> 源元素类型
 * @param <E> 目标元素类型
 * @see Iterator
 * @see Enumeration
 */
@RequiredArgsConstructor
class IteratorToEnumeration<S, E> implements Enumeration<E>, Serializable {
    private static final long serialVersionUID = 1L;
    
    /**
     * 源Iterator对象，不可为null。
     */
    @NonNull
    private final Iterator<? extends S> iterator;
    
    /**
     * 元素转换器，用于将源类型S的元素转换为目标类型E，不可为null。
     */
    @NonNull
    private final Function<? super S, ? extends E> converter;

    /**
     * 判断是否还有下一个元素。
     * 该方法直接代理源Iterator的hasNext()方法。
     *
     * @return 如果还有元素可迭代，返回true；否则返回false
     */
    @Override
    public boolean hasMoreElements() {
        return iterator.hasNext();
    }

    /**
     * 获取下一个元素，并应用转换器进行类型转换。
     *
     * @return 转换后的下一个元素
     * @throws java.util.NoSuchElementException 如果没有更多元素可获取
     */
    @Override
    public E nextElement() {
        S s = iterator.next();
        return converter.apply(s);
    }
}