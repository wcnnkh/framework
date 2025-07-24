package run.soeasy.framework.core.collection;

import java.io.Serializable;
import java.util.Collection;

import lombok.NonNull;

/**
 * 函数式接口，定义可列表化元素集合的规范。
 * 实现此接口的类能够将元素集合转换为列表形式，支持流式操作和集合转换。
 * 
 * @author soeasy.run
 * @param <E> 元素类型
 * @see Elements
 */
@FunctionalInterface
public interface Listable<E> {
    
    /**
     * 获取空的Listable实例，对应空元素集合。
     * 该实例为类型安全的单例，推荐直接使用以避免重复创建。
     *
     * @param <E> 元素类型
     * @return 空的Listable实例
     */
    @SuppressWarnings("unchecked")
    public static <E> Listable<E> empty() {
        return (Listable<E>) EmptyElements.EMPTY_ELEMENTS;
    }

    /**
     * 基于已有元素集合创建Listable实例，返回的实例同时实现Serializable接口。
     * 通过接口组合语法（Listable & Serializable）确保实例可序列化。
     *
     * @param <E> 元素类型
     * @param elements 元素集合，不可为null
     * @return 可序列化的Listable实例
     */
    @SuppressWarnings("unchecked")
    public static <E> Listable<E> forElements(@NonNull Elements<E> elements) {
        return (Listable<E> & Serializable) () -> elements;
    }

    /**
     * 基于Java Collection创建Listable实例，返回的实例可序列化。
     * 内部通过Elements.of()转换为Elements，再封装为可序列化的Listable。
     *
     * @param <E> 元素类型
     * @param collection Java Collection实例，不可为null
     * @return 可序列化的Listable实例
     */
    public static <E> Listable<E> forCollection(@NonNull Collection<E> collection) {
        return forElements(Elements.of(collection));
    }

    /**
     * 获取元素集合的实例。
     * 该方法为接口的抽象方法，实现类需返回实际的元素集合。
     *
     * @return 元素集合实例
     */
    Elements<E> getElements();

    /**
     * 判断元素集合是否包含元素。
     * 内部调用getElements().isEmpty()，返回相反逻辑（true表示无元素）。
     *
     * @return true表示元素集合为空，false表示包含元素
     */
    default boolean hasElements() {
        return getElements().isEmpty();
    }
}