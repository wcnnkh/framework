package run.soeasy.framework.core.collection;

import java.util.List;

import lombok.NonNull;

/**
 * 标准列表元素包装器，用于将实现了List接口的对象转换为ListElements接口实例。
 * 该类继承自StandardCollectionElements，提供了对底层List对象的透明包装，
 * 支持列表的有序性、索引访问和所有列表特有的操作。
 *
 * <p>设计特点：
 * <ul>
 *   <li>继承StandardCollectionElements的核心功能，专注于List特性</li>
 *   <li>保持与源List对象的一致性，不额外维护元素顺序</li>
 *   <li>通过List接口约束元素有序性，符合Java集合框架设计</li>
 *   <li>实现ListElementsWrapper接口，提供列表专用操作方法</li>
 * </ul>
 *
 * <p>使用场景：
 * <ul>
 *   <li>需要将List对象转换为ListElements接口实例的场景</li>
 *   <li>需要统一处理不同类型List对象的场景</li>
 *   <li>需要在保持元素有序性的同时进行列表操作的场景</li>
 * </ul>
 *
 * @param <E> 元素类型
 * @param <W> 被包装的源对象类型，必须实现List接口
 * @see StandardCollectionElements
 * @see ListElementsWrapper
 * @see List
 */
public class StandardListElements<E, W extends List<E>> extends StandardCollectionElements<E, W>
        implements ListElementsWrapper<E, W> {
    private static final long serialVersionUID = 1L;

    /**
     * 创建标准列表元素包装实例，将源List对象包装为ListElements接口实例。
     *
     * @param source 被包装的源List对象，不可为null
     */
    public StandardListElements(@NonNull W source) {
        super(source);
    }
}