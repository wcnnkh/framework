package run.soeasy.framework.core.collection;

import java.util.Enumeration;
import java.util.function.Function;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * 支持类型转换的枚举器包装器，用于将一种类型的枚举元素转换为另一种类型。
 * 该类实现了Enumeration接口，通过应用给定的转换函数，将源枚举器中的元素转换为目标类型。
 *
 * <p>设计特点：
 * <ul>
 *   <li>支持通过Function函数式接口定义元素转换规则</li>
 *   <li>惰性转换，仅在调用nextElement()时进行转换</li>
 *   <li>支持空值转换，当源元素为null时返回null</li>
 *   <li>提供静态工厂方法简化常用转换操作</li>
 * </ul>
 *
 * <p>使用示例：
 * <pre>{@code
 * Enumeration<Integer> intEnum = ...;
 * Enumeration<String> strEnum = new ConvertibleEnumeration<>(
 *     intEnum, 
 *     Object::toString
 * );
 * }</pre>
 *
 * @param <T> 源元素类型
 * @param <E> 目标元素类型
 * @see Enumeration
 * @see Function
 */
@RequiredArgsConstructor
class ConvertibleEnumeration<T, E> implements Enumeration<E> {

    /** 被转换的源枚举器 */
    @NonNull
    private final Enumeration<? extends T> enumeration;

    /** 元素转换函数，将源类型元素转换为目标类型元素 */
    @NonNull
    private final Function<? super T, ? extends E> converter;

    /**
     * 判断枚举器中是否还有更多元素。
     * 该方法直接委托给源枚举器的对应方法实现。
     *
     * @return 如果枚举器中还有更多元素返回true，否则返回false
     */
    @Override
    public boolean hasMoreElements() {
        return enumeration.hasMoreElements();
    }

    /**
     * 获取下一个元素，并将其转换为目标类型。
     * 如果源元素为null，则直接返回null而不应用转换函数。
     *
     * @return 转换后的下一个元素
     * @throws java.util.NoSuchElementException 如果没有更多元素可用
     */
    @Override
    public E nextElement() {
        T v = enumeration.nextElement();
        if (v == null) {
            return null;
        }

        return converter.apply(v);
    }

    /**
     * 静态工厂方法，将任意类型的枚举器转换为字符串枚举器。
     * 该方法使用String.valueOf()作为转换函数，确保所有非空元素都被转换为字符串。
     *
     * @param enumeration 源枚举器
     * @return 字符串类型的枚举器
     */
    public static Enumeration<String> convertToStringEnumeration(Enumeration<?> enumeration) {
        return new ConvertibleEnumeration<Object, String>(enumeration, (k) -> String.valueOf(k));
    }
}