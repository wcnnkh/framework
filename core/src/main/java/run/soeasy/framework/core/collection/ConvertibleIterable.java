package run.soeasy.framework.core.collection;

import java.util.Collections;
import java.util.Iterator;
import java.util.function.Function;

/**
 * 支持类型转换的可迭代对象实现，用于将一种类型的元素集合转换为另一种类型的元素集合。
 * 该类实现了Iterable接口，通过应用给定的转换函数，将源可迭代对象中的元素转换为目标类型。
 *
 * <p>设计特点：
 * <ul>
 *   <li>支持通过Function函数式接口定义元素转换规则</li>
 *   <li>惰性转换，仅在迭代时进行元素转换</li>
 *   <li>空安全设计，当源可迭代对象为null时返回空迭代器</li>
 *   <li>基于Iterator接口实现，支持标准的迭代操作</li>
 * </ul>
 *
 * <p>使用示例：
 * <pre>{@code
 * List<Integer> integers = Arrays.asList(1, 2, 3);
 * Iterable<String> strings = new ConvertibleIterable<>(
 *     integers,
 *     Object::toString
 * );
 * }</pre>
 *
 * @param <T> 源元素类型
 * @param <V> 目标元素类型
 * @see Iterable
 * @see Iterator
 * @see Function
 */
public class ConvertibleIterable<T, V> implements Iterable<V> {

    /** 被转换的源可迭代对象 */
    private final Iterable<? extends T> iterable;

    /** 元素转换函数，将源类型元素转换为目标类型元素 */
    private final Function<T, V> converter;

    /**
     * 构造函数，创建一个转换可迭代对象实例。
     *
     * @param iterable  源可迭代对象
     * @param converter 元素转换函数
     */
    public ConvertibleIterable(Iterable<? extends T> iterable, Function<T, V> converter) {
        this.iterable = iterable;
        this.converter = converter;
    }

    /**
     * 获取转换后的元素迭代器。
     * 该方法返回一个ConvertibleIterator实例，实现元素的惰性转换。
     * 如果源可迭代对象为null，则返回一个空迭代器。
     *
     * @return 转换后的元素迭代器
     */
    @Override
    public Iterator<V> iterator() {
        if (iterable == null) {
            return Collections.emptyIterator();
        }

        return new ConvertibleIterator<T, V>(iterable.iterator(), converter);
    }
}