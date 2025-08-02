package run.soeasy.framework.core.collection;

import java.util.Set;

import lombok.NonNull;

/**
 * 标准集合元素包装器，用于将实现了Set接口的对象转换为SetElements接口实例。
 * 该类继承自StandardCollectionElements，提供了对底层Set对象的透明包装，
 * 确保元素的唯一性，并支持集合专用的操作。
 *
 * <p>设计特点：
 * <ul>
 *   <li>继承StandardCollectionElements的核心功能，专注于Set特性</li>
 *   <li>保持与源Set对象的一致性，不额外维护元素唯一性</li>
 *   <li>通过Set接口约束元素唯一性，符合Java集合框架设计</li>
 *   <li>实现SetElementsWrapper接口，提供集合专用操作方法</li>
 * </ul>
 *
 * <p>使用场景：
 * <ul>
 *   <li>需要将Set对象转换为SetElements接口实例的场景</li>
 *   <li>需要统一处理不同类型Set对象的场景</li>
 *   <li>需要在保持元素唯一性的同时进行集合操作的场景</li>
 * </ul>
 *
 * @param <E> 元素类型
 * @param <W> 被包装的源对象类型，必须实现Set接口
 * @see StandardCollectionElements
 * @see SetElementsWrapper
 * @see Set
 */
public class StandardSetElements<E, W extends Set<E>> extends StandardCollectionElements<E, W>
        implements SetElementsWrapper<E, W> {
    private static final long serialVersionUID = 1L;

    /**
     * 创建标准集合元素包装实例，将源Set对象包装为SetElements接口实例。
     *
     * @param source 被包装的源Set对象，不可为null
     */
    public StandardSetElements(@NonNull W source) {
        super(source);
    }
}