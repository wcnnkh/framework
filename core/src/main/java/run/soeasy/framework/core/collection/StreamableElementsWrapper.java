package run.soeasy.framework.core.collection;

import java.util.function.Consumer;

/**
 * 可流式元素包装器接口，用于将实现了Streamable接口的对象转换为Elements接口实例。
 * 该接口继承自StreamableWrapper和Elements，提供了对底层Streamable对象的透明包装，
 * 支持通过Stream进行元素处理，并可转换为其他Elements接口实现。
 *
 * <p>设计特点：
 * <ul>
 *   <li>继承StreamableWrapper接口，提供对Streamable对象的包装能力</li>
 *   <li>继承Elements接口，支持集合操作和转换</li>
 *   <li>默认方法实现委托给Elements接口的默认实现</li>
 *   <li>类型安全的泛型设计，确保包装器与被包装类型的一致性</li>
 * </ul>
 *
 * <p>使用场景：
 * <ul>
 *   <li>需要将Streamable对象转换为Elements接口实例的场景</li>
 *   <li>需要统一处理不同类型Streamable对象的场景</li>
 *   <li>需要通过流式操作处理元素并转换为其他集合类型的场景</li>
 * </ul>
 *
 * @param <E> 元素类型
 * @param <W> 被包装的源对象类型，必须实现Streamable接口
 * @see Streamable
 * @see StreamableWrapper
 * @see Elements
 */
public interface StreamableElementsWrapper<E, W extends Streamable<E>> extends StreamableWrapper<E, W>, Elements<E> {

    /**
     * 对元素执行遍历操作，委托给Elements接口的默认实现。
     * 该方法会遍历元素并对每个元素执行给定的操作。
     *
     * @param action 对元素执行的操作
     */
    @Override
    default void forEach(Consumer<? super E> action) {
        Elements.super.forEach(action);
    }

    /**
     * 将元素转换为列表形式，委托给Elements接口的默认实现。
     * 该方法返回一个ListElementsWrapper实例，包含当前元素的列表形式。
     *
     * @return 列表元素包装器
     */
    @Override
    default ListElementsWrapper<E, ?> toList() {
        return Elements.super.toList();
    }

    /**
     * 将元素转换为集合形式，委托给Elements接口的默认实现。
     * 该方法返回一个SetElementsWrapper实例，包含当前元素的集合形式。
     *
     * @return 集合元素包装器
     */
    @Override
    default SetElementsWrapper<E, ?> toSet() {
        return Elements.super.toSet();
    }
}