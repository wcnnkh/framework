package run.soeasy.framework.core.collection;

import java.util.Iterator;
import java.util.NoSuchElementException;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * 序列迭代器实现，用于将普通迭代器转换为带有序列信息的迭代器。
 * 该迭代器为每个元素生成一个Sequential包装对象，包含元素的索引位置和是否为最后一个元素的标识。
 *
 * <p>设计特点：
 * <ul>
 *   <li>包装普通迭代器，为每个元素添加序列上下文信息</li>
 *   <li>自动计算元素索引，并判断是否为最后一个元素</li>
 *   <li>惰性计算，仅在调用next()时生成Sequential对象</li>
 *   <li>严格遵循Iterator接口规范，支持hasNext()和next()操作</li>
 * </ul>
 *
 * <p>使用示例：
 * <pre>{@code
 * List<String> list = Arrays.asList("A", "B", "C");
 * Iterator<Sequential<String>> sequentialIterator = new SequentialIterator<>(list.iterator());
 * 
 * while (sequentialIterator.hasNext()) {
 *     Sequential<String> seq = sequentialIterator.next();
 *     System.out.printf("Index: %d, Element: %s, IsLast: %b%n", 
 *         seq.getIndex(), seq.getElement(), seq.isLast());
 * }
 * // 输出:
 * // Index: 0, Element: A, IsLast: false
 * // Index: 1, Element: B, IsLast: false
 * // Index: 2, Element: C, IsLast: true
 * }</pre>
 *
 * @param <E> 元素类型
 * @see Iterator
 * @see Sequential
 */
@RequiredArgsConstructor
class SequentialIterator<E> implements Iterator<Sequential<E>> {
    
    /** 被包装的原始迭代器 */
    @NonNull
    private final Iterator<? extends E> iterator;
    
    /** 当前元素索引，从0开始递增 */
    private long index = 0;

    /**
     * 判断是否存在下一个元素。
     * 该方法直接委托给原始迭代器的hasNext()方法，确保行为一致性。
     *
     * @return 如果原始迭代器还有元素返回true，否则返回false
     */
    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    /**
     * 获取下一个带序列信息的元素包装对象。
     * 该方法执行以下操作：
     * 1. 检查原始迭代器是否有下一个元素
     * 2. 从原始迭代器获取下一个元素
     * 3. 生成包含当前索引、元素值和是否为最后一个元素标志的Sequential对象
     * 4. 索引自增1
     *
     * @return 包含序列信息的Sequential对象
     * @throws NoSuchElementException 当原始迭代器没有更多元素时抛出
     */
    @Override
    public Sequential<E> next() {
        if (!iterator.hasNext()) {
            throw new NoSuchElementException();
        }

        E value = iterator.next();
        boolean isLast = !iterator.hasNext();
        return new Sequential<>(index++, value, isLast);
    }
}