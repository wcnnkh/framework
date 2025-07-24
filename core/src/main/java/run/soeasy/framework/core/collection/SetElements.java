package run.soeasy.framework.core.collection;

import java.util.Set;
import java.util.stream.Collectors;

import lombok.NonNull;

/**
 * 集合元素包装类，将元素集合转换为Set形式并提供缓存能力。
 * 该类继承自CollectionElements，基于Set实现元素存储，
 * 确保元素唯一性，同时利用父类的缓存机制优化性能。
 *
 * <p>核心特性：
 * <ul>
 *   <li>继承CollectionElements的缓存机制和集合操作能力</li>
 *   <li>使用Set作为底层存储结构，确保元素唯一性</li>
 *   <li>通过Collectors.toSet()确保元素收集为不可变Set</li>
 *   <li>实现SetElementsWrapper接口，提供集合专用操作方法</li>
 * </ul>
 *
 * <p>使用场景：
 * <ul>
 *   <li>需要元素唯一性校验的场景</li>
 *   <li>需要快速判断元素存在性的场景</li>
 *   <li>需要将流式元素转换为Set并缓存的场景</li>
 * </ul>
 *
 * @param <E> 元素类型
 * @see CollectionElements
 * @see SetElementsWrapper
 */
public class SetElements<E> extends CollectionElements<E, Set<E>> implements SetElementsWrapper<E, Set<E>> {
    private static final long serialVersionUID = 1L;

    /**
     * 创建集合元素包装实例，将源元素集合转换为Set并缓存。
     * 该构造函数使用Collectors.toSet()作为收集器，确保元素唯一性。
     *
     * @param elements 源元素集合，不可为null
     */
    public SetElements(@NonNull Elements<E> elements) {
        super(elements, Collectors.toSet());
    }
}