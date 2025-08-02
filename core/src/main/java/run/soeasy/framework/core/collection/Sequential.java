package run.soeasy.framework.core.collection;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 带序列信息的元素包装类，用于在集合遍历过程中提供元素的位置和边界信息。
 * 该类将元素与其在集合中的索引和是否为最后一个元素的标志关联起来，
 * 常用于需要位置信息的迭代或流处理场景。
 *
 * <p>设计特点：
 * <ul>
 *   <li>不可变对象，线程安全</li>
 *   <li>通过索引和last标志提供完整的序列上下文</li>
 *   <li>重写equals和hashCode方法，仅基于element属性</li>
 *   <li>实现Serializable接口，支持对象序列化</li>
 * </ul>
 *
 * <p>使用示例：
 * <pre>{@code
 * List<String> list = Arrays.asList("a", "b", "c");
 * Stream<Sequential<String>> sequentialStream = IntStream.range(0, list.size())
 *     .mapToObj(i -> new Sequential<>(i, list.get(i), i == list.size() - 1));
 *
 * sequentialStream.forEach(s -> {
 *     System.out.println("Index: " + s.getIndex() + ", Element: " + s.getElement());
 *     if (s.isLast()) {
 *         System.out.println("This is the last element");
 *     }
 * });
 * }</pre>
 *
 * @param <E> 元素类型
 * @see Serializable
 */
@Data
@EqualsAndHashCode(of = "element")
@AllArgsConstructor
public final class Sequential<E> implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 元素在集合中的索引位置，从0开始计数。
     */
    private final long index;

    /**
     * 被包装的原始元素。
     */
    private final E element;

    /**
     * 指示该元素是否为集合中的最后一个元素。
     */
    private final boolean last;

    /**
     * 判断当前元素是否有下一个元素。
     * 该方法是isLast()的逻辑取反，用于提供更直观的判断方式。
     *
     * @return 如果不是最后一个元素返回true，否则返回false
     */
    public boolean hasNext() {
        return !last;
    }
}