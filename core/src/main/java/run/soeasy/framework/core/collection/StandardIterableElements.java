package run.soeasy.framework.core.collection;

import java.io.Serializable;
import java.util.Objects;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * 标准可迭代元素包装器，用于将任意实现了Iterable接口的对象转换为Elements接口实例。
 * 该类提供了对源Iterable对象的透明包装，实现了Elements接口的所有操作，
 * 同时保持与源对象的一致性，并支持序列化功能。
 *
 * <p>核心特性：
 * <ul>
 *   <li>实现Serializable接口，支持对象序列化</li>
 *   <li>通过@RequiredArgsConstructor自动生成必要的构造函数</li>
 *   <li>使用@NonNull注解确保源对象不为null</li>
 *   <li>基于源对象的equals/hashCode/toString实现相应方法</li>
 * </ul>
 *
 * <p>使用场景：
 * <ul>
 *   <li>将现有Iterable对象转换为Elements接口实例</li>
 *   <li>需要序列化Iterable对象的场景</li>
 *   <li>需要统一处理不同类型Iterable对象的场景</li>
 * </ul>
 *
 * @param <E> 元素类型
 * @param <W> 被包装的源对象类型，必须实现Iterable接口
 * @see Elements
 * @see Iterable
 * @see Serializable
 */
@RequiredArgsConstructor
@Getter
public class StandardIterableElements<E, W extends Iterable<E>> implements IterableElementsWrapper<E, W>, Serializable {
    private static final long serialVersionUID = 1L;
    
    /**
     * 被包装的源Iterable对象，不可为null。
     * 所有操作将委托给该对象执行。
     */
    @NonNull
    private final W source;

    /**
     * 获取元素迭代器，委托给源对象的iterator方法。
     *
     * @return 元素迭代器
     */
    @Override
    public java.util.Iterator<E> iterator() {
        return source.iterator();
    }

    /**
     * 判断元素集合是否为空，基于源对象的iterator().hasNext()实现。
     *
     * @return 如果集合为空返回true，否则返回false
     */
    @Override
    public boolean isEmpty() {
        return !iterator().hasNext();
    }

    /**
     * 返回元素集合的字符串表示形式，委托给源对象的toString方法。
     *
     * @return 元素集合的字符串表示
     */
    @Override
    public String toString() {
        return source.toString();
    }

    /**
     * 判断与另一个对象是否相等，基于源对象的equals方法。
     *
     * @param obj 要比较的对象
     * @return 如果与源对象相等返回true，否则返回false
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        StandardIterableElements<?, ?> that = (StandardIterableElements<?, ?>) obj;
        return source.equals(that.source);
    }

    /**
     * 返回元素集合的哈希码，基于源对象的hashCode方法。
     *
     * @return 哈希码值
     */
    @Override
    public int hashCode() {
        return Objects.hash(source);
    }
}