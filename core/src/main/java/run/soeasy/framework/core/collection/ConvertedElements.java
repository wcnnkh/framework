package run.soeasy.framework.core.collection;

import java.util.function.Function;
import java.util.stream.Stream;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * 元素转换器实现，用于将一种类型的元素集合转换为另一种类型的元素集合。
 * 该类通过应用给定的转换函数，实现对源元素集合的惰性转换，支持在转换过程中控制元素数量和结构。
 *
 * <p>设计特点：
 * <ul>
 *   <li>支持通过Function函数式接口定义元素转换规则</li>
 *   <li>提供resize标志控制转换后元素数量是否动态调整</li>
 *   <li>实现ElementsWrapper接口，提供统一的元素集合操作</li>
 *   <li>基于Stream流处理，支持惰性计算和并行操作</li>
 * </ul>
 *
 * <p>使用示例：
 * <pre>{@code
 * Elements<String> stringElements = ...;
 * Elements<Integer> intElements = new ConvertedElements<>(
 *     stringElements, 
 *     false, 
 *     stream -> stream.map(Integer::parseInt)
 * );
 * }</pre>
 *
 * @param <S> 源元素类型
 * @param <E> 目标元素类型
 * @param <W> 源元素集合类型，必须实现Elements&lt;S&gt;接口
 * @see ElementsWrapper
 * @see Function
 * @see Stream
 */
@RequiredArgsConstructor
@Getter
public class ConvertedElements<S, E, W extends Elements<S>> implements ElementsWrapper<E, Elements<E>> {

    /** 被转换的源元素集合 */
    @NonNull
    private final W target;

    /** 是否在转换后重新计算元素数量 */
    private final boolean resize;

    /** 元素转换函数，将源元素流转换为目标元素流 */
    @NonNull
    private final Function<? super Stream<S>, ? extends Stream<E>> converter;

    /**
     * 获取转换后的元素集合。
     * 该方法通过应用转换函数实现源元素到目标元素的转换，
     * 并返回一个新的Elements实例，支持流式操作。
     *
     * @return 转换后的元素集合
     */
    @Override
    public Elements<E> getSource() {
        return Elements.of(() -> converter.apply(target.stream()));
    }

    /**
     * 返回元素数量。
     * 当resize标志为true时，会重新计算转换后的元素数量；
     * 当resize标志为false时，直接返回源集合的元素数量，避免重复计算。
     *
     * @return 元素数量
     */
    @Override
    public long count() {
        return resize ? ElementsWrapper.super.count() : target.count();
    }

    /**
     * 判断集合是否为空。
     * 当resize标志为true时，会检查转换后的元素流是否为空；
     * 当resize标志为false时，直接使用源集合的isEmpty判断，提高性能。
     *
     * @return 如果集合为空返回true，否则返回false
     */
    @Override
    public boolean isEmpty() {
        return resize ? ElementsWrapper.super.isEmpty() : target.isEmpty();
    }

    /**
     * 判断集合是否只包含一个元素。
     * 当resize标志为true时，会检查转换后的元素流是否只有一个元素；
     * 当resize标志为false时，直接使用源集合的isUnique判断，提高性能。
     *
     * @return 如果集合只包含一个元素返回true，否则返回false
     */
    @Override
    public boolean isUnique() {
        return resize ? ElementsWrapper.super.isUnique() : target.isUnique();
    }
}