package run.soeasy.framework.core.collection;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 列表元素包装类，将元素集合转换为列表形式并提供缓存能力。
 * 该类继承自CollectionElements，基于List实现元素存储，
 * 支持列表特有的索引访问和顺序操作，同时利用父类的缓存机制优化性能。
 *
 * <p>核心特性：
 * <ul>
 *   <li>继承CollectionElements的缓存机制和集合操作能力</li>
 *   <li>使用List作为底层存储结构，支持索引访问和顺序操作</li>
 *   <li>通过Collectors.toList()确保元素按顺序收集为不可变列表</li>
 *   <li>实现ListElementsWrapper接口，提供列表专用操作方法</li>
 * </ul>
 *
 * <p>使用场景：
 * <ul>
 *   <li>需要按索引访问元素的场景</li>
 *   <li>需要保持元素插入顺序的场景</li>
 *   <li>需要将流式元素转换为列表并缓存的场景</li>
 * </ul>
 *
 * @param <E> 元素类型
 * @see CollectionElements
 * @see ListElementsWrapper
 */
public class ListElements<E> extends CollectionElements<E, List<E>> implements ListElementsWrapper<E, List<E>> {
    private static final long serialVersionUID = 1L;

    /**
     * 创建列表元素包装实例，将源元素集合转换为列表并缓存。
     * 该构造函数使用Collectors.toList()作为收集器，确保元素按顺序收集为List。
     *
     * @param elements 源元素集合，不可为null
     */
    public ListElements(Elements<E> elements) {
        super(elements, Collectors.toList());
    }
}