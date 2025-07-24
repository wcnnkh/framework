package run.soeasy.framework.core.collection;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * 标准可流式元素包装器，用于将实现了Streamable接口的对象转换为Elements接口实例。
 * 该类提供了对源Streamable对象的透明包装，实现了Iterable和Streamable接口的所有操作，
 * 支持通过Stream进行元素处理，并可转换为迭代器进行遍历。
 *
 * <p>核心特性：
 * <ul>
 *   <li>基于源对象的Stream实现元素处理和迭代</li>
 *   <li>惰性求值：仅在需要时执行Stream操作</li>
 *   <li>线程安全：如果源Streamable是线程安全的，则包装器也是线程安全的</li>
 *   <li>支持链式操作：可与其他Streamable/Elements实现无缝衔接</li>
 * </ul>
 *
 * <p>使用场景：
 * <ul>
 *   <li>将Streamable对象转换为Elements接口实例</li>
 *   <li>需要统一处理不同类型Streamable对象的场景</li>
 *   <li>需要同时支持迭代器和流式操作的场景</li>
 * </ul>
 *
 * @param <E> 元素类型
 * @param <W> 被包装的源对象类型，必须实现Streamable接口
 * @see Streamable
 * @see StreamableElementsWrapper
 * @see Elements
 */
@RequiredArgsConstructor
@Getter
public class StandardStreamableElements<E, W extends Streamable<E>> implements StreamableElementsWrapper<E, W> {
    
    /**
     * 被包装的源Streamable对象。
     * 所有操作将委托给该对象执行。
     */
	@NonNull
    private final W source;

    /**
     * 获取元素迭代器。
     * 该方法通过将源对象的Stream收集为List，再获取其迭代器。
     * 注意：每次调用都会重新执行Stream操作，生成新的List。
     *
     * @return 元素迭代器
     */
    @Override
    public Iterator<E> iterator() {
        List<E> list = source.collect(Collectors.toList());
        return list.iterator();
    }

    /**
     * 获取元素流。
     * 该方法直接委托给源对象的stream()方法，保持Stream的惰性求值特性。
     *
     * @return 元素流
     */
    @Override
    public Stream<E> stream() {
        return source.stream();
    }
}